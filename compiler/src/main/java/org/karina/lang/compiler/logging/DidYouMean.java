package org.karina.lang.compiler.logging;

import java.util.*;

/**
 * Helper for finding suggestions for typos.
 */
public final class DidYouMean {

    private DidYouMean() {}

    /**
     * Find the best suggestions for a target string from a set of available strings.
     * @param limit the maximum number of suggestions to return
     */
    public static List<String> suggestions(Set<String> available, String target, int limit) {

        return available
                .stream()
                .sorted(Comparator.comparingInt(x -> calculate(x, target)))
                .limit(limit).toList();

    }


    /**
     * Calculate the Levenshtein distance between two strings.
     * Source: <a href="https://github.com/eugenp/tutorials/tree/master/algorithms-modules/algorithms-miscellaneous-9/src/main/java/com/baeldung/algorithms/editdistance">github.com</a>
     */
    private static int calculate(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0)
                    dp[i][j] = j;

                else if (j == 0)
                    dp[i][j] = i;

                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1, dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    static int min(int... numbers) {
        return Arrays.stream(numbers)
                     .min().orElse(Integer.MAX_VALUE);
    }
}
