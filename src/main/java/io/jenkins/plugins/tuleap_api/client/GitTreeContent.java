package io.jenkins.plugins.tuleap_api.client;

import io.jenkins.plugins.tuleap_api.client.exceptions.git.TreeNotFoundException;

public interface GitTreeContent {

    enum ContentType {
        TREE,
        BLOB,
        SYMLINK;

        public String toString() {
            return this.name().toLowerCase();
        }
    }

    String getId();

    String getName();

    String getPath();

    ContentType getType();

    String getMode();
}
