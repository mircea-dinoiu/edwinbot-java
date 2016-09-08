package util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Versioning {
    /**
     * Get version
     */
    public static String getVersion(File file) {
        String version = "1.0";
        List<String> lines;
        Pattern log = Pattern.compile("- [A-Z]+ \\(([0-9]+[.][0-9][.]?[0-9]?[.]?[0-9]?)\\).*");
        Matcher m;

        try {
            lines = FileUtils.readLines(file);

            for (String line : lines) {
                m = log.matcher(line);

                if (m.find()) {
                    version = m.group(1);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return adjustVersion(version);
    }

    /**
     * Adjust version method
     * Increase the version indicators if one of the indicators exceeds its range
     *
     * @param version version string matching x.x.x.x string where x is a digit
     * @return adjusted version string
     */
    public static String adjustVersion(String version) {
        String[] rawVersionParts = Strings.explode(version, ".");
        List<Integer> versionParts = new ArrayList<>();
        List<String> parsedVersionParts = new ArrayList<>();

        for (String each : rawVersionParts) {
            versionParts.add(Integer.parseInt(each));
        }

        if (versionParts.size() > 3 && versionParts.get(3) > 9) {
            versionParts.set(3, 0);
            versionParts.set(2, versionParts.get(2) + 1);
        }
        if (versionParts.size() > 2 && versionParts.get(2) > 9) {
            versionParts.set(2, 0);
            versionParts.set(1, versionParts.get(1) + 1);
        }
        if (versionParts.get(1) > 9) {
            versionParts.set(1, 0);
            versionParts.set(0, versionParts.get(0) + 1);
        }

        if (versionParts.size() > 3 && 0 == versionParts.get(3)) {
            versionParts.remove(3);
            if (0 == versionParts.get(2)) {
                versionParts.remove(2);
            }
        } else if (versionParts.size() > 2 && 0 == versionParts.get(2)) {
            versionParts.remove(2);
        }

        for (Integer each : versionParts) {
            parsedVersionParts.add(each.toString());
        }

        return util.Strings.implode(".", parsedVersionParts);
    }
}
