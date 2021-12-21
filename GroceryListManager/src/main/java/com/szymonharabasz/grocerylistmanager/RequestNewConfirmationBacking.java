package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.ExpirablePayload;
import com.szymonharabasz.grocerylistmanager.domain.User;
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

@Named
public class RequestNewConfirmationBacking {

    private final UserService userService;
    private final SecurityContext securityContext;
    private final ExternalContext externalContext;
    private final FacesContext facesContext;
    private final Event<User> userEvent;

    @Inject
    public RequestNewConfirmationBacking(UserService userService, SecurityContext securityContext, FacesContext facesContext, Event<User> userEvent) {
        this.userService = userService;
        this.securityContext = securityContext;
        this.externalContext = facesContext.getExternalContext();
        this.facesContext = facesContext;
        this.userEvent = userEvent;
    }

    public void request() {
        Date expiresAt = Date.from(Instant.now().plus(Duration.ofDays(2)));
        currenUser().ifPresent(user -> {
            user.setConfirmationToken(new ExpirablePayload(RandomStringUtils.randomAlphanumeric(32), expiresAt));
            userService.save(user);
            userEvent.fireAsync(user);
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/message.xhtml?type=email-sent");
            } catch (IOException e) {
                facesContext.addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "An error has occured.", null));
            }
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
