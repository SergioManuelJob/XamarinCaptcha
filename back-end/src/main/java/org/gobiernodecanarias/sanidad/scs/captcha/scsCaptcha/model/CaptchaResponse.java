package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model;

import java.io.Serializable;

public class CaptchaResponse implements Serializable {

    private static final long serialVersionUID = -1L;

    private boolean valid;
    private String response;

    public CaptchaResponse() {
        this.valid = false;
        this.response = null;
    }

    public CaptchaResponse(boolean isValid, String response) {
        this.valid = isValid;
        this.response = response;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "CaptchaResponse{" +
                "valid=" + valid +
                ", response='" + response + '\'' +
                '}';
    }
}
