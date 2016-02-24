LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/curl


LOCAL_MODULE := data-geter
LOCAL_MODULE_FILENAME := libdatagetter
LOCAL_SRC_FILES := datagetter.cpp

#LOCAL_LDFLAGS += $(LOCAL_PATH)/libcurl.a
LOCAL_LDFLAGS := $(LOCAL_PATH)/lib/$(TARGET_ARCH_ABI)/libcurl.a

LOCAL_LDLIBS += -lm -llog
include $(BUILD_SHARED_LIBRARY)