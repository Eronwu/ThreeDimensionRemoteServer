//
// Created by 吴大大 on 2019/5/8.
//

#ifndef THREEDIMENSIONREMOTESERVER_UINPUT_H
#define THREEDIMENSIONREMOTESERVER_UINPUT_H

int initUinput();

int setMoveRel(int dx, int dy);

int closeUinput();

int setLeftClick();

int setRightClick();
#endif //THREEDIMENSIONREMOTESERVER_UINPUT_H
