package rs.ananas.i18n.migration.tool.util;

public class ObjectUtil {

    private ObjectUtil() {
    }

    public static boolean isEmpty(String content) {
        return content == null ||
                content.isBlank() ||
                content.replace(" ", "").contains("{}");
    }

    public static String requireNonEmpty(String content, String message) {
        if (isEmpty(content))
            throw new IllegalArgumentException(message);
        return content;

    }
}
