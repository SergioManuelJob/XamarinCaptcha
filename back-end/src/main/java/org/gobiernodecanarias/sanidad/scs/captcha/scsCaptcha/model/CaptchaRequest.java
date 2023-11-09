package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model;

import java.io.Serializable;

public class CaptchaRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    private String name;
    private String value;

    public CaptchaRequest() {
        this.name = null;
        this.value = null;
    }

    public CaptchaRequest(String name, String value) {
        this.name = name;
        this.value = value;
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
        return "CaptchaRequest{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
