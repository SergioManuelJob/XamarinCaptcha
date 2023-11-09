package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.enumerate.AudioType;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Repository
public class CaptchaRepository {
    private static final String RESOURCE_ROOT = "captcha";
    private final Logger LOG = LoggerFactory.getLogger(CaptchaRepository.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<CaptchaAnswer> images;
    private List<CaptchaAnswer> audios;

    @PostConstruct
    public void init() {
        LOG.info("========================================");
        LOG.info("Iniciando servicio...");
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, CaptchaAnswer.class);
        try {
            this.images = objectMapper.readValue(getResourceAsStream("images.json"), collectionType);
            this.audios = objectMapper.readValue(getResourceAsStream("audios.json"), collectionType);
        } catch (IOException e) {
            throw new RuntimeException("No se ha sido posible la carga de los recursos necesarios para el captcha.", e);
        }
        LOG.info("Servicio inicializado con {} imágenes y {} audios", images.size(), audios.size());
        LOG.info("========================================");
    }

    private InputStream getResourceAsStream(String resource) {
        LOG.debug("Ruta recurso: " + RESOURCE_ROOT + "/" + resource);
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE_ROOT + "/" + resource);
    }

    public List<CaptchaAnswer> getImages() {
        return images;
    }

    public List<CaptchaAnswer> getAudios() {
        return audios;
    }

    public InputStream getImageStream(String path) {
        return getResourceAsStream("images/" + path);
    }

    public InputStream getAudioStream(String path, AudioType audioType) {
        if (AudioType.OGG == audioType) {
            path = path.replace(".mp3", ".ogg");
        }
        return getResourceAsStream("audios/" + path);
    }

}
