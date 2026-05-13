package com.abovebytes.stack.sentinel.utils;

import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.env.Environment;
    import org.springframework.stereotype.Component;

    @Component
    public class ProfileChecker {

        @Autowired
        private Environment environment;

        public String[] getActiveProfiles() {
            return environment.getActiveProfiles();
        }
    }