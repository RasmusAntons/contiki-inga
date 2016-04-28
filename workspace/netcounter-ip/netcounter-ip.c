#include "contiki.h"
#include "leds.h"
#include "dev/button-sensor.h"
#include "contiki-net.h"
#include "sys/node-id.h"
#include <stdio.h>

PROCESS(client_thread, "Client Thread");
PROCESS(server_thread, "Server Thread");
AUTOSTART_PROCESSES(&client_thread, &server_thread);

static uint8_t led_state = 0;
static uint8_t button_state = 0;

static struct uip_udp_conn *server_conn,
                           *client_conn;

PROCESS_THREAD(client_thread, ev, data)
{
	uip_ipaddr_t ipaddr;
	
	PROCESS_BEGIN();
	
	if (node_id == 1219)
	{
		uip_ip6addr(&ipaddr, 0xfe80, 0x0000, 0x0000, 0x0000, 0x090b, 0x0bff, 0xfe0f, 0x0b0b);
	}
	else if (node_id == 579)
	{
		uip_ip6addr(&ipaddr, 0xfe80, 0x0000, 0x0000, 0x0000, 0xa557, 0xb7ff, 0xfe10, 0x2dd4);
	}
	
	client_conn = udp_new(&ipaddr, UIP_HTONS(3000), NULL);
	udp_bind(client_conn, UIP_HTONS(3034));
	
	SENSORS_ACTIVATE(button_sensor);
	while (1)
	{
		PROCESS_YIELD();
		if (ev == sensors_event && data == &button_sensor && (button_state = !button_state))
		{
			printf("button\n");
			uip_udp_packet_send(client_conn, ".", 2);
		}
		printf("printing this fixes some strange bug, please ignore\n");
	}
	PROCESS_END();
}

PROCESS_THREAD(server_thread, ev, data)
{
	PROCESS_BEGIN();
	
	leds_init();
	leds_off(LEDS_ALL);
	
	server_conn = udp_new(NULL, UIP_HTONS(0), NULL);
	udp_bind(server_conn, UIP_HTONS(3000));
	
	while (1)
	{
		PROCESS_YIELD();
		if (ev == tcpip_event)
		{
			led_state = (led_state + 1) & 3;
			leds_off(~led_state);
			leds_on(led_state);
		}
	}
	PROCESS_END();
}
