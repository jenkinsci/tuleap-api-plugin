package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitBranch;
import io.jenkins.plugins.tuleap_api.client.GitCommit;

final public class GitBranchEntity implements GitBranch {

    private final String name;
    private final GitCommitEntity commit;

    public GitBranchEntity(@JsonProperty("name") String name, @JsonProperty("commit") GitCommitEntity commit){
        this.name = name;
        this.commit = commit;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public GitCommit getCommit() {
        return this.commit;
    }
}
