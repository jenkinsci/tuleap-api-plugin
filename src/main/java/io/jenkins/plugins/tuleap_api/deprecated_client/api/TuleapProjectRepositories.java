package io.jenkins.plugins.tuleap_api.deprecated_client.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Translation of Tuleap Project's repositories
 *
 * @see <a href= https://tuleap.net/api/explorer/#!/projects/retrieveGit>https://tuleap.net/api/explorer/#!/projects/retrieveGit</a>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TuleapProjectRepositories {

    private List<TuleapGitRepository> repositories = new ArrayList<>();

    public List<TuleapGitRepository> getRepositories() {
        return Collections.unmodifiableList(repositories);
    }

    public void setRepositories(List<TuleapGitRepository> repositories) {
        this.repositories = new ArrayList<>(repositories == null ? Collections.emptyList() : repositories);
    }
}
