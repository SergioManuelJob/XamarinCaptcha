package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.enumerate;

public enum CaptchaValidationResult {
    VALID_IMAGE,
    FAILED_IMAGE(true),
    VALID_AUDIO,
    FAILED_AUDIO(true),
    MISSING_FIELD_NAME(true),
    MISSING_VALUE(true),
    INSUFFICIENT_OPTION_COUNT(true),
    OTHER_ERROR(true);

    boolean failed;

    CaptchaValidationResult() {
        this.failed = false;
    }

    CaptchaValidationResult(boolean failed) {
        this.failed = failed;
    }

    public boolean isFailed() {
        return failed;
    }

    public boolean isValid() {
        return !failed;
    }

}
