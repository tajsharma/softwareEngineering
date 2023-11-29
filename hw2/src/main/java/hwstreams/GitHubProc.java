package hwstreams;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;

public class GitHubProc {
  //helper class to count occurrences in a single comment
  static Long countWordOccurrencesInSingleComment(GitHubComment comment, String word) {
    return Arrays.stream(Util.getWords(comment.body())) // Convert the body of the comment to a stream of words
            .filter(token -> token.equals(word)) // Filter the stream to only the specified word
            .count(); //keep track of count
  }

  public static Long getWordCount(Stream<GitHubComment> comments, String word) {
    return comments // first, map each comment to its word count
            .parallel() //enables parallel computation
            .map(comment -> countWordOccurrencesInSingleComment(comment, word))
            .reduce(0L, Long::sum); // sum them all
  }


  public static Map<String, Long> getPerProjectCount(Stream<GitHubComment> comments) {
    return comments
            .parallel()
            .collect(Collectors.groupingBy(
                    comment -> Util.getProject(comment), // Pass the entire GitHubComment object
                    Collectors.counting() // Count the number of comments per project
            ));
  }


  public static Map<String, Long> getAuthorActivity(Stream<GitHubComment> comments) {
    return comments
            .parallel()
            .collect(Collectors.groupingBy(
                    GitHubComment::author, // Group by the author of the comment
                    Collectors.counting()  // Count the number of comments per author
            ));
  }


  public static Map<String, Long> getCommentUrlAuthorCount(Stream<GitHubComment> comments) {
    return comments
            .parallel()
            .filter(comment -> comment.body().contains("http://") || comment.body().contains("https://")) // Filter comments with URLs
            .collect(Collectors.groupingBy(
                    GitHubComment::author, // Group by the author
                    Collectors.counting()  // Count the comments per author
            ));
  }


  public static Stream<GitHubComment> filterCommentsWithUrl(Stream<GitHubComment> comments) {
    return comments
            .parallel()
            .filter(comment -> comment.body().contains("http://") || comment.body().contains("https://"));
  }


  public static Map<String, Double> getAuthorAverageVerbosity(Stream<GitHubComment> comments) {
    return comments
            .parallel()
            .collect(Collectors.groupingBy(
                    GitHubComment::author, // Group by author
                    Collectors.averagingInt(comment -> Util.getWords(comment.body()).length) // Average length of comments
            ));
  }


  public static Map<String, Map<String, Long>> getAuthorWordCountPerProject(
          Stream<GitHubComment> comments, String word) {
    return comments
            .parallel()
            .collect(Collectors.groupingBy(
                    comment -> Util.getProject(comment), // Group by project
                    Collectors.groupingBy(
                            GitHubComment::author, // Group by author within each project
                            Collectors.summingLong(comment ->
                                    Arrays.stream(Util.getWords(comment.body()))
                                            .filter(token -> token.equals(word))
                                            .count()) // Count occurrences of the word
                    )
            ));
  }
}
