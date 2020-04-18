package com.taxi.sb.exceptions;

public class JsonResponse {
    String message;
    int httpStatus ;

    public JsonResponse() {}

    public JsonResponse(String message, int httpStatus ) {
        super();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

}