#include "contiki.h"
#include "leds.h"
#include "dev/button-sensor.h"
#include "net/rime/rime.h"
#include <stdio.h>

PROCESS(netcounter_thread, "Netcounter Thread");
AUTOSTART_PROCESSES(&netcounter_thread);

void pair(struct abc_conn *c);
void recv(struct unicast_conn *c, const linkaddr_t *from);

static uint8_t led_state = 0;
static uint8_t button_state = 0;
static uint16_t my_link = 0;
static uint16_t receiver_link = 0;
static char net_buffer[120];
static uint8_t net_buffer_len = 0;

static struct abc_conn aconn;
static struct abc_callbacks acb = {pair};
static struct unicast_conn uconn;
static struct unicast_callbacks ucb = {recv};

void pair(struct abc_conn *c)
{
	char *message = (char *) packetbuf_dataptr();
	if (!receiver_link && !strncmp(message, "my_link=", 8))
	{
		receiver_link = atoi(message + 8);
		printf("set receiver_link to %x\n", receiver_link);
	}
}

void recv(struct unicast_conn *c, const linkaddr_t *from)
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
	
	SENSORS_ACTIVATE(button_sensor);
	
	my_link = (linkaddr_node_addr.u8[0] << 8) | linkaddr_node_addr.u8[1];
	
	printf("press button to start pairing process\n");
	PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data == &button_sensor);
	PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data == &button_sensor);
	abc_open(&aconn, 128, &acb);
	static uint8_t attempt = 0;
	static struct etimer et;
	etimer_set(&et, CLOCK_SECOND);
	
	sprintf(net_buffer, "my_link=%d", my_link);
	net_buffer_len = strlen(net_buffer) + 1;
	
	while (attempt < 10)
	{
		PROCESS_YIELD();
		if (etimer_expired(&et))
		{
			etimer_restart(&et);
			printf("attempt %d\n", ++attempt);
			leds_toggle(LEDS_ALL);
			packetbuf_copyfrom(net_buffer, net_buffer_len);
			abc_send(&aconn);
		}
	}
	
	abc_close(&aconn);
	
	if (!receiver_link)
	{
		printf("pairing unsuccessful, restart both nodes\n");
		while (1) PROCESS_YIELD();
	}
	
	leds_off(LEDS_ALL);
	unicast_open(&uconn, 146, &ucb);
	
	while(1)
	{
		PROCESS_YIELD();
		if (ev == sensors_event && data == &button_sensor && (button_state = !button_state))
		{
			linkaddr_t addr;
			addr.u16 = UIP_HTONS(receiver_link);
			packetbuf_copyfrom(".", 2);
			unicast_send(&uconn, &addr);
		}
		printf("printing this fixes some strange bug, please ignore\n");
	}
	PROCESS_END();
}
