package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.interceptors.RedirectToConfirmation;
import com.szymonharabasz.grocerylistmanager.service.RandomService;
import com.szymonharabasz.grocerylistmanager.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;

import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.SecurityContext;
import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

@Named
public class RequestNewConfirmationBacking {

    private final RandomService randomService;
    private final UserService userService;
    private final SecurityContext securityContext;
    private final Event<User> userEvent;

    @Inject
    public RequestNewConfirmationBacking(RandomService randomService, UserService userService, SecurityContext securityContext, Event<User> userEvent) {
        this.randomService = randomService;
        this.userService = userService;
        this.securityContext = securityContext;
        this.userEvent = userEvent;
    }

    @RedirectToConfirmation(type = "email-sent")
    public void request() {
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofDays(2)));
        currenUser().ifPresent(user -> {
            user.setConfirmationToken(new ExpirablePayload(randomService.getAlphanumeric(32), expiresAt));
            userService.save(user);
            userEvent.fireAsync(user);
        });
    }

    Optional<User> currenUser() {
        if (securityContext != null) {
            Principal caller = securityContext.getCallerPrincipal();
            return userService.findByName(caller.getName());
        } else {
            return Optional.empty();
        }
    }
}
