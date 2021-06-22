package io.jenkins.plugins.tuleap_api.client;

public interface GitFileContent{
    String getEncoding();

    Integer getSize();

    String getName();

    String getPath();

    String getContent();
}
