#include "contiki.h"
#include "net/rime/rime.h"
#include "dev/motors.h"
#include "dev/leds.h"
#include "dev/button-sensor.h"
#include "sys/node-id.h"
#include <stdio.h>

#define I2C_ADDR 0x66
#define TIMEOUT CLOCK_SECOND
#define RECOVERY (CLOCK_SECOND / 3) * 2
#define PACKET_TYPE_CONTROL 244
#define PACKET_TYPE_HELLO 245
#define CMD_DEV 0
#define CMD_SPEED 1

PROCESS(motor_control_process, "Motor control process");
AUTOSTART_PROCESSES(&motor_control_process);

struct packet {
	uint8_t packet_type;
};

struct packet_control {
	uint8_t packet_type;
	uint16_t seq_no;
	linkaddr_t receiver;
	int8_t left;
	int8_t right;
};

struct packet_hello {
	uint8_t packet_type;
	linkaddr_t sender;
};

static int8_t left, right, active, running;
static uint16_t seq_no;
static uint8_t button_state, packet_count, blocked;
static struct etimer timeout;
static struct abc_conn abc;

static struct packet_hello hello = {.packet_type = PACKET_TYPE_HELLO};

static void abc_recv(struct abc_conn *c)
{
	struct packet *data = packetbuf_dataptr();
	if (data->packet_type != PACKET_TYPE_CONTROL) {
		printf("received some other packet\n");
		return;
	}
	struct packet_control *cmd = packetbuf_dataptr();
	if (cmd->seq_no <= seq_no) {
		printf("received packet number %d but expected at least %d\n", cmd->seq_no, seq_no + 1);
		return;
	}
	seq_no = cmd->seq_no;
	if (cmd->receiver.u16 != linkaddr_node_addr.u16) {
		if (active) {
			printf("forwarding command [%d,%d,%04x,%i,%i]\n", cmd->packet_type, cmd->seq_no, cmd->receiver.u16, cmd->left, cmd->right);
			packetbuf_copyfrom(cmd, sizeof(struct packet_control));
			abc_send(&abc);
		}
		return;
	}
	leds_toggle(LEDS_ALL);
	++packet_count;
	if (left != cmd->left && !blocked) {
		left = cmd->left;
		motor_set_left(left);
	}
	if (right != cmd->right && !blocked) {
		right = cmd->right;
		motor_set_right(-right);
	}
	running = (left || right) && !blocked;
	printf("received command [%d,%d,%04x,%i,%i]\n", cmd->packet_type, cmd->seq_no, cmd->receiver.u16, cmd->left, cmd->right);
}

static const struct abc_callbacks abc_call = {abc_recv};


PROCESS_THREAD(motor_control_process, ev, data)
{
	PROCESS_EXITHANDLER(abc_close(&abc);)
	PROCESS_BEGIN();
	SENSORS_ACTIVATE(button_sensor);


	rf230_set_txpower(14);

	left = 0;
	right = 0;
	active = 0;
	running = 0;
	seq_no = 0;
	blocked = 0;
	button_state = 0;
	packet_count = 0;

	motors_init();
	leds_init();
	leds_on(LEDS_ALL);
	etimer_set(&timeout, TIMEOUT);

	hello.sender = linkaddr_node_addr;
	abc_open(&abc, 128, &abc_call);

	while (1)
	{
		PROCESS_YIELD();
		if (etimer_expired(&timeout)) {
			if (blocked) {
				blocked = 0;
			} else if (running && packet_count <= 5) {
				printf("timeout, stopping motors\n");
				motor_set_left(0);
				motor_set_right(0);
				left = 0;
				right = 0;
				running = 0;
				blocked = 1;
			}
			packet_count = 0;
			etimer_restart(&timeout);
		} else if (ev == sensors_event && data == &button_sensor && (button_state = !button_state)) {
			active = 1;
			seq_no = 0;
			printf("sending hello\n");
			packetbuf_copyfrom(&hello, sizeof(struct packet_hello));
			abc_send(&abc);
		}
	}
	PROCESS_END();
}
