package io.jenkins.plugins.tuleap_api.client;

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
