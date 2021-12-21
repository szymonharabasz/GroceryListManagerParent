package com.szymonharabasz.grocerylistmanager;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;

@Named
@RequestScoped
public class MessageBacking {

    private final FacesContext facesContext;
    private final ExternalContext externalContext;

    private String type;

    private String title;

    private String header;

    @Inject
    public MessageBacking(FacesContext facesContext) {
        this.facesContext = facesContext;
        this.externalContext = facesContext.getExternalContext();
    }

    public void load() {
        if (Objects.equals(type, "email-sent")) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Check your e-mail",
                            "An e-mail has been sent to the address you provided in the registration. " +
                                    "Check your mailbox and click the confirmation link to activate your account."));
            title = "Confirmation e-mail sent";
            header = "Confirmation e-mail sent";
        } else if (Objects.equals(type, "password-changed")) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Success",
                            "Your password has been successfuly changed."));
            title = "Password changed";
            header = "Password changed";
        } else if (Objects.equals(type, "password-reset-requested")) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Check your e-mail",
                            "An e-mail has been sent to the address you provided in the registration. " +
                                    "Check your mailbox and click the reset link to change your password."));
            title = "Password reset requested";
            header = "Password reset requested";
        }
        else if (Objects.equals(type, "wrong-token")) {
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problem with the link",
                            "A wrong token has been provided. Check the link you have received in the e-mail." +
                                    "The link could also have been used already."));
            title = "Wrong token";
            header = "Wrong token";
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}
