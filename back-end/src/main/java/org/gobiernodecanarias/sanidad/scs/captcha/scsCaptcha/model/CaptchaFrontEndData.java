package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model;

import java.io.Serializable;
import java.util.List;

public class CaptchaFrontEndData implements Serializable {

    private static final long serialVersionUID = -1L;

    private String imageName;
    private String imageFieldName;
    private List<String> values;
    private String audioFieldName;

    public CaptchaFrontEndData() {
        this.imageName = null;
        this.imageFieldName = null;
        this.values = null;
        this.audioFieldName = null;
    }

    public CaptchaFrontEndData(String imageName, String imageFieldName, List<String> values, String audioFieldName) {
        this.imageName = imageName;
        this.imageFieldName = imageFieldName;
        this.values = values;
        this.audioFieldName = audioFieldName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageFieldName() {
        return imageFieldName;
    }

    public void setImageFieldName(String imageFieldName) {
        this.imageFieldName = imageFieldName;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getAudioFieldName() {
        return audioFieldName;
    }

    public void setAudioFieldName(String audioFieldName) {
        this.audioFieldName = audioFieldName;
    }

    @Override
    public String toString() {
        return "CaptchaFrontEndData{" +
                "imageName='" + imageName + '\'' +
                ", imageFieldName='" + imageFieldName + '\'' +
                ", values=" + values.size() +
                ", audioFieldName='" + audioFieldName + '\'' +
                '}';
    }
}
