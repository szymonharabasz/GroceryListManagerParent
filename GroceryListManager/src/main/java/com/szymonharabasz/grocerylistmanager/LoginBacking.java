package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Named
@RequestScoped
public class LoginBacking {
    private final SecurityContext securityContext;
    private final FacesContext facesContext;
    private final ExternalContext externalContext;
    private final UserService userService;
    private final HashingService hashingService;

    private String username;
    private String password;

    @Inject
    public LoginBacking(SecurityContext securityContext, FacesContext facesContext, UserService userService, HashingService hashingService) {
        this.securityContext = securityContext;
        this.facesContext = facesContext;
        this.externalContext = facesContext.getExternalContext();
        this.userService = userService;
        this.hashingService = hashingService;
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

    public void handleLogin() {
        System.out.println("TEST VARIABLE " + externalContext.getInitParameter("testVariable"));
        userService.findByName(username).flatMap(user -> hashingService.findSaltByUserId(user.getId())).ifPresent(salt -> {
            String passwordHash = HashingService.createHash(password, salt);
            UsernamePasswordCredential usernamePasswordCredential = new UsernamePasswordCredential(username, passwordHash);
            AuthenticationParameters authenticationParameters = AuthenticationParameters.withParams().credential(usernamePasswordCredential);
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
            AuthenticationStatus authenticationStatus = securityContext.authenticate(request, response, authenticationParameters);
            System.err.println("Authentication status: " + authenticationStatus);
            switch (authenticationStatus) {
                case SEND_CONTINUE:
                    facesContext.responseComplete();
                    break;
                case SEND_FAILURE:
                    facesContext.addMessage(null,
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                    "Wrong user name or password", null));
                    break;
                case SUCCESS:
                    redirect("/index.xhtml");
                    break;
                case NOT_DONE:
            }
        });
    }
    private void redirect(String to) {
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + to);
        } catch (IOException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "An error has occured.", null));
        }
    }
}
