//
// Created by woo on 2019/5/8.
//

#include <com_woo_threedimensionremoteserver_RemoteJNI.h>
#include "uinput.h"

/*
#ifdef __cplusplus
extern "C" {
#endif
*/
JNIEXPORT jint JNICALL Java_com_woo_threedimensionremoteserver_RemoteJNI_initVirtualMouse
        (JNIEnv *, jobject) {
    return initUinput();
}

/*
 * Class:     com_woo_threedimensionremoteserver_RemoteJNI
 * Method:    setMoveRel
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_com_woo_threedimensionremoteserver_RemoteJNI_setMoveRel
        (JNIEnv *, jobject, jint x, jint y) {
    return setMoveRel(x, y);
}
/*
 * Class:     com_woo_threedimensionremoteserver_RemoteJNI
 * Method:    setLeftClick
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_woo_threedimensionremoteserver_RemoteJNI_setLeftClick
        (JNIEnv *, jobject) {

    return setLeftClick();
}

/*
 * Class:     com_woo_threedimensionremoteserver_RemoteJNI
 * Method:    setRightClick
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_woo_threedimensionremoteserver_RemoteJNI_setRightClick
        (JNIEnv *, jobject) {

    return setRightClick();
}

/*
 * Class:     com_woo_threedimensionremoteserver_RemoteJNI
 * Method:    setAcurracy
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_com_woo_threedimensionremoteserver_RemoteJNI_setAcurracy
        (JNIEnv *, jobject, jint) {

    return 0;
}

JNIEXPORT jint JNICALL Java_com_woo_threedimensionremoteserver_RemoteJNI_closeVirtualMouse
        (JNIEnv *, jobject) {

    return closeUinput();
}
/*
#ifdef __cplusplus
}
#endif
*/