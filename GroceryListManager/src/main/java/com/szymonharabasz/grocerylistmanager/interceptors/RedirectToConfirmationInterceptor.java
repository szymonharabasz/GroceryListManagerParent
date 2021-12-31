package com.szymonharabasz.grocerylistmanager.interceptors;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

@Interceptor
@RedirectToConfirmation
public class RedirectToConfirmationInterceptor {
    @AroundInvoke
    public Object redirect(InvocationContext context) throws Exception {
        Object result = context.proceed();
        RedirectToConfirmation redirectToConfirmation = context.getMethod().getAnnotation(RedirectToConfirmation.class);
        String type = redirectToConfirmation.type();
        System.err.println("Redirect interceptor called for " + type);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        try {
            externalContext.redirect(externalContext.getRequestContextPath() +
                    "/message.xhtml?type=" + type);
        } catch (IOException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    ResourceBundle.getBundle("com.szymonharabasz.grocerylistmanager.texts")
                            .getString("generic-error-message"), null));
        }
        return result;
    }
}
