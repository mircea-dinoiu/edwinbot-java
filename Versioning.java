import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import util.Strings;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Versioning {
    final private static String FILE_PATH = "src/changelog.ini";

    private static void showMenu() {
        String[] menu = {
            "[1] BUG FIX",
            "[2] ENHANCEMENT",
            "[3] BIG ENHANCEMENT",
            "[4] CHANGE REQUEST",
            "[5] BIG CHANGE REQUEST",
            "[0] exit"
        };

        System.out.println(String.format("\n%s\n", StringUtils.repeat("-", 50)));
        System.out.println(String.format("%s\n", Strings.implode("\n", menu)));
    }

    private static void listen() {
        List<String> lines = new ArrayList<>(); // Prepare the lines list
        File file = new File(FILE_PATH); // Open the changelog file
        String get,
               cDesc,
               cType = null,
               log,
               today,
               version = util.Versioning.getVersion(file); // Get the current version
        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
        List<Number> versionParts = new ArrayList<>();
        byte cTypeCode;

        showMenu();

        System.out.println(String.format("Current version: %s", version));

        try {
            System.out.print("Change type: ");
            get = bufferRead.readLine();
            cTypeCode = Byte.parseByte(get);

            if (cTypeCode < 0 || cTypeCode > 5) {
                throw new AssertionError();
            } else if (0 == cTypeCode) { // Exit?
                return;
            }

            System.out.print("Change description: ");
            get = bufferRead.readLine();
            cDesc = StringUtils.strip(get, " \t\n\r");

            if (0 == cDesc.length()) {
                System.out.println("Change description too short");
            } else {
                // Put all lines in a list
                for (String line : FileUtils.readLines(file)) {
                    line = StringUtils.strip(line, " \t\n\r");

                    if (line.length() > 0) {
                        lines.add(line);
                    }
                }

                // Get the current day
                today = new SimpleDateFormat("[dd.MM.yyyy]").format(new Date());

                // Add the current day to the log files if not already inserted
                for (String line : lines) {
                    Pattern p = Pattern.compile("(\\[[0-3][0-9]\\.[0-1][0-9]\\.[0-9][0-9][0-9][0-9]\\])");
                    Matcher m = p.matcher(line);

                    if (m.find()) {
                        if (!m.group(1).equals(today)) {
                            lines.add(0, today);
                        }
                        break;
                    }
                }

               // Set the new version
               for (String each : Strings.explode(version, ".")) {
                   versionParts.add(Integer.parseInt(each));
               }

                if (versionParts.size() < 3) {
                    versionParts.add(0);
                }
                if (versionParts.size() < 4) {
                    versionParts.add(0);
                }

                switch (cTypeCode) {
                    case 1:
                        versionParts.set(3, versionParts.get(3).intValue() + 1);
                        cType = "BUG";
                        break;
                    case 2:
                        versionParts.set(2, versionParts.get(2).intValue() + 1);
                        versionParts.set(3, 0);
                        cType = "ENH";
                        break;
                    case 3:
                        versionParts.set(1, versionParts.get(1).intValue() + 1);
                        versionParts.set(2, 0);
                        versionParts.set(3, 0);
                        cType = "ENH";
                        break;
                    case 4:
                        versionParts.set(3, versionParts.get(3).intValue() + 1);
                        cType = "CHR";
                        break;
                    case 5:
                        versionParts.set(2, versionParts.get(2).intValue() + 1);
                        versionParts.set(3, 0);
                        break;
                }

                version = util.Versioning.adjustVersion(Strings.implode(".", Strings.numbersToStrings(versionParts)));

                System.out.println(String.format("New version: %s", version));

                // Confirm
                System.out.print("Is there any error? y/n ");
                get = bufferRead.readLine();
                if (!get.trim().toLowerCase().equals("y")) {
                    // Create the log line
                    log = String.format("- %s (%s): %s", cType, version, cDesc);

                    // Insert the new log into the changelog
                    lines.add(1, log);

                    FileUtils.writeLines(file, lines);
                }
            }
        } catch (NumberFormatException | AssertionError e) {
            System.out.println("Invalid change type specified");
        } catch (Exception e) {
            e.printStackTrace();
        }

        listen();
    }

    public static void main(String[] args) {
        listen();
    }
}
