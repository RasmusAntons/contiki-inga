#include "contiki.h"
#include "net/rime/rime.h"
#include "dev/motors.h"
#include "dev/leds.h"
#include "sys/node-id.h"
#include <stdio.h>

#define I2C_ADDR 0x66
#define TIMEOUT (CLOCK_SECOND * 3) / 2
#define PACKET_TYPE_CONTROL 244
#define CMD_DEV 0
#define CMD_SPEED 1

PROCESS(motor_control_process, "Motor control process");
AUTOSTART_PROCESSES(&motor_control_process);

struct packet {
	uint8_t packet_type;
};

struct packet_control {
	uint8_t packet_type;
	uint8_t seq_no;
	linkaddr_t receiver;
	int8_t left;
	int8_t right;
};

static int8_t left, right, running, seq_no;
static struct timer timeout;
static struct abc_conn abc;

static void abc_recv(struct abc_conn *c)
{
	struct packet *data = packetbuf_dataptr();
	if (data->packet_type != PACKET_TYPE_CONTROL)
		return;
	struct packet_control *cmd = packetbuf_dataptr();
	if (cmd->seq_no <= seq_no && (cmd->seq_no > 0x0F || seq_no < 0xF0))
		return;
	seq_no = cmd->seq_no;
	if (cmd->receiver.u8[0] != linkaddr_node_addr.u8[0] || cmd->receiver.u8[1] != linkaddr_node_addr.u8[1]) {
		printf("forwarding command [%d,%d,%04x,%i,%i]\n", cmd->packet_type, cmd->seq_no, cmd->receiver.u16, cmd->left, cmd->right);
		packetbuf_copyfrom(cmd, sizeof(struct packet_control));
		abc_send(&abc);
		return;
	}
	timer_restart(&timeout);
	leds_toggle(LEDS_ALL);
	if (left != cmd->left) {
		left = cmd->left;
		motor_set_left(left);
	}
	if (right != cmd->right) {
		right = cmd->right;
		motor_set_right(-right);
	}
	running = left || right;
	printf("received command [%d,%d,%04x,%i,%i]\n", cmd->packet_type, cmd->seq_no, cmd->receiver.u16, cmd->left, cmd->right);
}

static const struct abc_callbacks abc_call = {abc_recv};


PROCESS_THREAD(motor_control_process, ev, data)
{
	PROCESS_EXITHANDLER(abc_close(&abc);)
	PROCESS_BEGIN();

	left = 0;
	right = 0;
	running = 0;
	seq_no = 0xFF;

	motors_init();
	leds_init();
	leds_on(LEDS_ALL);
	timer_set(&timeout, TIMEOUT);

	abc_open(&abc, 128, &abc_call);

	while (1)
	{
		PROCESS_PAUSE();
		if (running && timer_expired(&timeout)) {
			printf("timeout, stopping motors\n");
			motor_set_left(0);
			motor_set_right(0);
			running = 0;
		}
	}
	PROCESS_END();
}
