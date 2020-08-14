package io.jenkins.plugins.tuleap_api.steps;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.tuleap_api.client.TuleapApiGuiceModule;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import org.jenkinsci.plugins.workflow.steps.*;
import org.jetbrains.annotations.NotNull;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class TuleapSendTTMResultsStep extends Step {
    private final transient String filesPath;
    private final transient String campaignId;
    private final transient String credentialId;

    @DataBoundConstructor
    public TuleapSendTTMResultsStep(
        String filesPath,
        String campaignId,
        String credentialId
    ) {
        this.filesPath = filesPath;
        this.campaignId = campaignId;
        this.credentialId = credentialId;
    }

    public String getFilesPath() {
        return filesPath;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public String getCredentialId() {
        return credentialId;
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new Execution(stepContext, this);
    }

    public static class Execution extends SynchronousStepExecution<Void> {
        private final static long serialVersionUID = 1L;
        private final transient TuleapSendTTMResultsStep tuleapSendTTMResultsStep;
        private final transient TuleapConfiguration tuleapConfiguration;
        private final transient TuleapSendTTMResultsRunner tuleapSendTTMResultsRunner;
        private final transient Run run;
        private final transient PrintStream logger;
        private final  TaskListener taskListener;
        private final  FilePath filePath;
        private final  EnvVars envVars;

        protected Execution(StepContext context, TuleapSendTTMResultsStep tuleapSendTTMResultsStep) throws IOException, InterruptedException {
            super(context);
            this.tuleapSendTTMResultsStep = tuleapSendTTMResultsStep;
            this.taskListener = context.get(TaskListener.class);
            this.logger = taskListener.getLogger();
            this.filePath = getContext().get(FilePath.class);
            this.envVars = getContext().get(EnvVars.class);
            this.run = getContext().get(Run.class);

            final Injector injector = Guice.createInjector(new TuleapApiGuiceModule());
            this.tuleapSendTTMResultsRunner = injector.getInstance(TuleapSendTTMResultsRunner.class);
            this.tuleapConfiguration = TuleapConfiguration.get();
        }

        @Override
        protected Void run() throws Exception {
            assert filePath != null;

            logger.println("Retrieving Tuleap API credentials");
            final TuleapAccessToken tuleapAccessToken = CredentialsProvider.findCredentialById(
                tuleapSendTTMResultsStep.getCredentialId(),
                TuleapAccessToken.class,
                run,
                URIRequirementBuilder.fromUri(tuleapConfiguration.getApiBaseUrl()).build()
            );

            assert tuleapAccessToken != null;

            tuleapSendTTMResultsRunner.run(
                tuleapAccessToken,
                logger,
                tuleapSendTTMResultsStep,
                filePath,
                envVars
            );

            return null;
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return new HashSet<>(
                Arrays.asList(
                    TaskListener.class,
                    FilePath.class,
                    EnvVars.class,
                    Run.class
                )
            );
        }

        @Override
        public String getFunctionName() {
            return "TuleapSendTTMResults";
        }

        @NotNull
        @Override
        public String getDisplayName() {
            return "Send Tuleap Test Management Results";
        }
    }
}
