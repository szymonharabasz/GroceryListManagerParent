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
import java.util.ResourceBundle;

@Named
@RequestScoped
public class LoginBacking {
    @Inject
    private SecurityContext securityContext;
    @Inject
    private FacesContext facesContext;
    @Inject
    private ExternalContext externalContext;
    @Inject
    private UserService userService;
    @Inject
    private HashingService hashingService;

    private String username;
    private String password;

    public LoginBacking(SecurityContext securityContext, FacesContext facesContext, ExternalContext externalContext, UserService userService, HashingService hashingService) {
        this.securityContext = securityContext;
        this.facesContext = facesContext;
        this.externalContext = externalContext;
        this.userService = userService;
        this.hashingService = hashingService;
    }

    public LoginBacking() {
        this(null, null, null, null, null);
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
        userService.findByName(username).flatMap(user -> hashingService.findSaltByUserId(user.getId())).ifPresent(salt -> {
            String passwordHash = hashingService.createHash(password, salt);
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
                case SUCCESS:
                    redirect("/index.xhtml");
                    break;
                case SEND_FAILURE:
                case NOT_DONE:
            }
        });
        facesContext.addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Wrong user name or password", null));

    }
    private void redirect(String to) {
        try {
            externalContext.redirect(externalContext.getRequestContextPath() + to);
        } catch (IOException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ResourceBundle.getBundle("com.szymonharabasz.grocerylistmanager.texts")
                            .getString("generic-error-message"), null));        }
    }
}
