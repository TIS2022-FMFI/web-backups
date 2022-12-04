package web_backups.main.ui.mailSender;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static web_backups.lib.global.enums.TextColors.RESET;
import static web_backups.lib.global.enums.TextColors.SUCCESS;

public class MailSender {
    private final String from;
    private final String to;
    private final String host;
    private final String subject;
    private final String text;

    public MailSender(String from, String to, String host, String subject, String text) {
        this.from = from;
        this.to = to;
        this.host = host;
        this.subject = subject;
        this.text = text;
    }

    public void sendMail() {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.port", "587");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, "2(i@w*;F$");
            }
        });
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            // Set To Field: adding recipient's email to from field.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            message.setSubject(subject);
            MimeBodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(text);

            // creating second MimeBodyPart object
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();
            String filename = "static/attachment.txt";
            messageBodyPart2.attachFile(new File(filename));

            // creating MultiPart object
            Multipart multipartObject = new MimeMultipart();
            multipartObject.addBodyPart(messageBodyPart1);
            multipartObject.addBodyPart(messageBodyPart2);

            // set body of the email.
            message.setContent(multipartObject);

            // Send email.
            Transport.send(message);
            System.out.println(SUCCESS.getColor() + "Mail successfully sent." + RESET.getColor());
        } catch (MessagingException mEx) {
            mEx.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
