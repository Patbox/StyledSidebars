package eu.pb4.styledsidebars.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.pb4.predicate.api.GsonPredicateSerializer;
import eu.pb4.predicate.api.MinecraftPredicate;
import eu.pb4.styledsidebars.ModInit;
import eu.pb4.styledsidebars.config.data.ConfigData;
import eu.pb4.styledsidebars.config.data.SidebarDefinition;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedHashMap;


public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient()
            .registerTypeHierarchyAdapter(MinecraftPredicate.class, GsonPredicateSerializer.INSTANCE)
            .registerTypeHierarchyAdapter(SidebarDefinition.Line.class, new SidebarDefinition.Line.Serializer())
            .create();

    private static Config CONFIG;
    private static boolean ENABLED = false;
    private static final LinkedHashMap<String, SidebarHandler> STYLES = new LinkedHashMap<>();

    public static Config getConfig() {
        return CONFIG;
    }

    public static boolean isEnabled() {
        return ENABLED;
    }

    public static boolean loadConfig() {
        ENABLED = false;

        CONFIG = null;
        try {

            var configStyle = Paths.get("", "config", "styled-sidebars", "styles");
            var configDir = Paths.get("", "config", "styled-sidebars");

            if (Files.notExists(configStyle)) {
                Files.createDirectories(configStyle);
                Files.writeString(configStyle.resolve("default.json"), GSON.toJson(DefaultValues.exampleStyleData()));
                Files.writeString(configStyle.resolve("right_text.json"), GSON.toJson(DefaultValues.exampleStyleDataWithRight()));
                Files.writeString(configStyle.resolve("scrolling.json"), GSON.toJson(DefaultValues.exampleStyleAnimatedData()));
                Files.writeString(configStyle.resolve("pages.json"), GSON.toJson(DefaultValues.examplePagesStyleData()));
                Files.writeString(configStyle.resolve("disable.json"), GSON.toJson(DefaultValues.disabledStyleData()));
            }

            ConfigData config;

            var configFile = configDir.resolve("config.json");


            if (Files.exists(configFile)) {
                config = GSON.fromJson(new InputStreamReader(Files.newInputStream(configFile), StandardCharsets.UTF_8), ConfigData.class);
            } else {
                config = new ConfigData();
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(configFile), StandardCharsets.UTF_8));
            writer.write(GSON.toJson(config));
            writer.close();

            STYLES.clear();


            Files.list(configStyle).filter((path) -> path.toString().endsWith(".json")).forEach((fileName) -> {
                var id = fileName.getFileName().toString();
                try {
                    SidebarHandler style = new SidebarHandler(id.substring(0, id.length() - ".json".length()), GSON.fromJson(Files.readString(fileName, StandardCharsets.UTF_8), SidebarDefinition.class));
                    STYLES.put(style.id, style);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            CONFIG = new Config(config);
            ENABLED = true;
        } catch (Throwable exception) {
            ENABLED = false;
            ModInit.LOGGER.error("Something went wrong while reading config!");
            exception.printStackTrace();
        }

        return ENABLED;
    }

    public static SidebarHandler getStyle(String key) {
        return STYLES.containsKey(key) ? STYLES.get(key) : SidebarHandler.FALLBACK;
    }

    public static boolean styleExist(String key) {
        return STYLES.containsKey(key);
    }

    public static Collection<SidebarHandler> getStyles() {
        return STYLES.values();
    }

    public static String getDefault() {
        return ENABLED ? CONFIG.configData.defaultStyle : "default";
    }
}
