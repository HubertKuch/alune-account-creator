package com.hubert.aluneaccountcreator.models;

public record UserRegisterResponse(String login, Boolean confirmationNeeded, String pin) {
}
