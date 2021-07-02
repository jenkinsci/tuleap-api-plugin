package io.jenkins.plugins.tuleap_api.client.exceptions;

public class ProjectNotFoundException extends Exception {
    private final String projectShortname;

    public ProjectNotFoundException(String projectShortname) {
        this.projectShortname = projectShortname;
    }

    @Override
    public String getMessage() {
        return String.format("Project %s was not found on Tuleap server", this.projectShortname);
    }
}
