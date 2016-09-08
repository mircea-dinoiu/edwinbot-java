package util;

import java.io.File;

public class Files {
    final private static boolean DEFAULT_GENERATE_PATH_MAKE_DIRECTORIES = false;
    final private static String DEFAULT_GENERATE_PATH_FILE_NAME = "";

    /**
     * Generate directory/file path
     *
     * If the returned value is a directory path, it will always have a leading separator
     * If the returned value is a file path, there will be no leading separator for the path
     *
     * @param rootPath the root path
     * @param directories directories between the root path and the filename
     * @param fileName file name to generate the path for
     * @param makeDirectories true to let the function create the directories
     * @return the generated path
     */
    public static String generatePath(String rootPath, String[] directories, String fileName, boolean makeDirectories) {
        String separator = File.separator;
        fileName = fileName.trim();
        rootPath = rootPath.trim();
        String pathSeparator;

        if (separator.equals(rootPath.substring(rootPath.length() - separator.length(), rootPath.length()))) {
            pathSeparator = "";
        } else {
            pathSeparator = separator;
        }

        String path = String.format(
                "%s%s",
                rootPath,
                pathSeparator
        );

        if (directories.length > 0) {
            path += util.Strings.implode(separator, directories) + separator;
        }
        if (makeDirectories) {
            File file = new File(path);
            file.mkdirs();
        }
        path += fileName;

        return path;
    }

    public static String generatePath(String rootPath, String[] directories, String fileName) {
        return generatePath(rootPath, directories, fileName, DEFAULT_GENERATE_PATH_MAKE_DIRECTORIES);
    }

    public static String generatePath(String rootPath, String fileName) {
        String[] directories = {};
        return generatePath(rootPath, directories, fileName);
    }

    public static String generatePath(String rootPath, String[] directories, boolean makeDirectories) {
        return generatePath(rootPath, directories, DEFAULT_GENERATE_PATH_FILE_NAME, makeDirectories);
    }

    public static String generatePath(String rootPath, String[] directories) {
        return generatePath(rootPath, directories, DEFAULT_GENERATE_PATH_FILE_NAME);
    }
}
