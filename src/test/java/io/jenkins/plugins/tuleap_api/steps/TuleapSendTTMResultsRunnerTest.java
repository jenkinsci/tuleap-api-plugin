package io.jenkins.plugins.tuleap_api.steps;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.TestCampaignApi;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import org.junit.Test;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class TuleapSendTTMResultsRunnerTest {

    @Test
    public void itSendsResultsToTTM() throws Exception {
        final TestCampaignApi testCampaignApi = mock(TestCampaignApi.class);
        final TuleapSendTTMResultsRunner tuleapSendTTMResultsRunner = new TuleapSendTTMResultsRunner(testCampaignApi);
        final TuleapAccessToken tuleapAccessToken = mock(TuleapAccessToken.class);
        final PrintStream logger = mock(PrintStream.class);
        final TuleapSendTTMResultsStep tuleapSendTTMResultsStep = mock(TuleapSendTTMResultsStep.class);
        final FilePath filePath = mock(FilePath.class);
        final EnvVars envVars = mock(EnvVars.class);

        final String path = "test/*.xml";
        final String campaignId = "1";
        final String buildUrl = "https://jenkins.example.com";
        final Secret secret = Secret.fromString("a-very-secret-secret");
        final List<String> results = Arrays.asList("some result 1", "some result 2");
        final FilePath path1 = mock(FilePath.class);
        final FilePath path2 = mock(FilePath.class);

        when(tuleapSendTTMResultsStep.getCampaignId()).thenReturn(campaignId);
        when(tuleapSendTTMResultsStep.getFilesPath()).thenReturn(path);
        when(tuleapAccessToken.getToken()).thenReturn(secret);
        when(envVars.get("BUILD_URL")).thenReturn(buildUrl);
        when(path1.readToString()).thenReturn("some result 1");
        when(path2.readToString()).thenReturn("some result 2");
        when(filePath.list(path)).thenReturn(new FilePath[] {path1, path2});

        tuleapSendTTMResultsRunner.run(
            tuleapAccessToken,
            logger,
            tuleapSendTTMResultsStep,
            filePath,
            envVars
        );

        verify(testCampaignApi, atMostOnce()).sendTTMResults(
            campaignId,
            buildUrl,
            results,
            secret
        );
    }
}
