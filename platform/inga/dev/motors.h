#ifndef MOTORS_H
#define	MOTORS_H
void motors_init();
uint8_t motor_set(char dev, int8_t speed);
uint8_t motor_set_left(int8_t speed);
uint8_t motor_set_right(int8_t speed);
#endif	/* MOTORS_H */
