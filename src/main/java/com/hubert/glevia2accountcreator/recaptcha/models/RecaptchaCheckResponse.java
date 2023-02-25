package com.hubert.glevia2accountcreator.recaptcha.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RecaptchaCheckResponse(Integer status, @JsonAlias("request") String answer) {
}
