package io.jenkins.plugins.tuleap_api.client;

public interface Project {

    Integer getId();

    String getShortname();

    String getLabel();

    String getUri();

}
