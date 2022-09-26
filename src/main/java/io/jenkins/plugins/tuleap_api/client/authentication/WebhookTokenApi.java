package io.jenkins.plugins.tuleap_api.client.authentication;

public interface WebhookTokenApi {
    String WEBHOOK_CHECK_API = "/plugins/hudson_git/jenkins_tuleap_hook_trigger_check";

    boolean checkWebhookTokenIsValid(String validityToken);
}
