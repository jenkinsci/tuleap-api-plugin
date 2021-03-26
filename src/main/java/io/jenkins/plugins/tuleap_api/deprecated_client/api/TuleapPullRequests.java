package io.jenkins.plugins.tuleap_api.deprecated_client.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TuleapPullRequests {
    private String title;
    private TuleapCommit head;
    private String branchSrc;
    private String branchDest;
    private String id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TuleapCommit getHead() {
        return head;
    }

    public void setHead(TuleapCommit commit) {
        this.head = commit;
    }

    public String getBranchSrc() {
        return branchSrc;
    }

    public String getBranchDest() {
        return branchDest;
    }

    public void setBranchSrc(String branchSrc) {
        this.branchSrc = branchSrc;
    }

    public void setBranchDest(String branchDest) {
        this.branchDest = branchDest;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
