package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.interceptors.RedirectToConfirmation;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import com.szymonharabasz.grocerylistmanager.service.UserTokenWrapper;
import org.apache.commons.lang3.RandomStringUtils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.ResourceBundle;

@Named
@RequestScoped
public class RequestPasswordResetBacking {

    private final FacesContext facesContext;
    private final ExternalContext externalContext;
    private final UserService userService;
    private final HashingService hashingService;
    private final Event<UserTokenWrapper> event;

    @Email
    private String email;

    @Inject
    public RequestPasswordResetBacking(
            FacesContext facesContext,
            UserService userService, HashingService hashingService, Event<UserTokenWrapper> event) {
        this.facesContext = facesContext;
        this.externalContext = facesContext.getExternalContext();
        this.userService = userService;
        this.hashingService = hashingService;
        this.event = event;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @RedirectToConfirmation(type = "password-reset-requested")
    public void request() {
        userService.findByEmail(email).ifPresent(user -> {
            Salt salt = hashingService.findSaltByUserId(user.getId()).orElseThrow(IllegalStateException::new);
            String passwordResetToken = RandomStringUtils.randomAlphanumeric(32);
            String passwordResetTokenHash = HashingService.createHash(passwordResetToken, salt.getSalt());
            Date expiresAt = Date.from(Instant.now().plus(Duration.ofMinutes(30)));
            user.setPasswordResetTokenHash(new ExpirablePayload(passwordResetTokenHash, expiresAt));
            userService.save(user);
            event.fireAsync(new UserTokenWrapper(user, passwordResetToken));
        });
    }

}
