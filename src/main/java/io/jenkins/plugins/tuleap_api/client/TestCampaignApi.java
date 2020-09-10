package io.jenkins.plugins.tuleap_api.client;

import hudson.util.Secret;

import java.util.List;

public interface TestCampaignApi {
    String TEST_CAMPAIGN_API = "/testmanagement_campaigns";

    void sendTTMResults(String campaignId, String buildUrl, List<String> results, Secret token);
}
