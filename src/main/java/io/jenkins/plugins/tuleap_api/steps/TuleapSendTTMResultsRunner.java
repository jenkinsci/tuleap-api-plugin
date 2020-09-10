package io.jenkins.plugins.tuleap_api.steps;

import hudson.EnvVars;
import hudson.FilePath;
import io.jenkins.plugins.tuleap_api.client.TestCampaignApi;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;

import javax.inject.Inject;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TuleapSendTTMResultsRunner {
    private final TestCampaignApi testCampaignApi;

    @Inject
    public TuleapSendTTMResultsRunner(final TestCampaignApi testCampaignApi) {
        this.testCampaignApi = testCampaignApi;
    }

    public void run(
        final TuleapAccessToken tuleapAccessToken,
        final PrintStream logger,
        final TuleapSendTTMResultsStep step,
        final FilePath filePath,
        final EnvVars envVars
    ) throws Exception {
        logger.println("Collecting all result files");
        final List<String> results = Arrays.stream(filePath.list(step.getFilesPath()))
            .map(path -> {
                try {
                    return path.readToString();
                } catch (IOException | InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            })
            .collect(Collectors.toList());

        logger.println("Sending results to Tuleap");
        this.testCampaignApi.sendTTMResults(
            step.getCampaignId(),
            envVars.get("BUILD_URL"),
            results,
            tuleapAccessToken.getToken()
        );
    }
}
