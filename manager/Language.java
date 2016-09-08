package manager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.*;

public class Language {
    final private static String[] DIRECTORIES = {"data", "language"};
    final private static String[] EXTENSIONS = {"json"};
    final private static String VARIABLE_PREFIX = "{";
    final private static String VARIABLE_SUFFIX = "}";

    private static Map<String, ArrayList<HashMap<String, String>>> languageMap = new HashMap<>();

    public static void load(String rootPath) {
        String directoryPath = util.Files.generatePath(rootPath, DIRECTORIES);
        File directory = new File(directoryPath);
        Iterator<File> files = FileUtils.iterateFiles(directory, EXTENSIONS, true);

        languageMap.clear();


        walkFiles(files);
    }

    /**
     * Get language string
     *
     * @param key key for language string
     * @param language language short name ("en", "ro")
     * @param valuesMap map of values to be applied on the template string
     * @return demanded message
     */
    public static String get(String key, String language, Map<String, Object> valuesMap) {
        try {
            List<HashMap<String, String>> variants = languageMap.get(key);
            String template;
            int choice = 0;

            if (variants.size() > 1) {
                Random random = new Random();
                choice = random.nextInt(variants.size());
            }

            template = variants.get(choice).get(language);

            return StrSubstitutor.replace(template, valuesMap, VARIABLE_PREFIX, VARIABLE_SUFFIX);
        } catch (Exception e) {
            return null;
        }
    }

    public static String get(String key, String language) {
        Map<String, Object> valuesMap = new HashMap<>();
        return get(key, language, valuesMap);
    }

    private static String getMapValueVariantMessage(String mapKey, String language, JSONObject jsonVariant) {
        String message;

        if (jsonVariant.get(language) instanceof JSONArray) {
            List<String> lines = (List<String>) jsonVariant.get(language);
            message = util.Strings.implode("<br>", lines);
        } else {
            message = (String) jsonVariant.get(language);
        }

        if (mapKey.startsWith("ERROR_")) {
            message = String.format(
                "%s %s",
                util.Render.highlight("✖", "Red"),
                message
            );
        } else if (mapKey.startsWith("SUCCESS_")) {
            message = String.format(
                "%s %s",
                util.Render.highlight("✔", "Green"),
                message
            );
        }

        return message;
    }

    private static HashMap<String, String> getMapValueVariant(JSONObject jsonVariant, String mapKey) {
        HashMap<String, String> mapValueVariant = new HashMap<>();

        for (Object languageObject : jsonVariant.keySet()) {
            String language = (String) languageObject;
            String message = getMapValueVariantMessage(mapKey, language, jsonVariant);

            mapValueVariant.put(language, message);
        }

        return mapValueVariant;
    }

    private static ArrayList<HashMap<String, String>> getMapValue(String mapKey, JSONObject jsonObject) {
        ArrayList<HashMap<String, String>> mapValue = new ArrayList<>();
        Object jsonObjectValue = jsonObject.get(mapKey);
        JSONArray jsonVariants;

        if (jsonObjectValue instanceof JSONArray) {
            jsonVariants = (JSONArray) jsonObjectValue;
        } else {
            jsonVariants = new JSONArray();
            jsonVariants.add(jsonObjectValue);
        }

        for (Object jsonVariant : jsonVariants) {
            mapValue.add(getMapValueVariant((JSONObject) jsonVariant, mapKey));
        }


        return mapValue;
    }

    private static void updateLanguageMap(String content, JSONParser parser) {
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(content);

            for (Object jsonObjectKey : jsonObject.keySet()) {
                String mapKey = (String) jsonObjectKey;
                languageMap.put(mapKey, getMapValue(mapKey, jsonObject));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void walkFiles(Iterator<File> files) {
        JSONParser parser = new JSONParser();

        while (files.hasNext()) {
            File file = files.next();
            try {
                String content = FileUtils.readFileToString(file);
                updateLanguageMap(content, parser);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}