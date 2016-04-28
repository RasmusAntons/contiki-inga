#include "contiki.h"
#include "leds.h"
#include "dev/button-sensor.h"
#include "net/rime/rime.h"
#include <stdio.h>

PROCESS(netcounter_thread, "Netcounter Thread");
AUTOSTART_PROCESSES(&netcounter_thread);

void recv(struct abc_conn *c);

static uint8_t led_state = 0;
static uint8_t button_state = 0;
static struct abc_conn conn;
static struct abc_callbacks cb = {recv};

void recv(struct abc_conn *c)
{
	led_state = (led_state + 1) & 3;
	leds_off(~led_state);
	leds_on(led_state);
}

PROCESS_THREAD(netcounter_thread, ev, data)
{
	PROCESS_BEGIN();
	leds_init();
	leds_off(LEDS_ALL);
	
	abc_open(&conn, 128, &cb);
	
	SENSORS_ACTIVATE(button_sensor);
	while(1)
	{
		PROCESS_YIELD();
		if (ev == sensors_event && data == &button_sensor && (button_state = !button_state))
		{
			packetbuf_copyfrom(NULL, 0);
			abc_send(&conn);
		}
		printf("printing this fixes some strange bug, please ignore\n");
	}
	PROCESS_END();
}
