#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/i2c-dev.h>
#include <stdint.h>
#include <inttypes.h>
#include <sys/ioctl.h>
#include <unistd.h>

#define I2C_ADDR 0x33

#define CMD_LEFT    'l'
#define CMD_RIGHT   'r'
#define CMD_S1      '1'
#define CMD_S2      '2'
#define CMD_S3      '3'
#define CMD_ADDR    'a'
#define CMD_STORE   'S'
#define CMD_RESET   'R'

int read_board_rev()
{
	FILE *f;

	f = fopen("/proc/cpuinfo", "r");
	if(f < 0)
	{
		perror("fopen() failed");
		return -1;
	}
    
	while(!feof(f))
	{
		char data[80];
		char *d = fgets(data, sizeof(data), f);
		char key[80], val[80];
		if(d && strlen(d)>1)
		{
			sscanf(d, "%80[^:]: %80[^\n]", key, val);
            
			int len = strlen(key);
            
			while(isspace(key[--len]))
				key[len] = '\0';
            
			if(!strcasecmp(key, "revision"))
			{
				int rev;
				sscanf(val, "%X", &rev);
				return rev;
			}
        	}
	}
	return -1;
}

int set_addr = 0;
int reset = 0;

int main(int argc, char *argv[])
{
	if(argc == 2)
	{
		if(!strcmp(argv[1], "-R"))
		{
			reset = 1;
		}
	}
	else if(argc == 3)
	{
		if(!strcmp(argv[1], "-a"))
		{
			set_addr = 1;
		}
	}
	else if(argc != 6)
	{
		fprintf(stderr, "usage: %s l r a b c\n  where 0 <= [l,r] <= 255\n  and 0 <= [a,b,c] <= 10000\n", argv[0]);
		return -1;
	}
	

	char *i2cfile = "/dev/i2c-0";

	int rev = read_board_rev();
    
	if(rev > 3)
        	i2cfile = "/dev/i2c-1";
	printf("Found board rev 0x%04x, using %s as I2C dev.\n", rev, i2cfile);

	int file;

	file = open(i2cfile, O_RDWR);
	if(file < 0)
	{
		perror("open() failed");
		return -1;
	}

	int ret;

	ret = ioctl(file, I2C_SLAVE, I2C_ADDR);
	if(ret < 0)
	{
		perror("ioctl() failed");
		return -2;
	}

	if(reset)
	{
		uint8_t rstbuf[1] = {CMD_RESET};
	
		write(file, rstbuf, 1);
	}
	if(set_addr)
	{
		int addr;
		ret = sscanf(argv[2], "%02X", &addr);
		if(ret != 1)
		{
			printf("wrong format.\n");
			return -2;
		}
		printf("Setting board address to %02X\n", addr);
		uint8_t adrbuf[10];
		adrbuf[0] = CMD_ADDR;
		adrbuf[1] = (uint8_t)addr;
		
		write(file, adrbuf, 2);

		sleep(1);

		printf("storing adr to eeprom\n");
		adrbuf[0] = CMD_STORE;
		write(file, adrbuf, 1);

		return 0;
	}

	int8_t l = atoi(argv[1]);
	int8_t r = atoi(argv[2]);

	printf("Setting values (l, r) = (%d, %d)\n", l, r); 
        
	uint8_t buff[13];
	buff[0] = CMD_LEFT;
	buff[1] = l;
	
	buff[2] = CMD_RIGHT;
	buff[3] = r;

	ret = write(file, buff, 4);
        if(ret < 0)
	{
		perror("write() failed");
        	return -2;
	}


	uint16_t a, b, c;
	a = atoi(argv[3]);
	b = atoi(argv[4]);
	c = atoi(argv[5]);

	printf("Setting values (a, b, c) = (%d, %d, %d)\n", a, b, c);

	buff[0] = CMD_S1;
	buff[1] = (uint8_t) (a >> 8);
	buff[2] = (uint8_t) a;
	buff[3] = CMD_S2;
	buff[4] = (uint8_t) (b >> 8);
	buff[5] = (uint8_t) b;
	buff[6] = CMD_S3;
	buff[7] = (uint8_t) (c >> 8);
	buff[8] = (uint8_t) c;

        int i;
        printf(" [ ");
        for(i = 0; i < 9; i++)
                printf("%02X ", buff[i]);
        printf("]\n");

	ret = write(file, buff, 9);
	if(ret < 0)
	{
		perror("write() failed");
		return -3;
	}
	return 0;
}



