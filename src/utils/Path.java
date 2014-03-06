package utils;

/**
 * Created by magnus on 2014-03-06.
 */
public class Path {
    public static String fileName(String pathToFile) {
        String[] parts = pathToFile.split("[/\\\\]");
        return parts[parts.length-1];
    }

    public static String baseName(String fullName) {
        String[] parts = fullName.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++){
            sb.append(parts[i]);
            if (i != parts.length-2) sb.append('.');
        }
        return sb.toString();
    }
}
