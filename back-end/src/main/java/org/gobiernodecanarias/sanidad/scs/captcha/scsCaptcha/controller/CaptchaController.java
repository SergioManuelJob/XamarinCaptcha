package org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.enumerate.AudioType;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.enumerate.CaptchaValidationResult;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaData;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaFrontEndData;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaRequest;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.model.CaptchaResponse;
import org.gobiernodecanarias.sanidad.scs.captcha.scsCaptcha.service.CaptchaSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

@RestController
@Scope("session")
public class CaptchaController {
    private static final int DEFAULT_NUM_OPTIONS = 5;
    private static final String POST_REQUEST_HEADER = "request";
    private final Logger LOG = LoggerFactory.getLogger(CaptchaController.class);
    
    @Autowired
    //@Inject
    private CaptchaSession captchaSession;

    @RequestMapping(value = "/start/{howMany}", method = RequestMethod.GET)
    @ResponseBody
    public CaptchaFrontEndData start(@PathVariable int howMany) {
        CaptchaFrontEndData dato = captchaSession.start(howMany);
        return dato;
    }

    @RequestMapping(value = "/image/{index}", method = RequestMethod.GET)
    public void image(@PathVariable int index, HttpServletResponse response) {
        boolean retina = false; // NOTA: No se disponen de las imágenes en este formato.
        InputStream input = captchaSession.getImage(index, retina);
        MediaType contentType = MediaType.IMAGE_PNG;
        writeResponse(contentType, input, response);
    }

    //TODO: Refactorizar este método o eliminar cuando se disponga de tiempo
    private void writeResponse(MediaType contentType, InputStream input, HttpServletResponse response) {
        OutputStream output = null;
        byte[] buffer = new byte[10240];
        try {
            response.setContentType(contentType.toString());
            output = response.getOutputStream();
            for (int length = 0; (length = input.read(buffer)) > 0; ) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } catch (IOException e) {
            LOG.error("Se ha producido un error al escribir la respuesta: ", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se puede cargar un recurso", e);
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException ignore) {
                LOG.error("Se ha producido un error al escribir la respuesta: ", ignore.getMessage());
            }
            try {
                input.close();
            } catch (IOException ignore) {
                LOG.error("Se ha producido un error al escribir la respuesta: ", ignore.getMessage());
            }
        }
    }

    @RequestMapping(value = "/audio/{audioType}", method = RequestMethod.GET)
    public void audio(@PathVariable String audioType, HttpServletResponse response) {
        AudioType audioTypEnum = AudioType.MP3;
        if (StringUtils.hasText(audioType)) {
            try {
                audioTypEnum = AudioType.valueOf(audioType.toUpperCase());
            } catch (Exception ex) {
                LOG.error("Se ha producido un error al obtener el tipo de audio. Se toma el formato MP3 por defecto. Error ocurrido: ", ex.getMessage());
            }
        }
        InputStream input = captchaSession.getAudio(audioTypEnum);
        MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;
        writeResponse(contentType, input, response);
    }

    @RequestMapping(value = "/audio", method = RequestMethod.GET)
    public void audio(HttpServletResponse response) {
        audio(AudioType.MP3.name().toLowerCase(), response);
    }

    //TODO: Cuando se disponga de tiempo refactorizar esta parte.
    @RequestMapping(value = "/try", method = RequestMethod.POST)
    public ResponseEntity<CaptchaResponse> validate(@RequestHeader(POST_REQUEST_HEADER) String valueRequestHeader ) {
        //NOTA: En las solicitudes POST, lo que se envía en el cuerpo se tiene que enviar como una cabecera denominado
        //      "request" en las cabeceras HTTP de la petición.
        LOG.debug("");
        LOG.debug("Validando la respuesta del captcha...");
        if(!StringUtils.hasText(valueRequestHeader)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se recibió o especificó la cabecera : " + POST_REQUEST_HEADER + " en la petición."
            );
        }
        LOG.debug("Contenido de la cabecera " + POST_REQUEST_HEADER + " es: " + valueRequestHeader);
        CaptchaRequest captchaRequest = new CaptchaRequest();
        try {
            captchaRequest = new ObjectMapper().readValue(valueRequestHeader, CaptchaRequest.class);
        } catch (JsonProcessingException e) {
            LOG.error("ERROR: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede procesar lo que viene en la cabecera : " + POST_REQUEST_HEADER + " (valor: " + valueRequestHeader + ")"
            );
        }
        CaptchaData captchaData = new CaptchaData(captchaRequest.getName(), captchaRequest.getValue());

        boolean isOk = false;
        CaptchaValidationResult result = captchaSession.validate(captchaData);
        String status = null;
        switch (result) {
            case FAILED_IMAGE:
                status = "failedImage";
                break;
            case VALID_IMAGE:
                isOk = true;
                status = "validImage";
                break;
            case FAILED_AUDIO:
                status = "failedAudio";
                break;
            case VALID_AUDIO:
                isOk = true;
                status = "validAudio";
                break;
            default:
                status = "otherError";
        }
        CaptchaResponse response = new CaptchaResponse(isOk, status);
        LOG.debug("Resultado validación: " + response);
        captchaSession.invalidate();
        return new ResponseEntity(response, HttpStatus.OK);
    }

}
