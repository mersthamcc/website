package cricket.merstham.graphql.helpers;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDateTimes {
    public static Instant between(Instant startInclusive, Instant endExclusive) {
        long startSeconds = startInclusive.getEpochSecond();
        long endSeconds = endExclusive.getEpochSecond();
        long random = ThreadLocalRandom.current().nextLong(startSeconds, endSeconds);

        return Instant.ofEpochSecond(random);
    }
}
