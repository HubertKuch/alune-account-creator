package com.hubert.aluneaccountcreator.models;

public record UserRegisterCredentials(
        String email, String login, String password, String socialId
) {
}
