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

#define ABS(x) ((x) >= 0?(x):(-(x)))
static int fd;
static struct uinput_user_dev uidev;
static struct input_event ev;
static int MIN_TIME_DELAY = 5000; // 65 - 5 us
static int MIN_INPUT_GAP = 5;

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
    // TODO : need use wm size get window size to set value
    uidev.absmin[ABS_X] = 0;
    uidev.absmax[ABS_X] = 1280; // wm size 1280x720
    uidev.absmin[ABS_Y] = 0;
    uidev.absmax[ABS_Y] = 720;

    if (write(fd, &uidev, sizeof(uidev)) < 0)
        die("error: write");

    if (ioctl(fd, UI_DEV_CREATE) < 0)
        die("error: ioctl");

    return 0;
}

int sendRel(int dx, int dy) {
    memset(&ev, 0, sizeof(struct input_event));
    ev.type = EV_REL;         //send x coordinates
    ev.code = REL_X;
    ev.value = dx;
    if (write(fd, &ev, sizeof(struct input_event)) < 0)
        die("error: write");

    memset(&ev, 0, sizeof(struct input_event));
    ev.type = EV_REL;         //send x coordinates
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

    return 0;
}

int setMoveRel(int dx, int dy) {
//TODO  use time () to calculate the gap time
    int i, gap;
    useconds_t sleep_time;
    if (dx != 0 || dy != 0) {
        if (0){//(ABS(dx) > MIN_INPUT_GAP) {
            sleep_time = (useconds_t) MIN_TIME_DELAY / (ABS(dx) / MIN_INPUT_GAP);
            LOGD("sleep time :%d\n", sleep_time);
            gap = dx>0?MIN_INPUT_GAP:-MIN_INPUT_GAP;
            for (i = 0; ABS(dx - i) > MIN_INPUT_GAP; i += gap) {
                sendRel(gap, REL_X);
                usleep(sleep_time);
            }
//            if (i != dx) {
//                sendRel(dx>lastX?(dx-i):-(dx-i), REL_X);
//            }
        } else
            sendRel(dx, dy);

//        lastX = dx;
//        lastY = dy;
    }


    return 0;
}

int closeUinput() {
    if (ioctl(fd, UI_DEV_DESTROY) < 0)
        die("error: ioctl");
    close(fd);

    return 0;
}

