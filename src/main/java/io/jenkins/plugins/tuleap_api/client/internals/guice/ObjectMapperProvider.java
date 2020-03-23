package io.jenkins.plugins.tuleap_api.client.internals.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.inject.Provider;

public class ObjectMapperProvider implements Provider<ObjectMapper> {

    @Override
    public ObjectMapper get() {
        return new ObjectMapper().registerModule(new GuavaModule());
    }
}
