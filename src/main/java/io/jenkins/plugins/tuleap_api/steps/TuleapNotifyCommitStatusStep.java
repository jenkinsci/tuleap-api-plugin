package io.jenkins.plugins.tuleap_api.steps;

import com.cloudbees.plugins.credentials.CredentialsMatchers;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardListBoxModel;
import com.cloudbees.plugins.credentials.domains.URIRequirementBuilder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import hudson.Extension;
import hudson.model.Item;
import hudson.model.Queue;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.security.ACL;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.tuleap_api.Messages;
import io.jenkins.plugins.tuleap_api.client.TuleapApiGuiceModule;
import io.jenkins.plugins.tuleap_api.client.internals.entities.TuleapBuildStatus;
import io.jenkins.plugins.tuleap_credentials.TuleapAccessToken;
import io.jenkins.plugins.tuleap_server_configuration.TuleapConfiguration;
import org.jenkinsci.plugins.plaincredentials.StringCredentials;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.verb.POST;

import java.io.PrintStream;
import java.io.Serial;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TuleapNotifyCommitStatusStep extends Step {
    private final TuleapBuildStatus status;
    private final String repositoryId;
    private final String credentialId;

    @DataBoundConstructor
    public TuleapNotifyCommitStatusStep(TuleapBuildStatus status, String repositoryId, String credentialId) {
        this.status = status;
        this.repositoryId = repositoryId;
        this.credentialId = credentialId;
    }

    public TuleapBuildStatus getStatus() {
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
        @Serial
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

            StringCredentials ciToken= getCIToken();
            if (ciToken != null) {
                tuleapNotifyCommitStatusRunner.run(
                    ciToken,
                    logger,
                    run,
                    step
                );

                return null;
            }

            TuleapAccessToken accessKey = getTuleapAccessKey();
            if (accessKey == null) {
                throw new RuntimeException(
                    "Credentials could not be retrieved using the provided credential id. Please check your Jenkinsfile."
                );
            }

            tuleapNotifyCommitStatusRunner.run(
                accessKey,
                logger,
                run,
                step
            );

            return null;
        }

        @Nullable
        private TuleapAccessToken getTuleapAccessKey() {
            return CredentialsProvider.findCredentialById(
                step.getCredentialId(),
                TuleapAccessToken.class,
                run,
                URIRequirementBuilder.fromUri(tuleapConfiguration.getApiBaseUrl()).build()
            );
        }

        @Nullable
        private StringCredentials getCIToken() {
            return CredentialsProvider.findCredentialById(
                step.getCredentialId(),
                StringCredentials.class,
                run,
                URIRequirementBuilder.fromUri(tuleapConfiguration.getApiBaseUrl()).build()
            );
        }
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {
        @Override
        public String getDisplayName() {
            return Messages.TuleapNotifyCommitStatusStep_displayName();
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

        public ListBoxModel doFillStatusItems() {
            ListBoxModel options = new ListBoxModel();
            options.add(Messages.TuleapNotifyCommitStatusStep_success(), TuleapBuildStatus.success.name());
            options.add(Messages.TuleapNotifyCommitStatusStep_failure(), TuleapBuildStatus.failure.name());
            options.add(Messages.TuleapNotifyCommitStatusStep_pending(), TuleapBuildStatus.pending.name());
            return options;
        }

        @POST
        public ListBoxModel doFillCredentialIdItems(@CheckForNull @AncestorInPath Item context,
                                                    @QueryParameter String apiUri) {

            if (context != null && context.hasPermission(Item.CONFIGURE)) {
                return new StandardListBoxModel().includeMatchingAs(
                    context instanceof Queue.Task ? ((Queue.Task) context).getDefaultAuthentication2() : ACL.SYSTEM2,
                    context, StringCredentials.class, URIRequirementBuilder.fromUri(apiUri).build(), CredentialsMatchers.instanceOf(StringCredentials.class)).includeEmptyValue();
            }
            return new StandardListBoxModel().includeEmptyValue();
        }
    }
}
