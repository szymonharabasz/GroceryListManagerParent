package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.service.MailService;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import com.szymonharabasz.grocerylistmanager.validation.Alphanumeric;
import com.szymonharabasz.grocerylistmanager.validation.Password;
import com.szymonharabasz.grocerylistmanager.validation.Unique;
import org.apache.commons.lang3.RandomStringUtils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.*;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Named
@RequestScoped
public class RegisterBacking {
    private final UserService userService;
    private final HashingService hashingService;
    private final MailService mailService;
    private final Event<User> userRegistrationEvent;
    private final FacesContext facesContext;
    private final ExternalContext externalContext;

    @NotBlank
    @Alphanumeric
    @Size(min = 4, max = 20)
    private String username;

    @NotBlank
    @Password
    private String password;
    @NotBlank
    @Password
    private String repeatPassword;
    @NotBlank
    @Email
    @Unique(name = "email", message = "this e-mail address has been already regustered")
    private String email;

    @Inject
    public RegisterBacking(UserService userService, HashingService hashingService, MailService mailService, Event<User> userRegistrationEvent, FacesContext facesContext) {
        this.userService = userService;
        this.hashingService = hashingService;
        this.mailService = mailService;
        this.userRegistrationEvent = userRegistrationEvent;
        this.facesContext = facesContext;
        this.externalContext = facesContext.getExternalContext();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void register() {
        Salt salt = new Salt(Utils.generateID(), HashingService.createSalt());
        hashingService.save(salt);
        User user = new User(salt.getUserId(), username, HashingService.createHash(password, salt), email);
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofDays(2)));
        user.setConfirmationToken(new ExpirablePayload(RandomStringUtils.randomAlphanumeric(32), expiresAt));
        userService.save(user);
        userRegistrationEvent.fireAsync(user);
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + "/message.xhtml?type=email-sent");
        } catch (IOException e) {
            facesContext.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "An error has occured.", null));
        }
    }
}
