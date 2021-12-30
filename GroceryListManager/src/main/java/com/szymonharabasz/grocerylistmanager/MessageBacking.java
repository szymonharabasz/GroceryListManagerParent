package com.szymonharabasz.grocerylistmanager;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

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
        FacesMessage.Severity severity = Objects.equals(type, "wrong-token") ?
                FacesMessage.SEVERITY_ERROR :
                FacesMessage.SEVERITY_INFO;
        ResourceBundle resourceBundle = ResourceBundle.getBundle("com.szymonharabasz.grocerylistmanager.texts");
        facesContext.addMessage(null,
                new FacesMessage(severity, resourceBundle.getString(type + ".summary"),
                        resourceBundle.getString(type + ".detail")));
        title = resourceBundle.getString(type + ".title");
        header = resourceBundle.getString(type + ".header");
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
