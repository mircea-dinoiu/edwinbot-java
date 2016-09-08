package manager;

import org.apache.commons.io.FileUtils;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.util.*;

public class Help {
    final private static String[] DIRECTORIES = {"data", "help"};
    final private static String COMMANDS_FILE_NAME = "commands.json";

    private static Map<String, String> aliases = new HashMap<>();
    private static Map<String, HashMap<String, Object>> help = new LinkedHashMap<>();

    private static Set<String> commandNamesAndAliases = new HashSet<>();
    private static Set<String> orderedCommandNamesAndAliases = new TreeSet<>();

    /**
     * Purges the static maps and loads the data from JSON files
     *
     * @param rootPath project's root path
     */
    public static void load(String rootPath) {
        String rootFilePath = util.Files.generatePath(rootPath, DIRECTORIES, COMMANDS_FILE_NAME);
        JSONParser parser = new JSONParser();

        aliases.clear();
        help.clear();

        collectCommands(rootFilePath, parser);
    }

    /**
     * Check if a command exists
     *
     * @param command command name or alias
     * @return true if command is an alias or a valid command name
     */
    public static boolean commandExists(String command) {
        return (null != getCommandName(command));
    }

    /**
     * Get command data
     *
     * @param command command name or alias
     * @return map representing command data if command exists, null otherwise
     */
    public static Map<String, Object> getCommandData(String command) {
        if (!commandExists(command)) {
            return null;
        } else {
            return help.get(getCommandName(command));
        }
    }

    /**
     * Get command name by alias or command name
     *
     * @param query alias or command name
     * @return command name if exists
     */
    public static String getCommandName(String query) {
        String commandName = null;

        if (help.containsKey(query)) {
            commandName = query;
        } else if (aliases.containsKey(query)) {
            commandName = aliases.get(query);
        }

        return commandName;
    }

    /**
     * Get commands
     *
     * @return set of commands
     */
    public static Set<String> getCommandNames() {
        return help.keySet();
    }

    /**
     * Get aliases
     *
     * @return set of aliases
     */
    public static Set<String> getAliases() {
        return aliases.keySet();
    }

    /**
     * Get command names and aliases as an unordered set of strings
     *
     * @return unordered set of command names and aliases
     */
    public static Set<String> getCommandNamesAndAliases() {
        if (commandNamesAndAliases.isEmpty()) {
            commandNamesAndAliases.addAll(getCommandNames());
            commandNamesAndAliases.addAll(getAliases());
        }

        return commandNamesAndAliases;
    }

    /**
     * Get command names and aliases as an ordered set of strings
     *
     * @return ordered set of command names and aliases
     */
    public static Set<String> getOrderedCommandNamesAndAliases() {
        if (orderedCommandNamesAndAliases.isEmpty()) {
            orderedCommandNamesAndAliases.addAll(getCommandNamesAndAliases());
        }

        return orderedCommandNamesAndAliases;
    }

    /**
     * Get category/command data
     *
     * @param name category/command name
     * @param commandMap the map
     * @return category/command data
     */
    private static Map getData(String name, LinkedHashMap commandMap) {
        return (Map) commandMap.get(name);
    }

    /**
     * Category/command data represents a category
     *
     * @param data category/command data
     * @return true if the given data is a category data, false otherwise
     */
    private static boolean isCategory(String name, Map data) {
        return name.equals(name.toUpperCase())
                && data.containsKey("name")
                && data.size() == 1;
    }


    private static Map<String, String> getCommandDescription(Map description) {
        Map<String, String> commandDescription = new HashMap<>();

        for (Object jsonObjectKey : description.keySet()) {
            String language = jsonObjectKey.toString();
            String descriptionMessage;

            if (description.get(language) instanceof List) {
                descriptionMessage = util.Strings.implode("<br>", (List<String>) description.get(language));
            } else {
                descriptionMessage = (String) description.get(language);
            }

            commandDescription.put(language, descriptionMessage);
        }

        return commandDescription;
    }

    private static Map<String, ArrayList<String>> getCommandParams(List params) {
        Map<String, ArrayList<String>> newParams = new HashMap<>();

        for (Object paramObject : params) {
            Map param = (Map) paramObject;

            for (Object languageObject : ((Map) param.get("name")).keySet()) {
                String language = (String) languageObject;
                String string;

                if (!newParams.containsKey(language)) {
                    newParams.put(language, new ArrayList<>());
                }

                if (param.containsKey("hideName") && param.get("hideName").equals(true)) {
                    string = "";

                    if (param.containsKey("before")) {
                        string = (String) param.get("before");
                    }
                    if (param.containsKey("after")) {
                        if (!string.equals("")) {
                            string = String.format("%s %s", string, param.get("after"));
                        } else {
                            string = (String) param.get("after");
                        }
                    }
                    if (param.containsKey("required") && param.get("required").equals(false)) {
                        string = String.format("[%s]", string);
                    }
                } else {
                    string = String.format("<%s>", ((Map) param.get("name")).get(language));
                    if (param.containsKey("before")) {
                        string = String.format("%s %s", param.get("before"), string);
                    }
                    if (param.containsKey("after")) {
                        string = String.format("%s %s", string, param.get("after"));
                    }
                    if (param.containsKey("required") && param.get("required").equals(false)) {
                        string = String.format("[%s]", string);
                    }
                }

                newParams.get(language).add(string);
            }
        }

        return newParams;
    }

