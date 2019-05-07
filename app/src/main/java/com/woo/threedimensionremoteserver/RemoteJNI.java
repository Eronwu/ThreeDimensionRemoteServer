package com.woo.threedimensionremoteserver;

public class RemoteJNI {

    public native int initVirtualMouse();

    public native int setMoveRel(int x, int y);

    public native int setLeftClick();

    public native int setRightClick();

    public native int setAcurracy(int n);
}
