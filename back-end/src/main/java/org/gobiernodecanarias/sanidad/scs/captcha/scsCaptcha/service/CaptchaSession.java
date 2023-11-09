package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.service;

import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.data.CaptchaRepository;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.enumerate.AudioType;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.enumerate.CaptchaValidationResult;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaAnswer;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaData;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaFrontEndData;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaSessionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.Collections.shuffle;

@Component

//@Scope(value= WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CaptchaSession implements Serializable {

    private static final long serialVersionUID = -1L;

    public static int MIN_OPTION_COUNT = 5;

    private final Logger LOG = LoggerFactory.getLogger(CaptchaSession.class);

    private final Random rand = new Random();
    private CaptchaSessionInfo captchaSessionInfo;

    @Autowired
    private CaptchaRepository captchaRepository;

    public CaptchaFrontEndData start(int howMany) {
        int optionCount = howMany;
        if (howMany < MIN_OPTION_COUNT) {
            optionCount = MIN_OPTION_COUNT;
        }
        LOG.debug("");
        LOG.debug("start() | Se mostrarán {} imágenes", optionCount);

        String salt = UUID.randomUUID().toString();
        List<CaptchaAnswer> choices = getRandomCaptchaOptions(optionCount, salt);
        CaptchaAnswer validChoice = choices.get(rand.nextInt(optionCount));
        CaptchaAnswer audioOption = getRandomCaptchaAudio(salt);
        String fieldName = hash(UUID.randomUUID().toString(), salt);
        String audioFieldName = hash(UUID.randomUUID().toString(), salt);
        this.captchaSessionInfo = new CaptchaSessionInfo(choices, validChoice.getObfuscatedName(), fieldName, audioFieldName, audioOption);
        List<String> frontEndOptions = new ArrayList<>(choices.size());
        for (CaptchaAnswer choice : choices) {
            frontEndOptions.add(choice.getObfuscatedName());
        }
        CaptchaFrontEndData frontendData = new CaptchaFrontEndData(
                validChoice.getName(), fieldName, frontEndOptions, audioFieldName);
        LOG.info("start() | Creada sesión, el campo {} se ha inicializado con {} valores", fieldName, optionCount);
        return frontendData;
    }

    private List<CaptchaAnswer> getRandomCaptchaOptions(int numberOfChoices, String salt) {
        List<CaptchaAnswer> options = new ArrayList<CaptchaAnswer>(captchaRepository.getImages());
        shuffle(options);
        List<CaptchaAnswer> choices = new ArrayList<>(numberOfChoices);
        for (CaptchaAnswer answer : options.subList(0, numberOfChoices)) {
            choices.add(new CaptchaAnswer(answer.getName(), hash(answer.getName(), salt), answer.getPath()));
        }
        shuffle(choices);
        return choices;
    }

    private String hash(String somethingToHash, String salt) {
        String toHash = somethingToHash + salt;
        return DigestUtils.md5DigestAsHex(toHash.getBytes());
    }

    private CaptchaAnswer getRandomCaptchaAudio(String salt) {
        shuffle(captchaRepository.getAudios());
        return captchaRepository.getAudios().get(rand.nextInt(captchaRepository.getAudios().size()));
    }

    public InputStream getImage(int index, boolean retina) {
        LOG.info("");
        LOG.info("getImage() | Comprobando sesión antes de obtener la imagen...");
        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha no ha sido inicializado. Por dicho motivo, no se puede devolver la imagen solicitada.");
        }
        List<CaptchaAnswer> answers = captchaSessionInfo.getChoices();
        if (answers != null && answers.size() > index) {
            CaptchaAnswer ca = answers.get(index);
            LOG.debug("Imagen solicitada {}", ca);
            return captchaRepository.getImageStream(ca.getPath());
        } else {
            throw new RuntimeException("Imagen solicitada para un índice no válido: " + index);
        }
    }

    public InputStream getAudio(AudioType audioType) {
        LOG.info("");
        LOG.info("getAudio() | Comprobando sesión antes de obtener el audio...");
        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha no ha sido inicializado. Por dicho motivo, no se puede devolver el audio solicitado.");
        }
        CaptchaAnswer captchaAnswer = this.captchaSessionInfo.getAudioAnswer();
        if (captchaAnswer == null) {
            throw new RuntimeException("No se ha encontrado la respuesta para el audio solicitado");
        }
        LOG.debug("Audio solicitado {}", captchaAnswer);
        return captchaRepository.getAudioStream(captchaAnswer.getPath(), audioType);
    }

    public CaptchaValidationResult validate(CaptchaData captchaData) {
        LOG.info("");
        LOG.info("validate() | Comprobando sesión antes de validar...");
        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha no ha sido inicializado. Por dicho motivo, no se puede realizar la comprobación del caso de los recursos de imagen");
        }
        String audioFieldName = captchaSessionInfo.getAudioFieldName();
        String imageFieldName = captchaSessionInfo.getFieldName();
        String receivedName = captchaData.getName();
        if (receivedName == null) {
            LOG.warn("Respuesta invalida, no se ha recibido el campo name");
            return CaptchaValidationResult.MISSING_FIELD_NAME;
        } else if (captchaData.getValue() == null) {
            LOG.warn("Respuesta invalida, no se ha recibido el campo value");
            return CaptchaValidationResult.MISSING_VALUE;
        } else if (receivedName.equals(audioFieldName)) {
            return validateAudio(captchaData.getValue()) ? CaptchaValidationResult.VALID_AUDIO : CaptchaValidationResult.FAILED_AUDIO;
        } else if (receivedName.equals(imageFieldName)) {
            return validateImage(captchaData.getValue()) ? CaptchaValidationResult.VALID_IMAGE : CaptchaValidationResult.FAILED_IMAGE;
        } else {
            LOG.warn("Respuesta no válida, no existe ningún audio {} ni ninguna imagen {} en los parámetros {}", audioFieldName, imageFieldName, captchaData);
            return CaptchaValidationResult.OTHER_ERROR;
        }
    }

    private boolean validateImage(String answer) {
        String expectedAnswer = captchaSessionInfo.getValidChoice();
        if (!captchaSessionInfo.getValidChoice().equals(answer)) {
            LOG.warn("Respuesta no válida, la imagen seleccionada {} no se corresponde con la respuesta esperada {}", answer, expectedAnswer);
            return false;
        } else {
            LOG.debug("Se ha verificado correctamente la imagen ({}={})", answer, expectedAnswer);
            return true;
        }
    }

    private boolean validateAudio(String answer) {
        String expectedAnswer = captchaSessionInfo.getAudioAnswer().getName().toLowerCase();
        if (!expectedAnswer.equals(answer.toLowerCase())) {
            LOG.warn("Respuesta no válida, la respuesta del audio {} no se corresponde con la respuesta esperada {}", answer, expectedAnswer);
            return false;
        } else {
            LOG.debug("Se ha verificado correctamente el audio ({}={})", answer, expectedAnswer);
            return true;
        }
    }

    public void invalidate() {
        this.captchaSessionInfo = null;
        LOG.debug("Invalidada sesión del captcha");
    }

}
