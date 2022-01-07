package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.User;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletContext;
import java.util.Properties;
import java.util.ResourceBundle;

@Named
@ApplicationScoped
public class MailService {

    private final ServletContext servletContext;
    private final ResourceBundle resourceBundle;
    private final Properties properties;

    @Inject
    public MailService(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.resourceBundle = ResourceBundle.getBundle("com.szymonharabasz.grocerylistmanager.texts");
        this.properties = setUpProperties();
    }

    public MailService() { this(null); }

    private String getLinkPrefix() {
        String contextPath = servletContext.getContextPath();
        String webserver = System.getProperty("HOST", "https://localhost:8181/");
        return webserver + contextPath;
    }

    public void sendConfirmation(@ObservesAsync User user) throws MessagingException {
        String confirmationToken = user.getConfirmationToken().getPayload();
        String link = getLinkPrefix() + "/confirm.xhtml?token=" + confirmationToken;

        String to = user.getEmail();
        String subject = resourceBundle.getString("confirmation-mail-title");
        String content = String.format(
                resourceBundle.getString("confirmation-mail-format"), user.getName(), link, link);
        sendMessage(to, subject, content);
    }

    public void sendPasswordReset(@ObservesAsync UserTokenWrapper userTokenWrapper) throws MessagingException {
        String link = getLinkPrefix() + "/reset-password.xhtml?user=" + userTokenWrapper.getUser().getName() +
                "&token=" + userTokenWrapper.getToken();

        System.err.println("Link is " + link);
        String to = userTokenWrapper.getUser().getEmail();
        String subject = resourceBundle.getString("password-reset-mail-title");
        String content = String.format(resourceBundle.getString("password-reset-mail-format"), userTokenWrapper.getUser().getName(), link, link);
        sendMessage(to, subject, content);
    }

    private void sendMessage(String to, String subject, String content) throws MessagingException {
        Message message = createMessage(to, subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        System.err.println("Before sending message");
        Transport.send(message);
        System.err.println("Before sending message");

    }

    private Message createMessage(String to, String subject) throws MessagingException {

        String username = servletContext.getInitParameter("mail.smtp.username");
        String password = servletContext.getInitParameter("mail.smtp.password");
        Session session = Session.getInstance(this.properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        String address = servletContext.getInitParameter("mail.smtp.address");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(address));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);

        return message;
    }

    private Properties setUpProperties() {
        String host = servletContext.getInitParameter("mail.smtp.host");
        String port = servletContext.getInitParameter("mail.smtp.port");
        System.err.println("HOST " + host);

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.trust",host);
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");

        return properties;
    }
}
