package com.ningso.ningsodemo.utils;

/**
 * Created by NingSo on 16/4/17.下午6:40
 *
 * @author: NingSo
 * @Email: ningso.ping@gmail.com
 */
public class SDCardException extends Exception {
    public SDCardException() {
        super();
    }

    public SDCardException(String detailMessage) {
        super(detailMessage);
    }

    public SDCardException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
