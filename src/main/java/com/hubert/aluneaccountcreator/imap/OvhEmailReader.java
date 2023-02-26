package com.hubert.aluneaccountcreator.imap;

import com.hubert.aluneaccountcreator.utils.ThreadUtils;
import jakarta.mail.*;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class OvhEmailReader {
    private final String host;
    private final String username;
    private final String password;
    private final boolean sslEnabled;

    public List<Message> getMessages() throws MessagingException {
        Properties properties = new Properties();

        properties.setProperty("mail.imap.ssl.enabled", sslEnabled ? "true" : "false");
        Session session = Session.getInstance(properties);


        Store store = session.getStore("imap");

        store.connect(host, username, password);

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);

        Message[] messages = folder.getMessages();

        return Arrays.stream(messages).toList();
    }

    public Message getMessageBySubject(String subject) {
        try {
            List<Message> matched = getMessages().stream().filter(msg -> {
                try {
                    return msg.getSubject().equals(subject);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }).toList();

            return matched.isEmpty() ? null : matched.get(0);
        } catch (Exception ignored) {
            return null;
        }
    }

    public CompletableFuture<Message> getMessageBySubject(String subject, Integer tries, Duration await) {
        return CompletableFuture.supplyAsync(() -> {
            Message message = getMessageBySubject(subject);

            for (int i = 0; i < tries; i++) {
                if (message == null) {
                    message = getMessageBySubject(subject);
                } else {
                    break;
                }

                ThreadUtils.delay(await.toMillis());
            }

            return message;
        });
    }

}
