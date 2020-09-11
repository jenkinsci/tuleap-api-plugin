package io.jenkins.plugins.tuleap_api.steps;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.tuleap_api.client.TuleapApiGuiceModule;
import io.jenkins.plugins.tuleap_api.client.internals.entities.BuildStatus;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TuleapNotifyCommitStatusStep extends Step {
    private final BuildStatus status;
    private final String repositoryId;
    private final String credentialId;

    @DataBoundConstructor
    public TuleapNotifyCommitStatusStep(BuildStatus status, String repositoryId, String credentialId) {
        this.status = status;
        this.repositoryId = repositoryId;
        this.credentialId = credentialId;
    }

    public BuildStatus getStatus() {
        return status;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new TuleapNotifyCommitStatusStepExecution(context, this);
    }

    public static class TuleapNotifyCommitStatusStepExecution extends SynchronousStepExecution<Void> {
        private static final long serialVersionUID = 1L;
        private final transient Run<?, ?> run;
        private final transient TuleapNotifyCommitStatusStep step;
        private final transient PrintStream logger;
        private final transient TuleapConfiguration tuleapConfiguration;
        private final transient TuleapNotifyCommitStatusRunner tuleapNotifyCommitStatusRunner;

        TuleapNotifyCommitStatusStepExecution(StepContext context, TuleapNotifyCommitStatusStep tuleapNotifyCommitStatusStep) throws Exception {
            super(context);
            this.step = tuleapNotifyCommitStatusStep;
            run = getContext().get(Run.class);
            logger = getContext().get(TaskListener.class).getLogger();

            final Injector injector = Guice.createInjector(new TuleapApiGuiceModule());
            this.tuleapNotifyCommitStatusRunner = injector.getInstance(TuleapNotifyCommitStatusRunner.class);
            this.tuleapConfiguration = TuleapConfiguration.get();
        }

        @Override
        protected Void run() {
            logger.println("Retrieving Tuleap API credentials");
            final StringCredentials credential = CredentialsProvider.findCredentialById(
                step.getCredentialId(),
                StringCredentials.class,
                run,
                URIRequirementBuilder.fromUri(tuleapConfiguration.getApiBaseUrl()).build()
            );

            assert credential != null;

            tuleapNotifyCommitStatusRunner.run(
                credential,
                logger,
                run,
                step
            );

            return null;
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        @Override
        public String getDisplayName() {
            return "Update the build status of the commit in Tuleap";
        }

        @Override
        public String getFunctionName() {
            return "tuleapNotifyCommitStatus";
        }

        @Override
        public Set<Class<?>> getRequiredContext() {
            return new HashSet<>(
                Arrays.asList(
                    TaskListener.class,
                    Run.class
                )
            );
        }
    }
}
