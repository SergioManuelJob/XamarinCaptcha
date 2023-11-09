package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model;

import java.io.Serializable;

public class CaptchaAnswer implements Serializable {

    private static final long serialVersionUID = -1L;

    private String name;
    private String path;
    private String obfuscatedName;

    public CaptchaAnswer() {
        this.name = null;
        this.path = null;
        this.obfuscatedName = null;
    }

    public CaptchaAnswer(String name, String obfuscatedName, String path) {
        this.name = name;
        this.obfuscatedName = obfuscatedName;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getObfuscatedName() {
        return obfuscatedName;
    }

    public void setObfuscatedName(String obfuscatedName) {
        this.obfuscatedName = obfuscatedName;
    }

    @Override
    public String toString() {
        return "CaptchaAnswer{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", obfuscatedName='" + obfuscatedName + '\'' +
                '}';
    }
}

