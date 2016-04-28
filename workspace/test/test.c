#include "contiki.h"
#include "leds.h"
#include "dev/button-sensor.h"
/*---------------------------------------------------------------------------*/
PROCESS(hello_world_process, "Hello world process");
AUTOSTART_PROCESSES(&hello_world_process);
/*---------------------------------------------------------------------------*/
PROCESS_THREAD(hello_world_process, ev, data)
{
	PROCESS_BEGIN();

	leds_init();
	
	SENSORS_ACTIVATE(button_sensor);
	leds_off(LEDS_ALL);
	
	while(1)
	{
		PROCESS_WAIT_EVENT_UNTIL(ev == sensors_event && data== &button_sensor);
		leds_toggle(LEDS_ALL);
	}
	
  PROCESS_END();
}
/*---------------------------------------------------------------------------*/
