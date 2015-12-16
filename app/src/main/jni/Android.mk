LOCAL_PATH      := $(call my-dir)   ## 定义 LOCAL_PATH 环境变量为本文件的目录，mydir 表示当前目录。
include $(CLEAR_VARS)               ## 清除除了 LOCAL_PATH 以外其他的 LOCAL_ 环境变量
LOCAL_MODULE    := hello            ## 动态库名字为hello
LOCAL_SRC_FILES := hello.c          ## 源文件名字
include $(BUILD_SHARED_LIBRARY)     ## 编译生成共享动态库