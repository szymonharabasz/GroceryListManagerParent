package com.szymonharabasz.grocerylistmanager.service;

import com.szymonharabasz.grocerylistmanager.domain.User;

import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import java.util.logging.Logger;

@Singleton
public class EmailConfirmTokenInvalidateService {
    private final Logger logger = Logger.getLogger(EmailConfirmTokenInvalidateService.class.getName());
    @Resource
    private TimerService timerService;
    private final UserService userService;

    @Inject
    public EmailConfirmTokenInvalidateService(UserService userService) {
        this.userService = userService;
    }

    public EmailConfirmTokenInvalidateService() {
        this(null);
    }

    public void sendPasswordReset(@ObservesAsync User user) {
        timerService.createTimer(48 * 60 * 60 * 1000, user);
    }

    @Timeout
    public void invalidatePasswordResetRequest(Timer timer) {
        if (timer.getInfo() instanceof User) {
            User user = (User) timer.getInfo();
            user.setConfirmationToken(null);
            userService.save(user);
            logger.info("Timeout: invalidating email confirm token hash for user " + user.getName());
        }
    }
}
