#include "contiki.h"
#include <stdio.h> 

#include "dev/rs232.h"
#include "dev/leds.h"
#include "dev/i2c.h"
#include "dev/motors.h"

#define I2C_ADDR 0x66

/*---------------------------------------------------------------------------*/
PROCESS(inga_control_process, "Ingacontrol process");
AUTOSTART_PROCESSES(&inga_control_process);
/*---------------------------------------------------------------------------*/

static int8_t calculateSpeed(uint8_t param) {
	//0 stop
	//1-128 rückwärts
	//129-256 forwärts
	if (param == 0) {
		return 0;
	} else if (param > 128) {
		return (param - 128);
	} else {
		return -param;
	}
}

PROCESS_THREAD(inga_control_process, ev, data) {
	PROCESS_BEGIN();
		i2c_init();
		leds_init();
		leds_on(LEDS_ALL);
		rs232_init(RS232_PORT_1, USART_BAUD_38400, USART_PARITY_NONE | USART_STOP_BITS_1 | USART_DATA_BITS_8);

		static char side = 'x';
		while (1) {
			uint8_t in;
			while (rs232_getByte(&in) < 0)
				PROCESS_PAUSE();
			printf("received %d, %c\n", in, in);

			if (in == 108) //left
				side = 'l';
			else if (in == 114)	//right
				side = 'r';
			else
				side = 'x';

			if (side != 'x') {
				while (rs232_getByte(&in) < 0)
					PROCESS_PAUSE();
				int8_t speed = calculateSpeed(in);
				printf("send command [%c,%i]\n", side, speed);
				motor_set(side, speed);
			}
			PROCESS_PAUSE();
		}

	PROCESS_END();
}
/*---------------------------------------------------------------------------*/
