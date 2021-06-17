package io.jenkins.plugins.tuleap_api.client;

public interface GitTreeContent {

    String getId();

    String getName();

    String getPath();

    String getType();

    String getMode();
}
