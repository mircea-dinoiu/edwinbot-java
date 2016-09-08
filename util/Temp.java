package util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class Temp {
    final private static String[] DIRECTORIES = {};
    final private static Object DEFAULT_LOAD_DEFAULT_OBJECT = null;
    final private static String[] DEFAULT_EXTRA_DIRECTORIES = {};
    final private static String SERIALIZED_FILE_EXTENSION = "ser";

    /**
     * Deserialize an object
     * After deserialization the temporary file will be deleted
     *
     * @param rootPath path to start from
     * @param extraDirectories extra directories between the root path and the file name
     * @param fileName file name to load the object from
     * @param defaultObject if file doesn't exist or the deserialization failed, return this object
     * @return the deserialized object if serialized object was found, default object otherwise
     */
    public static Object load(String rootPath, String[] extraDirectories, String fileName, Object defaultObject) {
        String[] loadDirectories = ArrayUtils.addAll(DIRECTORIES, extraDirectories);
        String filePath = util.Files.generatePath(
                rootPath,
                loadDirectories,
                String.format(
                        "%s.%s",
                        fileName,
                        SERIALIZED_FILE_EXTENSION
                )
        );

        try {
            InputStream fileInput = new FileInputStream(filePath);
            InputStream buffer = new BufferedInputStream(fileInput);
            ObjectInput input = new ObjectInputStream(buffer);
            Object ret = input.readObject();
            input.close();

            File file = new File(filePath);
            file.delete();

            return ret;
        } catch (Exception e) {
            return defaultObject;
        }
    }

    public static Object load(String rootPath, String fileName, Object defaultObject) {
        return load(rootPath, DEFAULT_EXTRA_DIRECTORIES, fileName, defaultObject);
    }

    public static Object load(String rootPath, String[] extraDirectories, String fileName) {
        return load(rootPath, extraDirectories, fileName, DEFAULT_LOAD_DEFAULT_OBJECT);
    }

    public static Object load(String rootPath, String fileName) {
        return load(rootPath, fileName, DEFAULT_LOAD_DEFAULT_OBJECT);
    }

    /**
     * Save object to file using serialization
     *
     * @param object the object to be saved
     * @param rootPath path to start from
     * @param extraDirectories extra directories between the root path and the file name
     * @param fileName file's name to save the object to
     */
    public static void save(Object object, String rootPath, String[] extraDirectories, String fileName) {
        String[] saveDirectories = ArrayUtils.addAll(DIRECTORIES, extraDirectories);
        String filePath = util.Files.generatePath(
                rootPath,
                saveDirectories,
                String.format(
                        "%s.%s",
                        fileName,
                        SERIALIZED_FILE_EXTENSION
                ),
                true
        );

        try {
            OutputStream fileOutput = new FileOutputStream(filePath);
            OutputStream buffer = new BufferedOutputStream(fileOutput);
            ObjectOutput output = new ObjectOutputStream(buffer);
            output.writeObject(object);
            output.close();
        } catch (Exception e) {
            System.out.println(String.format(
                    "Could not save %s",
                    filePath
            ));
        }
    }

    public static void save(Object object, String rootPath, String fileName) {
        save(object, rootPath, DEFAULT_EXTRA_DIRECTORIES, fileName);
    }
}