    private static List<String> getCommandRequirements(Map data) {
        List<String> requirements = new ArrayList<>();
        String baseRequirement,
               requiresUserModerator = "",
               requiresBotModerator = "";

        if (data.containsKey("admin") && data.get("admin").equals(true)) {
            baseRequirement = "Admin";
        } else {
            int level = data.containsKey("level") ? Integer.valueOf(data.get("level").toString()) : 1;

            baseRequirement = String.format("Lvl %d+", level);

            if (data.containsKey("level2")) {
                baseRequirement = String.format("%s/%d+", baseRequirement, Integer.parseInt(data.get("level2").toString()));
            }

            if (data.containsKey("orModerator") && data.get("orModerator").equals(true)) {
                baseRequirement = String.format("%s%s", baseRequirement, "|@UisM@");
            } else if (data.containsKey("requiresUserModerator") || data.containsKey("requiresBotModerator")) {
                if (data.get("requiresUserModerator").equals(true)) {
                    requiresUserModerator = "@UisM@";
                }
                if (data.get("requiresBotModerator").equals(true)) {
                    requiresBotModerator = "@BisM@";
                }
            }
        }

        requirements.add(baseRequirement);
        if (!requiresUserModerator.equals("")) {
            requirements.add(requiresUserModerator);
        }
        if (!requiresBotModerator.equals("")) {
            requirements.add(requiresBotModerator);
        }

        return requirements;
    }

    private static String getCategoryPath(String name, File currentFile) {
        String rootPath = currentFile.getParent();
        String[] directories = {name.toLowerCase()};

        return util.Files.generatePath(rootPath, directories, COMMANDS_FILE_NAME);
    }

    private static void collectCategory(
        String name,
        File currentFile,
        JSONParser parser,
        HashMap<String, HashMap> parents,
        Map data
    ) {
        HashMap<String, HashMap> parentsCopy = new LinkedHashMap<>(parents);
        parentsCopy.put(name, (HashMap) data.get("name"));

        try {
            collectCommands(getCategoryPath(name, currentFile), parser, parentsCopy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void collectCommand(String name, HashMap<String, HashMap> parents, Map data) {
        HashMap<String, Object> commandData = new HashMap<>();

        commandData.put("description", getCommandDescription((Map) data.get("description")));
        if (data.containsKey("params")) {
            commandData.put("params", getCommandParams((List) data.get("params")));
        }
        commandData.put("requirements", getCommandRequirements(data));
        commandData.put("parents", parents);
        commandData.put("pm", (null == data.get("pm") || data.get("pm").equals(true)));
        commandData.put("room", (null == data.get("room") || data.get("room").equals(true)));
        commandData.put("slow", data.containsKey("slow") && data.get("slow").equals(true));
        commandData.put("raw", data);

        if (data.containsKey("aliases")) {
            commandData.put("aliases", data.get("aliases"));
            for (Object alias : (List) data.get("aliases")) {
                aliases.put((String) alias, name);
            }
        }

        help.put(name, commandData);
    }

    private static void collectCommands(
        String filePath,
        JSONParser parser,
        HashMap<String, HashMap> parents
    ) {
        try {
            File file = new File(filePath);
            String content = FileUtils.readFileToString(file);
            ContainerFactory orderedKeyFactory = new ContainerFactory() {
                @Override
                public Map createObjectContainer() {
                    return new LinkedHashMap();
                }

                @Override
                public List creatArrayContainer() {
                    return new ArrayList<>();
                }
            };
            LinkedHashMap commandMap = (LinkedHashMap) parser.parse(content, orderedKeyFactory);

            for (Object commandKey : commandMap.keySet()) {
                String name = (String) commandKey;
                Map data = getData(name, commandMap);

                if (isCategory(name, data)) {
                    collectCategory(name, file, parser, parents, data);
                } else {
                    collectCommand(name, parents, data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void collectCommands(String filePath, JSONParser parser) {
        HashMap<String, HashMap> parents = new LinkedHashMap<>();
        collectCommands(filePath, parser, parents);
    }
}
