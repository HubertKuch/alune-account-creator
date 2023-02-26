package com.hubert.aluneaccountcreator.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hubert.aluneaccountcreator.configuration.ImapConfiguration;
import com.hubert.aluneaccountcreator.imap.OvhEmailReader;
import com.hubert.aluneaccountcreator.models.IncomingEmailData;
import com.hubert.aluneaccountcreator.models.UserRegisterCredentials;
import com.hubert.aluneaccountcreator.models.UserRegisterResponse;
import com.hubert.aluneaccountcreator.pages.AluneRegisterPage;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@ShellComponent
@RequiredArgsConstructor
public class AccountCommands {
    private final ImapConfiguration imapConfiguration;
    private final RestTemplate restTemplate = new RestTemplate();

    @ShellMethod(value = "Create X accounts", key = "create-accounts")
    public void createAccounts(
            @ShellOption(help = "Emails list txt. Every email in new line", value = "--emails-list") String emailsListPath,
            @ShellOption(help = "Social id to delete a character", value = "--social-id") String socialId,
            @ShellOption(help = "With random delays between 200ms and 2s on providing data", value = "--random-delays", defaultValue = "true") boolean isWithRandomDelays
    ) throws MessagingException, IOException, ExecutionException, InterruptedException {
        File emailsFile = new File(emailsListPath);
        List<IncomingEmailData> emails = readEmails(emailsFile);
        List<UserRegisterResponse> users = new ArrayList<>();

        for (IncomingEmailData email : emails) {
            System.out.println(email);
            UserRegisterResponse userAccount = createUserAccount(
                    email.password(),
                    socialId,
                    email.email(),
                    isWithRandomDelays
            );

            if (userAccount != null) {
                users.add(userAccount);
            }
        }
        users.forEach(System.out::println);

        String workingDir = System.getProperty("user.dir");
        File file = new File(workingDir + "/accounts.json");

        if (file.exists()) {
            log.error("accounts.json file already exists.");
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(users);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        writer.write(content);

        writer.close();
        // create-accounts --emails-list /home/hubert/Downloads/emails.txt --social-id 7654321
    }

    private UserRegisterResponse createUserAccount(
            String password,
            String socialId,
            String email,
            boolean isWithRandomDelay
    ) throws MessagingException, IOException, ExecutionException, InterruptedException {
        WebDriver webDriver = new ChromeDriver();

        try {
            AluneRegisterPage aluneRegisterPage = new AluneRegisterPage(webDriver);

            aluneRegisterPage.get();
            UserRegisterResponse user = aluneRegisterPage.createAccount(
                    new UserRegisterCredentials(email,
                            email.substring(0, email.indexOf("@")).replace("_", ""),
                            password,
                            socialId
                    ),
                    isWithRandomDelay
            );

            if (user == null) {
                return null;
            }

            boolean activated = activateAccount(email, password);

            if (!activated) {
                log.error("Cannot activate mail.");
                return null;
            }

            return user;
        } finally {
            webDriver.quit();
        }
    }

    private boolean activateAccount(String email, String password) throws
            MessagingException,
            IOException,
            ExecutionException,
            InterruptedException {
        OvhEmailReader ovhEmailReader = new OvhEmailReader(imapConfiguration.getHost(), email, password, true);
        CompletableFuture<Message> messageFuture = ovhEmailReader.getMessageBySubject(
                "Rejestracja w serwisie",
                10,
                Duration.ofSeconds(1)
        );

        while (!messageFuture.isDone())
            ;
        Message message = messageFuture.get();

        if (message == null) {
            return false;
        }

        String emailContent = new String(message.getInputStream().readAllBytes());
        Document document = Jsoup.parse(emailContent);
        Elements elements = document.getElementsByTag("a");

        elements.forEach(anchor -> {
            if (anchor.text().contains("Aktywuj konto")) {
                System.out.println("activate");
                String url = anchor.attr("href");
                restTemplate.getForEntity(url, String.class);
            }
        });

        return true;
    }

    private List<IncomingEmailData> readEmails(File emailsFile) {
        if (!emailsFile.exists()) {
            log.error("Cannot read emails file");
            throw new IllegalStateException();
        }

        try (InputStream inputStream = new FileInputStream(emailsFile)) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Error reading emails file");
            throw new IllegalStateException();
        }
    }
}
