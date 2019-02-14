package com.jhh.mq.exception;

/**
 * 2018/11/16.
 */
public class MqException extends Exception{

    private static final long serialVersionUID = 1L;
    private int resultCode;

    public MqException(int resultCode, String resultMessage) {
        super(resultMessage);
        this.resultCode = resultCode;

    }

    public int MqException() {
        return resultCode;
    }
}
