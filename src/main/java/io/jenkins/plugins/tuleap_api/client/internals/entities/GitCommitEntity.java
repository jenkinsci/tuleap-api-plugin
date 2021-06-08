package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitCommit;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitCommitEntity implements GitCommit {

    private final String hash;

    private final ZonedDateTime commitDate;

    public GitCommitEntity(@JsonProperty("id") String hash, @JsonProperty("committed_date") String commitDate) {
        this.hash = hash;
        this.commitDate = ZonedDateTime.parse(commitDate);
    }

    @Override
    public String getHash() {
        return this.hash;
    }

    @Override
    public ZonedDateTime getCommitDate() {
        return this.commitDate;
    }
}
