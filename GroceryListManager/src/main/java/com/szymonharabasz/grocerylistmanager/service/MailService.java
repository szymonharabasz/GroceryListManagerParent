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

    @Inject
    public MailService(ServletContext servletContext) {
        resourceBundle = ResourceBundle.getBundle("com.szymonharabasz.grocerylistmanager.texts");
        this.servletContext = servletContext;
    }

    public MailService() { this(null); }

    public void sendConfirmation(@ObservesAsync User user) throws MessagingException {
        Message message = createMessage();
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        message.setSubject(resourceBundle.getString("confirmation-mail-title"));

        String contextPath = servletContext.getContextPath();
        String webserver = System.getProperty("HOST", "https://localhost:8181/");
        String confirmationToken = user.getConfirmationToken().getPayload();
        String link = webserver + contextPath + "/confirm.xhtml?token=" + confirmationToken;

        String msg = String.format(
                resourceBundle.getString("confirmation-mail-format"), user.getName(), link, link);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    public void sendPasswordReset(@ObservesAsync UserTokenWrapper userTokenWrapper) throws MessagingException {
        Message message = createMessage();
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userTokenWrapper.getUser().getEmail()));
        message.setSubject(resourceBundle.getString("password-reset-mail-title"));

        String contextPath = servletContext.getContextPath();
        String webserver = System.getProperty("HOST", "https://localhost:8181/");
        String link = webserver + contextPath + "/reset-password.xhtml?user=" + userTokenWrapper.getUser().getName() +
                "&token=" + userTokenWrapper.getToken();

        String msg = String.format(resourceBundle.getString("password-reset-mail-format"), userTokenWrapper.getUser().getName(), link, link);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }

    private Message createMessage() throws MessagingException {
        String host = servletContext.getInitParameter("mail.smtp.host");
        String port = servletContext.getInitParameter("mail.smtp.port");

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.ssl.trust",host);
        properties.put("mail.smtp.socketFactory.port", port);
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");

        String username = servletContext.getInitParameter("mail.smtp.username");
        String password = servletContext.getInitParameter("mail.smtp.password");

        System.err.println("HOST " + host);

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        String address = servletContext.getInitParameter("mail.smtp.address");
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(address));

        return message;
    }
}
