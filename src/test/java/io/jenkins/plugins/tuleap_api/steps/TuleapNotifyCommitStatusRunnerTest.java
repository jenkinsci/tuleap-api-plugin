package io.jenkins.plugins.tuleap_api.steps;

import hudson.model.Run;
import hudson.plugins.git.util.Build;
import hudson.plugins.git.util.BuildData;
import hudson.util.Secret;
import io.jenkins.plugins.tuleap_api.client.GitApi;
import io.jenkins.plugins.tuleap_api.client.internals.entities.BuildStatus;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import org.eclipse.jgit.lib.ObjectId;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.junit.Test;

import java.io.PrintStream;

import static org.mockito.Mockito.*;

public class TuleapNotifyCommitStatusRunnerTest {

    @Test
    public void itSendsCommitBuildResultToTuleap() throws Exception {
        final GitApi gitApi = mock(GitApi.class);
        final TuleapNotifyCommitStatusRunner tuleapNotifyCommitStatusRunner = new TuleapNotifyCommitStatusRunner(gitApi);
        final StringCredentials credential = mock(StringCredentials.class);
        final PrintStream logger = mock(PrintStream.class);
        final TuleapNotifyCommitStatusStep tuleapNotifyCommitStatusStep = mock(TuleapNotifyCommitStatusStep.class);
        final Run run = mock(Run.class);
        final BuildData buildData = mock(BuildData.class);
        final Build build = mock(Build.class);
        final ObjectId sha1 = mock(ObjectId.class);

        final String repositoryId = "1";
        final String aSha1Value = "someSha1ValueThatMatters";
        final Secret secret = Secret.fromString("a-very-secret-secret");

        buildData.lastBuild = build;
        when(tuleapNotifyCommitStatusStep.getRepositoryId()).thenReturn(repositoryId);
        when(tuleapNotifyCommitStatusStep.getStatus()).thenReturn(BuildStatus.success);
        when(run.getAction(BuildData.class)).thenReturn(buildData);
        when(build.getSHA1()).thenReturn(sha1);
        when(sha1.name()).thenReturn(aSha1Value);
        when(credential.getSecret()).thenReturn(secret);

        tuleapNotifyCommitStatusRunner.run(
            credential,
            logger,
            run,
            tuleapNotifyCommitStatusStep
        );

        verify(gitApi, atMostOnce()).sendBuildStatus(
            repositoryId,
            aSha1Value,
            BuildStatus.success,
            secret
        );
    }
}
