package com.pivotallabs.web;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class UserApplication extends ResourceConfig {

    public UserApplication() {
        registerClasses(UserResource.class)
                .register(JacksonFeature.class);
    }
}
