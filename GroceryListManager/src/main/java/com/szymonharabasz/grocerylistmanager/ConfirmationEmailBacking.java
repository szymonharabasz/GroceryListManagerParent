package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.interceptors.RedirectToConfirmation;
import com.szymonharabasz.grocerylistmanager.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

@Named
@RequestScoped
public class ConfirmationEmailBacking {

    private final UserService userService;
    private final FacesContext facesContext;

    private String token;

    @Inject
    public ConfirmationEmailBacking(UserService userService, FacesContext facesContext) {
        this.userService = userService;
        this.facesContext = facesContext;
    }

    public void confirmEmail() {
        System.out.println("TOKEN " + token);
        Optional<User> user = userService.findByConfirmationToken(token);
        user.ifPresent(usr -> {
            usr.setConfirmed(true);
            usr.setConfirmationToken(null);
            userService.save(usr);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Your e-mail addeess has been successfully confirmed. You can now sign in to " +
                                    "your account"));
        });
        if (!user.isPresent()) { showError(); }
    }

    @RedirectToConfirmation(type = "wrong-token")
    private void showError() {  }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
