package io.jenkins.plugins.tuleap_api.client;

import java.time.ZonedDateTime;

public interface GitCommit {
    String getHash();

    ZonedDateTime getCommitDate();
}
