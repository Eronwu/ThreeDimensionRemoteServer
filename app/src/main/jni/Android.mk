LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
                  com_woo_threedimensionremoteserver_RemoteJNI.cpp \
                  uinput.cpp

LOCAL_SHARED_LIBRARIES := \
#	liblog libcutils libutils

LOCAL_LDLIBS :=-llog

LOCAL_CERTIFICATE := platform

LOCAL_MODULE := UInputJNILib


include $(BUILD_SHARED_LIBRARY)
