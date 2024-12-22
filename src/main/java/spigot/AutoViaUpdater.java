package spigot;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

import static common.BuildYml.updateBuildNumber;
import static common.UpdateVias.updateVia;
import static common.BuildYml.createYamlFile;

public final class AutoViaUpdater extends JavaPlugin {

    private FileConfiguration config;
    public boolean isViaVersionEnabled;
    public boolean isViaVersionDev;
    public boolean isViaVersionJava8;
    public boolean isViaBackwardsEnabled;
    public boolean isViaBackwardsDev;
    public boolean isViaBackwardsJava8;
    public boolean isViaRewindEnabled;
    public boolean isViaRewindDev;
    public boolean isViaRewindJava8;
    public boolean isViaRewindLegacyEnabled;
    public boolean isViaRewindLegacyDev;

    @Override
    public void onEnable() {
        new Metrics(this, 18603);
        loadConfiguration();
        createYamlFile(getDataFolder().getAbsolutePath(), false);
        isViaVersionEnabled = getConfig().getBoolean("ViaVersion.enabled");
        isViaVersionDev = getConfig().getBoolean("ViaVersion.dev");
        isViaVersionJava8 = getConfig().getBoolean("ViaVersion.java8");
        isViaBackwardsEnabled = getConfig().getBoolean("ViaBackwards.enabled");
        isViaBackwardsDev = getConfig().getBoolean("ViaBackwards.dev");
        isViaBackwardsJava8 = getConfig().getBoolean("ViaBackwards.java8");
        isViaRewindEnabled = getConfig().getBoolean("ViaRewind.enabled");
        isViaRewindDev = getConfig().getBoolean("ViaRewind.dev");
        isViaRewindJava8 = getConfig().getBoolean("ViaRewind.java8");
        isViaRewindLegacyEnabled = getConfig().getBoolean("ViaRewind-Legacy.enabled");
        isViaRewindLegacyDev = getConfig().getBoolean("ViaRewind-Legacy.dev");
    }

    @Override
    public void onDisable() {
        updateChecker();
    }

    public void updateChecker() {
        config = getConfig();
        checkUpdateVias();
    }

    public void checkUpdateVias(){
        try {
            System.out.println("Checking for updates...");
            if (getServer().getPluginManager().getPlugin("ViaVersion") == null) {
                updateBuildNumber("ViaVersion", -1);
            }
            if (getServer().getPluginManager().getPlugin("ViaBackwards") == null) {
                updateBuildNumber("ViaBackwards", -1);
            }
            if (getServer().getPluginManager().getPlugin("ViaRewind") == null) {
                updateBuildNumber("ViaRewind", -1);
            }
            if (getServer().getPluginManager().getPlugin("ViaRewind-Legacy-Support") == null) {
                updateBuildNumber("ViaRewind%20Legacy%20Support", -1);
            }
            if (isViaVersionEnabled) {
                update("ViaVersion", isViaVersionDev, isViaVersionJava8);
            }
            if (isViaBackwardsEnabled) {
                update("ViaBackwards", isViaBackwardsDev, isViaBackwardsJava8);
            }
            if (isViaRewindEnabled) {
                update("ViaRewind", isViaRewindDev, isViaRewindJava8);
            }
            if (isViaRewindLegacyEnabled) {
                update("ViaRewind%20Legacy%20Support", isViaRewindLegacyDev, false);
            }
            System.out.println("Updates are checked and installed correctly.");
        } catch (IOException e) {
            System.out.println("Error checking for updates:");
            e.printStackTrace();
        }
    }

    private void update(String pluginName, boolean isDev, boolean isJava8) throws IOException {
        String pluginKey = isJava8 ? pluginName + "-Java8" : (isDev ? pluginName + "-Dev" : pluginName);
        updateVia(pluginKey, getDataFolder().getParent(), isDev, isJava8);
    }

    public void loadConfiguration(){
        saveDefaultConfig();
        config = getConfig();
        config.addDefault("ViaVersion.enabled", true);
        config.addDefault("ViaVersion.dev", false);
        config.addDefault("ViaBackwards.enabled", true);
        config.addDefault("ViaBackwards.dev", false);
        config.addDefault("ViaRewind.enabled", true);
        config.addDefault("ViaRewind.dev", false);
        config.addDefault("ViaRewind-Legacy.enabled", true);
        config.options().copyDefaults(true);
        saveConfig();
    }
}
