package com.example.bid_api.util.exception;

import org.springframework.http.HttpStatus;


public class ServiceException extends RuntimeException {

    protected HttpStatus statusCode = HttpStatus.BAD_REQUEST;

    private int code;

    private Object data;


    public ServiceException(int code, String message) {
        super(message);
        this.code = code;

    }
    public ServiceException( String message) {
        super(message);
        this.code = 500;

    }

    public ServiceException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public int getCode() {
        return code;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
