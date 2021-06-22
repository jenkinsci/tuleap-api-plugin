package io.jenkins.plugins.tuleap_api.client.internals.entities;

import io.jenkins.plugins.tuleap_api.client.GitTreeContent;
import junit.framework.TestCase;

public class GitTreeContentEntityTest extends TestCase {

    public void testItSetsGitTreeContentTypeToTreeIfTree() {
        GitTreeContent content = new GitTreeContentEntity(
            "whatever_id",
            "whatever_name",
            "whatever_path",
            "tree",
            "whatever_mode"
        );

        assertEquals(GitTreeContent.ContentType.TREE, content.getType());
    }

    public void testItSetsGitTreeContentTypeToSymlinkIfNotTreeAnd120000Mode() {
        GitTreeContent content = new GitTreeContentEntity(
            "whatever_id",
            "whatever_name",
            "whatever_path",
            "blob",
            "120000"
        );

        assertEquals(GitTreeContent.ContentType.SYMLINK, content.getType());
    }

    public void testItSetsGitTreeContentTypeToBlobOtherwise() {
        GitTreeContent content = new GitTreeContentEntity(
            "whatever_id",
            "whatever_name",
            "whatever_path",
            "blob",
            "whatever_mode"
        );

        assertEquals(GitTreeContent.ContentType.BLOB, content.getType());
    }
}
