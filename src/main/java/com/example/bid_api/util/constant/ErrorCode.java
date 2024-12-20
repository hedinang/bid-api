package com.example.bid_api.util.constant;

public enum ErrorCode {
    E401("register fail"),
    E404("Login fail");

    private String describe;

    ErrorCode(String des) {
        this.describe = des;
    }

    /**
     * Get exception code
     *
     * @return
     */
    public int code() {
        return Integer.parseInt(this.name().replaceAll("E", ""));
    }


}
