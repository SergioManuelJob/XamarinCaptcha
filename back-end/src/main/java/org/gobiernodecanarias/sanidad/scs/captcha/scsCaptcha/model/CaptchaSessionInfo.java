package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model;

import java.io.Serializable;
import java.util.List;

public class CaptchaSessionInfo implements Serializable {

    private static final long serialVersionUID = -1L;

    private List<CaptchaAnswer> choices;
    private String validChoice;
    private String fieldName;
    private String audioFieldName;
    private CaptchaAnswer audioAnswer;

    public CaptchaSessionInfo() {
        this.choices = null;
        this.validChoice = null;
        this.fieldName = null;
        this.audioFieldName = null;
        this.audioAnswer = null;
    }

    public CaptchaSessionInfo(List<CaptchaAnswer> choices, String validChoice, String fieldName, String audioFieldName, CaptchaAnswer audioAnswer) {
        this.choices = choices;
        this.validChoice = validChoice;
        this.fieldName = fieldName;
        this.audioFieldName = audioFieldName;
        this.audioAnswer = audioAnswer;
    }

    public List<CaptchaAnswer> getChoices() {
        return choices;
    }

    public void setChoices(List<CaptchaAnswer> choices) {
        this.choices = choices;
    }

    public String getValidChoice() {
        return validChoice;
    }

    public void setValidChoice(String validChoice) {
        this.validChoice = validChoice;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getAudioFieldName() {
        return audioFieldName;
    }

    public void setAudioFieldName(String audioFieldName) {
        this.audioFieldName = audioFieldName;
    }

    public CaptchaAnswer getAudioAnswer() {
        return audioAnswer;
    }

    public void setAudioAnswer(CaptchaAnswer audioAnswer) {
        this.audioAnswer = audioAnswer;
    }

    @Override
    public String toString() {
        return "CaptchaSessionInfo{" +
                "choices=" + choices.size() +
                ", validChoice='" + validChoice + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", audioFieldName='" + audioFieldName + '\'' +
                ", audioAnswer=" + audioAnswer +
                '}';
    }
}
