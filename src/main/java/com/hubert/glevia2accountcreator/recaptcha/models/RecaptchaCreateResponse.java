package com.hubert.glevia2accountcreator.recaptcha.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public record RecaptchaCreateResponse(Integer status, @JsonAlias("request") String id) {}
