package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model;

import java.io.Serializable;

public class CaptchaData implements Serializable {

    private static final long serialVersionUID = -1L;

    private boolean valid;
    private String name;
    private String value;

    public CaptchaData() {
        this.valid = false;
        this.name = null;
        this.value = null;
    }

    public CaptchaData(String name, String value) {
        this.valid = false;
        this.name = name;
        this.value = value;
    }

    public CaptchaData(boolean valid, String name, String value) {
        this.valid = valid;
        this.name = name;
        this.value = value;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "CaptchaData{" +
                "valid=" + valid +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
