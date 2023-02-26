package com.hubert.aluneaccountcreator.models;

public record IncomingEmailData(
        Integer id,
        String name,
        String email,
        String domain,
        String password
) {}
