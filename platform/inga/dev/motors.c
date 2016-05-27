#include "contiki.h"
#include "motors.h"
#include "dev/i2c.h"

#define I2C_ADDR 0x66

void motors_init()
{
	i2c_init();
}

uint8_t motor_set(char dev, int8_t speed)
{
	uint8_t success = i2c_start(I2C_ADDR);
	success &= i2c_write(dev);
	success &= i2c_write(speed);
	i2c_stop();
	return success;
}

uint8_t motor_set_left(int8_t speed)
{
	motor_set('l', speed);
}

uint8_t motor_set_right(int8_t speed)
{
	motor_set('r', speed);
}
