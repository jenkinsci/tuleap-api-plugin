package io.jenkins.plugins.tuleap_api.deprecated_client.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TuleapPullRequestsCollection {
    private ArrayList<TuleapPullRequests> collection;

    public void setCollection(ArrayList<TuleapPullRequests> collection) {
        this.collection = collection;
    }

    public ArrayList<TuleapPullRequests> getCollection() {
        return this.collection;
    }
}
