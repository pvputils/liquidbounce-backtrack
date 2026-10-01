package dev.pvputils.backtrack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public final class ConfigTodoAi {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("backtrackTodoAi.json");
    public boolean enabled = false;
    public double rangeMin = 1, rangeMax = 3;
    public int delayMin = 100, delayMax = 150;
    public int cooldownMin = 0, cooldownMax = 10;
    public int trackingBuffer = 500, chance = 50, attackWindow = 1000;
    public boolean pauseOnHurt = false;
    public int hurtThreshold = 3;
    public String targetMode = "attack";
    public boolean targetMobs = false;
    public static ConfigTodoAi load() {
        try {
            if (Files.exists(FILE)) {
                ConfigTodoAi config = GSON.fromJson(Files.readString(FILE), ConfigTodoAi.class);
                if (config != null) { config.validate(); return config; }
            }
        } catch (Exception exception) { BacktrackTodoAi.LOGGER.warn("Cannot load Backtrack settings; using defaults", exception); }
        return new ConfigTodoAi();
    }
    public void save() {
        try { Files.createDirectories(FILE.getParent()); Files.writeString(FILE, GSON.toJson(this)); }
        catch (Exception exception) { BacktrackTodoAi.LOGGER.error("Cannot save Backtrack settings", exception); }
    }
    public void validate() {
        if (!Double.isFinite(rangeMin) || !Double.isFinite(rangeMax) || rangeMin < 0 || rangeMax > 10 || rangeMin > rangeMax)
            throw new IllegalArgumentException("Range must satisfy 0 <= rangeMin <= rangeMax <= 10");
        pair(delayMin, delayMax, 1000, "delay"); pair(cooldownMin, cooldownMax, 2000, "cooldown");
        bounded(trackingBuffer, 2000, "trackingBuffer"); bounded(chance, 100, "chance");
        bounded(attackWindow, 5000, "attackWindow"); bounded(hurtThreshold, 10, "hurtThreshold");
        if (!"attack".equals(targetMode) && !"range".equals(targetMode)) throw new IllegalArgumentException("targetMode: attack or range");
    }
    private static void bounded(int value, int max, String name) {
        if (value < 0 || value > max) throw new IllegalArgumentException(name + " must be between 0 and " + max);
    }
    private static void pair(int min, int max, int limit, String name) {
        bounded(min, limit, name); bounded(max, limit, name);
        if (min > max) throw new IllegalArgumentException(name + " minimum exceeds maximum");
    }
    public ConfigTodoAi changed(String name, String value) {
        ConfigTodoAi copy = GSON.fromJson(GSON.toJson(this), ConfigTodoAi.class);
        switch (name) {
            case "rangeMin" -> copy.rangeMin = Double.parseDouble(value);
            case "rangeMax" -> copy.rangeMax = Double.parseDouble(value);
            case "delayMin" -> copy.delayMin = Integer.parseInt(value);
            case "delayMax" -> copy.delayMax = Integer.parseInt(value);
            case "cooldownMin" -> copy.cooldownMin = Integer.parseInt(value);
            case "cooldownMax" -> copy.cooldownMax = Integer.parseInt(value);
            case "trackingBuffer" -> copy.trackingBuffer = Integer.parseInt(value);
            case "chance" -> copy.chance = Integer.parseInt(value);
            case "attackWindow" -> copy.attackWindow = Integer.parseInt(value);
            case "hurtThreshold" -> copy.hurtThreshold = Integer.parseInt(value);
            case "pauseOnHurt" -> copy.pauseOnHurt = bool(value);
            case "targetMobs" -> copy.targetMobs = bool(value);
            case "targetMode" -> copy.targetMode = value.toLowerCase(Locale.ROOT);
            default -> throw new IllegalArgumentException("Unknown setting: " + name);
        }
        copy.validate(); return copy;
    }
    private static boolean bool(String value) {
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) throw new IllegalArgumentException("Use true or false");
        return Boolean.parseBoolean(value);
    }
    public String describe() { return GSON.toJson(this); }
}
