package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.User;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletContext;
import java.util.Properties;

@Named
@ApplicationScoped
public class MailService {

    private final ServletContext servletContext;

    @Inject
    public MailService(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public MailService() { this(null); }

    public void sendConfirmation(@ObservesAsync User user) throws MessagingException {
        Message message = createMessage();
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user.getEmail()));
        message.setSubject("[Grocery List Manager] Confirm your email.");

        String contextPath = servletContext.getContextPath();
        // TODO: change to real domain
        String webserver = System.getProperty("HOST");
        String confirmationToken = user.getConfirmationToken().getPayload();
        String link = webserver + contextPath + "/confirm.xhtml?token=" + confirmationToken;

        String msg = "<h3>Hello, " + user.getName() + "!</h3><br /><br />" +
                "to confirm you e-mail address in the Grocery List Manager application, " +
                "please click on the link below or copy it to " +
                "your web browser address bar.<br /><br />" +
                "<a href=\"" + link + "\">" + link + "</a><br /><br />" +
                "This helps use to make sure that you are a real personn and that your address " +
                "can be used for password recovery should you need that. If you fail to confirm your e-mail," +
                "your acount will be removed after 48 hours.<br /><br />" +
                "Your Grocery List Manager Team";
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
        message.setSubject("[Grocery List Manager] Reset your password.");

        String contextPath = servletContext.getContextPath();
        // TODO: change to real domain
        String webserver = System.getProperty("HOST");
        String link = webserver + contextPath + "/reset-password.xhtml?user=" + userTokenWrapper.getUser().getName() +
                "&token=" + userTokenWrapper.getToken();

        String msg = "<h3>Hello, " + userTokenWrapper.getUser().getName() + "!</h3><br /><br />" +
                "to reset your password in the Grocery List Manager application, " +
                "please click on the link below or copy it to " +
                "your web browser address bar.<br /><br />" +
                "<a href=\"" + link + "\">" + link + "</a><br /><br />" +
                "If you have not requested to reset your password, please ignore this e-mail..<br /><br />" +
                "Your Grocery List Manager Team";
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
