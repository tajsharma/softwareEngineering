package hwstreams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class StudentTest {
    @Test
    public void testWordCountInSingleComment() { //test case for helper function
        GitHubComment comment = new GitHubComment(
                "1",
                "commitId",
                "http://somewebsite.com",
                "author",
                "2022-01-01T12:00:00Z",
                "test here then test appears again."
        );
        long count = GitHubProc.countWordOccurrencesInSingleComment(comment, "test");
        assertEquals(2, count);
    }

    @Test //method should filter out comments that contain URLs in their bodies
    public void testCommentUrlAuthorCount() {
        Stream<GitHubComment> comments = Stream.of(
                new GitHubComment(
                        "2",
                        "commitId2",
                        "http://lebronjames.com/isthegoat",
                        "author",
                        "2000-01-02T13:00:00Z",
                        "This is a comment with a URL: http://ucdavis.org"
                ),
                new GitHubComment(
                        "3",
                        "commitId3",
                        "http://lebronjamesgoat.com/issue3",
                        "anotherAuthor",
                        "200002-01-03T14:00:00Z",
                        "This comment doesnt have URL."
                )
        );
        Map<String, Long> actual = GitHubProc.getCommentUrlAuthorCount(comments);
        Map<String, Long> expected = new HashMap<>();
        expected.put("author", 1L);
        assertEquals(expected, actual);
    }
}
