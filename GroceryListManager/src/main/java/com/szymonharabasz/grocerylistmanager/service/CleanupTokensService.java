package com.szymonharabasz.grocerylistmanager.service;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.logging.Logger;

@Singleton
@Startup
public class CleanupTokensService {
    private final Logger logger = Logger.getLogger(CleanupTokensService.class.getName());

    private final UserService userService;

    @Inject
    public CleanupTokensService(UserService userService) {
        this.userService = userService;
    }

    public CleanupTokensService() {
        this(null);
    }

    @Schedule(minute = "*/10", hour = "*", persistent = false)
    public void clesanupTokens() {
        logger.info("Cleanning up tokens...");
        userService.findAll().forEach(user -> {
            if (user.getConfirmationToken() != null) {
                if  (user.getConfirmationToken().isExpired()) {
                    user.setConfirmationToken(null);
                }
            }
            if (user.getPasswordResetTokenHash() != null) {
                if (user.getPasswordResetTokenHash().isExpired()) {
                    user.setPasswordResetTokenHash(null);
                }
            }
            userService.save(user);
        });
    }
}
