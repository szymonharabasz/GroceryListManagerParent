package com.szymonharabasz.grocerylistmanager;

import com.szymonharabasz.grocerylistmanager.service.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.security.enterprise.identitystore.CredentialValidationResult;
import javax.security.enterprise.identitystore.IdentityStore;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Named
@ApplicationScoped
public class MongoIdentityStore implements IdentityStore {
    private final Set<String> userAdminRoleSet;
    UserService userService;

    @Inject
    public MongoIdentityStore(UserService userService) {
        this.userService = userService;
        this.userAdminRoleSet = new HashSet<>(Arrays.asList("USER", "ADMIN"));
    }

    @Override
    public CredentialValidationResult validate(Credential credential) {
        System.err.println("!!!!!!!! CALLING VALIDATE !!!!!!!!");
        UsernamePasswordCredential usernamePasswordCredential = (UsernamePasswordCredential) credential;
        CredentialValidationResult credentialValidationResult = userService.findByName(usernamePasswordCredential.getCaller()).map(user -> {
            CredentialValidationResult result;
            if (usernamePasswordCredential.compareTo(user.getName(), user.getPasswordHash())) {
                result = new CredentialValidationResult(user.getName(), userAdminRoleSet);
            } else {
                result = CredentialValidationResult.NOT_VALIDATED_RESULT;
            }
            return result;
        }).orElse(CredentialValidationResult.NOT_VALIDATED_RESULT);
        System.err.println("Credentials validation result " + credentialValidationResult.getStatus());
        return credentialValidationResult;
    }

}
