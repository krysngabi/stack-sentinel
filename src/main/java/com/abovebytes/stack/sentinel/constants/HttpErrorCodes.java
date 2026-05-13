package com.abovebytes.stack.sentinel.constants;

import com.abovebytes.stack.sentinel.utils.MessageUtils;
import lombok.Getter;

@Getter
public enum HttpErrorCodes {
    // These codes are used in the front end mobile app, so don't update it unless updated in the mobile side as well.
    REDIRECT_TO_LOGIN("http.should.redirect.to.login");

    private final String description;

    HttpErrorCodes(String description) {
        this.description = description;
    }

    //    CentralHttpErrorCodes.REDIRECT_TO_LOGIN.message(messageUtils);
    public String message(MessageUtils messageUtils) {
        return messageUtils.message(description);
    }
}

