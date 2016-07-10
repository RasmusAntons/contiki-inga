#include "contiki.h"
#include "net/rime/rime.h"
#include "net/linkaddr.h"
#include "random.h"
#include "dev/leds.h"
#include "dev/rs232.h"
#include <stdio.h>

#define SECRET_MYSTIC_BYTE 243
#define PACKET_TYPE_CONTROL 244
#define TRANSMISSION_DELAY CLOCK_SECOND/20
#define RETRANSMISSION_INTERVAL CLOCK_SECOND
#define CMD_DEV 0
#define CMD_SPEED 1
#define CMD_LENGTH 2

PROCESS(motor_relay_process, "Motor relay process");
AUTOSTART_PROCESSES(&motor_relay_process);

struct packet_control {
	uint8_t packet_type;
	uint8_t seq_no;
	linkaddr_t receiver;
	int8_t left;
	int8_t right;
};

static struct abc_conn abc;

static void abc_recv(struct abc_conn *c)
{
	printf("abc message received\n");
}

static const struct abc_callbacks abc_call = {abc_recv};

PROCESS_THREAD(motor_relay_process, ev, data)
{
	PROCESS_EXITHANDLER(abc_close(&abc);)
	PROCESS_BEGIN();

	leds_init();
	leds_on(LEDS_ALL);

	abc_open(&abc, 128, &abc_call);

	static struct packet_control cmd;
	cmd.packet_type = PACKET_TYPE_CONTROL;
	cmd.seq_no = 0;

	static struct timer retransmission;
	timer_set(&retransmission, RETRANSMISSION_INTERVAL);

	while (1) {
		static uint8_t in;
		in = 0;

		while (rs232_getByte(&in) < 0 && !timer_expired(&retransmission))
			PROCESS_PAUSE();

		if (in == SECRET_MYSTIC_BYTE) {
			while (rs232_getByte(&cmd.receiver.u8[0]) < 0)
				PROCESS_PAUSE();
			while (rs232_getByte(&cmd.receiver.u8[1]) < 0)
				PROCESS_PAUSE();
			while (rs232_getByte((uint8_t *) &cmd.left) < 0)
				PROCESS_PAUSE();
			while (rs232_getByte((uint8_t *) &cmd.right) < 0)
				PROCESS_PAUSE();
		}

		if (in == SECRET_MYSTIC_BYTE || timer_expired(&retransmission)) {
			printf("sending command [%d,%d,%04x,%i,%i]\n", cmd.packet_type, cmd.seq_no, cmd.receiver.u16, cmd.left, cmd.right);
			leds_toggle(LEDS_ALL);
			packetbuf_copyfrom(&cmd, sizeof(struct packet_control));
			abc_send(&abc);
			++cmd.seq_no;
			timer_restart(&retransmission);
			/* wait before allowing the next transmission, should be obsolete with the sequence number
			static struct etimer et;
			etimer_set(&et, TRANSMISSION_DELAY);
			PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&et));
			*/
		}
		PROCESS_PAUSE();
	}
	PROCESS_END();
}
