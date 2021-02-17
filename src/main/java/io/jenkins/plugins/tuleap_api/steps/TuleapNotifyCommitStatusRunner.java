package io.jenkins.plugins.tuleap_api.steps;

import hudson.model.Run;
import hudson.plugins.git.util.BuildData;
import io.jenkins.plugins.tuleap_api.client.GitApi;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;

import javax.inject.Inject;
import javax.management.RuntimeErrorException;
import java.io.PrintStream;

public class TuleapNotifyCommitStatusRunner {
    private final GitApi gitApi;

    @Inject
    public TuleapNotifyCommitStatusRunner(final GitApi gitApi) {
        this.gitApi = gitApi;
    }

    public void run(
        final StringCredentials credential,
        final PrintStream logger,
        final Run run,
        final TuleapNotifyCommitStatusStep step
    ) {
        logger.println("Retrieving Git Data");
        final BuildData gitData = run.getAction(BuildData.class);

        if (gitData == null) {
            throw new RuntimeErrorException(
                new Error("Failed to retrieve Git Data. Please check the configuration.")
            );
        }

        logger.println("Sending build status to Tuleap");
        gitApi.sendBuildStatus(
            step.getRepositoryId(),
            gitData.lastBuild.getSHA1().name(),
            step.getStatus(),
            credential.getSecret()
        );
    }
}
