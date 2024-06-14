package com.eshop.usermanagement.security;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {
    private Environment env;

    public String getProperty(String key) {
        return env.getProperty(key);
    }
}
