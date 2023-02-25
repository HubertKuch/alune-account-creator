package com.hubert.glevia2accountcreator.models;

public record UserRegisterResponse(String login, Boolean confirmationNeeded, String pin) {
}
