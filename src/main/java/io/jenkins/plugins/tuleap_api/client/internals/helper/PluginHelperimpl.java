package io.jenkins.plugins.tuleap_api.client.internals.helper;

import jenkins.model.Jenkins;

public class PluginHelperimpl implements PluginHelper {
    @Override
    public Jenkins getJenkinsInstance() {
        Jenkins jenkins = Jenkins.getInstanceOrNull();
        if (jenkins == null) {
            throw new IllegalStateException("Jenkins not started");
        }
        return jenkins;
    }
}
