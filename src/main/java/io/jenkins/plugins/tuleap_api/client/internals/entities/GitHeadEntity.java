package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.jenkins.plugins.tuleap_api.client.GitHead;

public class GitHeadEntity implements GitHead {

    private final String id;

    public GitHeadEntity(@JsonProperty("id") String id){
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
