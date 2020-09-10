package io.jenkins.plugins.tuleap_api.client.internals.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

@JsonRootName("automated_tests_results")
public class SendTTMResultsEntity {
    private final List<String> results;
    private final String buildUrl;

    public SendTTMResultsEntity(final String buildUrl, final List<String> results) {
        this.results = results;
        this.buildUrl = buildUrl;
    }

    @JsonGetter("junit_contents")
    public List<String> getResults() {
        return this.results;
    }

    @JsonGetter("build_url")
    public String getBuildUrl() {
        return this.buildUrl;
    }

}
