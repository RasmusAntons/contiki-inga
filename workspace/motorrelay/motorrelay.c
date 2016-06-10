#include "contiki.h"
#include <stdio.h> 
#include "net/rime/rime.h"
#include "dev/i2c.h"
#include "dev/rs232.h"
#include "dev/leds.h"

#define I2C_ADDR 0x66
#define RIME_ADDR_CONTROLLER 0x04C3

#define CMD_DEV 0
#define CMD_SPEED 1
#define CMD_LENGTH 2

PROCESS(motor_relay_process, "Motor relay process");
AUTOSTART_PROCESSES(&motor_relay_process);

void recv(struct unicast_conn *c, const linkaddr_t *from);

static struct unicast_conn uconn;
static struct unicast_callbacks ucb = {recv};

void recv(struct unicast_conn *c, const linkaddr_t *from)
{
	printf("received message from %d.%d\n", from->u8[0], from->u8[1]);
}

PROCESS_THREAD(motor_relay_process, ev, data) {
	PROCESS_BEGIN();
	
	i2c_init();
	leds_init();
	leds_on(LEDS_ALL);
	rs232_init(RS232_PORT_1, USART_BAUD_38400, USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);
	
	unicast_open(&uconn, 146, &ucb);
	static linkaddr_t addr;
	addr.u16 = UIP_HTONS(RIME_ADDR_CONTROLLER);
	
	static uint8_t cmd[2];
	cmd[CMD_DEV] = 'x';
	while (1) {
		uint8_t in;
		while (rs232_getByte(&in) < 0)
			PROCESS_PAUSE();
		printf("received %d, %c\n", in, in);

		if (in == 108) //left
			cmd[CMD_DEV] = 'l';
		else if (in == 114)	//right
			cmd[CMD_DEV] = 'r';
		else
			cmd[CMD_DEV] = 'x';

		if (cmd[CMD_DEV] != 'x') {
			while (rs232_getByte(cmd + CMD_SPEED) < 0)
				PROCESS_PAUSE();
			printf("send command [%c,%i]\n", cmd[CMD_DEV], cmd[CMD_SPEED]);
			packetbuf_copyfrom(cmd, CMD_LENGTH);
			leds_toggle(LEDS_ALL);
			unicast_send(&uconn, &addr);
		}
		PROCESS_PAUSE();
	}

	PROCESS_END();
}
