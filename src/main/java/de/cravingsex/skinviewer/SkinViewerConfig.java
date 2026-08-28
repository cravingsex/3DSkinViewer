package de.cravingsex.skinviewer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SkinViewerConfig {
    public enum Position { LEFT, RIGHT }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("skinviewer.json");
    private static Data data = new Data();

    private SkinViewerConfig() {}

    public static boolean showMenuName() { return data.showMenuName; }
    public static boolean showIngameNameTag() { return data.showIngameNameTag; }
    public static Position position() { return data.position == null ? Position.LEFT : data.position; }
    /** The menu viewer is intentionally anchored; it cannot be moved vertically. */
    public static int verticalOffset() { return 0; }
    /** The viewer has one intentional, consistent presentation size. */
    public static int viewerScale() { return 72; }
    public static void toggleMenuName() { data.showMenuName = !data.showMenuName; save(); }
    public static void toggleIngameNameTag() { data.showIngameNameTag = !data.showIngameNameTag; save(); }
    public static void togglePosition() { data.position = position() == Position.LEFT ? Position.RIGHT : Position.LEFT; save(); }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) {
                    Data loaded = GSON.fromJson(reader, Data.class);
                    if (loaded != null) data = loaded;
                }
            }
        } catch (Exception ignored) {
            data = new Data();
        }
        if (data.position == null) data.position = Position.LEFT;
        // One-time compatibility with the original single name setting.
        if (data.showMenuName == null) data.showMenuName = data.showName == null || data.showName;
        if (data.showIngameNameTag == null) data.showIngameNameTag = data.showName == null || data.showName;
        save();
    }

    public static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            try (Writer writer = Files.newBufferedWriter(FILE)) { GSON.toJson(data, writer); }
        } catch (Exception ignored) { }
    }

    private static final class Data {
        Boolean showName;
        Boolean showMenuName = true;
        Boolean showIngameNameTag = true;
        Position position = Position.LEFT;
    }
}
