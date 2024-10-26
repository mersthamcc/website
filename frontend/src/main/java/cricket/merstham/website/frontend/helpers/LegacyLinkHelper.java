package cricket.merstham.website.frontend.helpers;

import cricket.merstham.website.frontend.exception.ResourceHasGoneException;
import cricket.merstham.website.frontend.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.function.Supplier;

public class LegacyLinkHelper {
    private LegacyLinkHelper() {}

    public static <T> T legacyHelper(Supplier<T> supplier) throws IOException {
        try {
            return supplier.get();
        } catch (ResourceNotFoundException e) {
            throw new ResourceHasGoneException(e);
        }
    }
}
