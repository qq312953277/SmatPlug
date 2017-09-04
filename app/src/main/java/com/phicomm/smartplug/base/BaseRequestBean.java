package com.phicomm.smartplug.base;

import java.io.Serializable;

public class BaseRequestBean implements Serializable {
    private static final long serialVersionUID = 1L;
    public String code;
    public String message;
    public String access_token;
    public String result;
    public String error;
    public String reason;
    public String token_status;

    public String getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public String getReason() {
        return reason;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_status() {
        return token_status;
    }
}
