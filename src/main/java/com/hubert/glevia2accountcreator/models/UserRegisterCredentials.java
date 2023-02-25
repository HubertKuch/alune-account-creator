package com.hubert.glevia2accountcreator.models;

public record UserRegisterCredentials(
        String email, String login, String password, String confirmation, String socialId, Boolean tosAccepted
) {
}
