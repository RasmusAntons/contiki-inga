#include "contiki.h"
#include <stdio.h> 
#include "net/rime/rime.h"
#include "dev/leds.h"
#include "dev/motors.h"

#define I2C_ADDR 0x66
#define RIME_ADDR_RELAY 0x0243

#define CMD_DEV 0
#define CMD_SPEED 1

PROCESS(motor_control_process, "Motor control process");
AUTOSTART_PROCESSES(&motor_control_process);

void recv(struct runicast_conn *c, const linkaddr_t *from, uint8_t seqno);

static struct runicast_conn uconn;
static struct runicast_callbacks ucb = {recv};

void recv(struct runicast_conn *c, const linkaddr_t *from, uint8_t seqno)
{
	leds_toggle(LEDS_ALL);
	
	static uint8_t dev;
	static int8_t speed;
	char *message = (char *) packetbuf_dataptr();
	dev = message[CMD_DEV];
	speed = message[CMD_SPEED];
	if (speed == 0) {
		speed = 0;
	} else if (speed > 128) {
		speed = (speed - 128);
	} else {
		speed = -speed;
	}
	if (dev == 'r') speed = -speed;
	
	printf("received command [%c,%i]\n", dev, speed);
	
	motor_set(dev, speed);
}

PROCESS_THREAD(motor_control_process, ev, data) {
	PROCESS_BEGIN();
	
	leds_init();
	leds_on(LEDS_ALL);
	
	runicast_open(&uconn, 146, &ucb);
	
	while (1)
	{
		PROCESS_YIELD();
	}

	PROCESS_END();
}
