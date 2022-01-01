package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.interceptors.RedirectToConfirmation;
import com.szymonharabasz.grocerylistmanager.service.MailService;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.RandomService;
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
import java.util.ResourceBundle;

@Named
@RequestScoped
public class RegisterBacking {
    private final UserService userService;
    private final HashingService hashingService;
    private final Event<User> userRegistrationEvent;

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
    public RegisterBacking(UserService userService, HashingService hashingService, Event<User> userRegistrationEvent) {
        this.userService = userService;
        this.hashingService = hashingService;
        this.userRegistrationEvent = userRegistrationEvent;
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

    @RedirectToConfirmation(type = "email-sent")
    public void register() {
        Salt salt = hashingService.createSalt();
        hashingService.save(salt);
        User user = userService.createUser(salt.getUserId(), username, hashingService.createHash(password, salt), email);
        userService.save(user);
        userRegistrationEvent.fireAsync(user);
    }
}
