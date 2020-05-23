package com.ozm.tmall.util;

public class Result {
    public static int SUCCESS_CODE = 0;
    public static int FAIL_CODE = 1;

    int code;
    String message;
    Object data;

    private Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Result success() {
        return new Result(SUCCESS_CODE,null,null);
    }
    public static Result success(Object data) {
        return new Result(SUCCESS_CODE,"",data);
    }
    public static Result fail(String message) {
        return new Result(FAIL_CODE,message,null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
