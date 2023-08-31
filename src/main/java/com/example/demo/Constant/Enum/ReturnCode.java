package com.example.demo.Constant.Enum;

public enum ReturnCode {
    RC200(200, "OK"),
    RC400(400, "Bad Request"),
    RC401(401, "Unauthorized"),
    RC402(402, "Payment Required"),
    RC403(403, "Forbidden"),
    RC404(404, "Not Found"),
    RC405(405, "Method Not Allowed"),
    RC406(406, "Not Acceptable"),
    RC408(408, "Request Timeout"),
    RC409(409, "Conflict"),
    RC500(500, "Internal Server Error");

    private final int code;
    private final String message;

    ReturnCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
