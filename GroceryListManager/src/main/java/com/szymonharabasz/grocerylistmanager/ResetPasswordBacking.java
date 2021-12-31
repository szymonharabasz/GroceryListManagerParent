package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.domain.Salt;
import com.szymonharabasz.grocerylistmanager.domain.User;
import com.szymonharabasz.grocerylistmanager.interceptors.RedirectToConfirmation;
import com.szymonharabasz.grocerylistmanager.service.HashingService;
import com.szymonharabasz.grocerylistmanager.service.UserService;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

@Named
@RequestScoped
public class ResetPasswordBacking implements Serializable {

    private String userName;
    private String newPassword;
    private String token;
    private User user;
    private Salt salt;

    private final UserService userService;
    private final HashingService hashingService;
    private final ExternalContext externalContext;

    @Inject
    public ResetPasswordBacking(UserService userService, HashingService hashingService, FacesContext facesContext) {
        this.userService = userService;
        this.hashingService = hashingService;
        this.externalContext = facesContext.getExternalContext();
    }

    public void load() {
        Optional<User> maybeUser = userService.findByName(userName);
        maybeUser.ifPresent(usr -> {
            this.salt = hashingService.findSaltByUserId(usr.getId()).orElseThrow(IllegalStateException::new);
            this.user = usr;
            String tokenHash = hashingService.createHash(token, salt);
            if (!Objects.equals(tokenHash, user.getPasswordResetTokenHash().getPayload())) {
                showError();
            }
        });
        System.out.println("USER IS " + user + " AND SALT IS " + salt);
        if (!maybeUser.isPresent()) {
            showError();
        }
    }

    @RedirectToConfirmation(type = "password-changed")
    public void resetPassword() {
        String newPasswordHash = hashingService.createHash(newPassword, salt);
        user.setPasswordHash(newPasswordHash);
        user.setPasswordResetTokenHash(null);
        userService.save(user);
    }

    @RedirectToConfirmation(type = "wrong-token")
    public void showError() {  }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
