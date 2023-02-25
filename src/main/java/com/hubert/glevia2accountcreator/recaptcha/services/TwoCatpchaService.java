package com.hubert.glevia2accountcreator.recaptcha.services;

import com.hubert.glevia2accountcreator.configuration.RecaptchaConfiguration;
import com.hubert.glevia2accountcreator.recaptcha.exceptions.RecaptchaCreateException;
import com.hubert.glevia2accountcreator.recaptcha.models.RecaptchaCheckResponse;
import com.hubert.glevia2accountcreator.recaptcha.models.RecaptchaCreateResponse;
import com.hubert.glevia2accountcreator.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
public class TwoCatpchaService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final RecaptchaConfiguration recaptchaConfiguration;

    public TwoCatpchaService(RecaptchaConfiguration recaptchaConfiguration) {
        this.recaptchaConfiguration = recaptchaConfiguration;
    }

    public RecaptchaCreateResponse createV3Task(String pageUrl) throws RecaptchaCreateException {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        uriComponentsBuilder.queryParam("key", recaptchaConfiguration.getApiKey());
        uriComponentsBuilder.queryParam("method", "hcaptcha");
        uriComponentsBuilder.queryParam("sitekey", recaptchaConfiguration.getSiteKey());
        uriComponentsBuilder.queryParam("pageurl", pageUrl);
        uriComponentsBuilder.queryParam("json", 1);
        uriComponentsBuilder.queryParam("action", 1);

        String inPath = "http://2captcha.com/in.php";
        String url = inPath + uriComponentsBuilder.build().toUriString();

        ResponseEntity<RecaptchaCreateResponse> response = restTemplate.getForEntity(url, RecaptchaCreateResponse.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            log.warn("Cannot create recaptcha task. Body: {}", response.getBody());
            throw new RecaptchaCreateException("Cannot create recaptcha task");
        }

        log.info("Response {}", response.getBody());

        return response.getBody();
    }

    public RecaptchaCheckResponse resolve(String id) {
        return resolve(id, 20, 1000L);
    }

    public RecaptchaCheckResponse resolve(String id, Integer tries, Long delayByTryInMillis) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();

        uriComponentsBuilder.queryParam("json", 1);
        uriComponentsBuilder.queryParam("key", recaptchaConfiguration.getApiKey());
        uriComponentsBuilder.queryParam("action", "get");
        uriComponentsBuilder.queryParam("id", id);

        String resPath = "http://2captcha.com/res.php";
        String url = resPath + uriComponentsBuilder.build().toUriString();

        for (int count=0; count < tries; count++) {
            ResponseEntity<RecaptchaCheckResponse> response = restTemplate.getForEntity(url, RecaptchaCheckResponse.class);
            RecaptchaCheckResponse body = response.getBody();

            if (body == null) {
                return null;
            }

            if (body.status() == 1) {
                return body;
            }

            ThreadUtils.delay(delayByTryInMillis);
        }

        return null;
    }
}
