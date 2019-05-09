#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <linux/input.h>
#include <linux/uinput.h>
#include <time.h>
#include<android/log.h>


#define LOG_TAG "debug"
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##args)

#define die(str, args...) do { \
    LOGE(str); \
    return -1; \
} while(0)

static int fd;
static struct uinput_user_dev uidev;
static struct input_event ev;


int initUinput() {

    fd = open("/dev/uinput", O_WRONLY | O_NONBLOCK);
    if (fd < 0) die("error: open");

    //config uinput working mode,  mouse or touchscreen?  relative coordinates or absolute coordinate?
    if (ioctl(fd, UI_SET_EVBIT, EV_KEY) < 0)         //support key button
        die("error: ioctl");
    if (ioctl(fd, UI_SET_KEYBIT, BTN_LEFT) < 0)  //support mouse left key
        die("error: ioctl");

    if (ioctl(fd, UI_SET_KEYBIT, BTN_RIGHT) < 0)  //support mouse right key
        die("error: ioctl");

    if (ioctl(fd, UI_SET_EVBIT, EV_REL) < 0)       //uinput use relative coordinates
        die("error: ioctl");
    if (ioctl(fd, UI_SET_RELBIT, REL_X) < 0)         //uinput use x coordinates
        die("error: ioctl");
    if (ioctl(fd, UI_SET_RELBIT, REL_Y) < 0)         //uinput use y coordinates
        die("error: ioctl");

    memset(&uidev, 0,
           sizeof(uidev));                  //creat an virtul input device node in /dev/input/***
    snprintf(uidev.name, UINPUT_MAX_NAME_SIZE, "uinput-sample");
    uidev.id.bustype = BUS_USB;
    uidev.id.vendor = 0x1;
    uidev.id.product = 0x1;
    uidev.id.version = 1;

    if (write(fd, &uidev, sizeof(uidev)) < 0)
        die("error: write");

    if (ioctl(fd, UI_DEV_CREATE) < 0)
        die("error: ioctl");

    return 0;
}

int setMoveRel(int dx, int dy) {
    if (dx != 0) {
        memset(&ev, 0, sizeof(struct input_event));
        ev.type = EV_REL;         //send x coordinates
        ev.code = REL_X;
        ev.value = dx;
        if (write(fd, &ev, sizeof(struct input_event)) < 0)
            die("error: write");

        memset(&ev, 0, sizeof(struct input_event));
        ev.type = EV_SYN; // inform input system to process this input event
        ev.code = 0;
        ev.value = 0;
        if (write(fd, &ev, sizeof(struct input_event)) < 0)
            die("error: write");
    }
    if (dy != 0) {
        memset(&ev, 0, sizeof(struct input_event));
        ev.type = EV_REL;  //send y coordinates
        ev.code = REL_Y;
        ev.value = dy;
        if (write(fd, &ev, sizeof(struct input_event)) < 0)
            die("error: write");

        memset(&ev, 0, sizeof(struct input_event));
        ev.type = EV_SYN; // inform input system to process this input event
        ev.code = 0;
        ev.value = 0;
        if (write(fd, &ev, sizeof(struct input_event)) < 0)
            die("error: write");
    }


    return 0;
}

int closeUinput(){
    if(ioctl(fd, UI_DEV_DESTROY) < 0)
        die("error: ioctl");
    close(fd);

    return 0;
}

