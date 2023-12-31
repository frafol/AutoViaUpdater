package velocity;

import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static common.BuildYml.createYamlFile;
import static common.UpdateVias.updateVia;

@Plugin(id = "autoviaupdater", name = "AutoViaUpdater", version = "5.0", url = "https://www.spigotmc.org/resources/autoviaupdater.109331/", authors = "NewAmazingPVP")
public final class AutoViaUpdater {

    private Toml config;
    private ProxyServer proxy;
    private File myFile;
    private Path dataDirectory;
    public boolean isViaVersionEnabled;
    public boolean isViaVersionDev;
    public boolean isViaBackwardsEnabled;
    public boolean isViaBackwardsDev;
    public boolean isViaRewindEnabled;
    public boolean isViaRewindDev;
    private final Metrics.Factory metricsFactory;

    @Inject
    public AutoViaUpdater(ProxyServer proxy, @DataDirectory Path dataDirectory, Metrics.Factory metricsFactory) {
        this.proxy = proxy;
        this.dataDirectory = dataDirectory;
        config = loadConfig(dataDirectory);
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        createYamlFile(dataDirectory.toAbsolutePath().toString(), true);
        metricsFactory.make(this, 18604);
        isViaVersionEnabled = config.getBoolean("ViaVersion.enabled");
        isViaVersionDev = config.getBoolean("ViaVersion.dev");
        isViaBackwardsEnabled = config.getBoolean("ViaBackwards.enabled");
        isViaBackwardsDev = config.getBoolean("ViaBackwards.dev");
        isViaRewindEnabled = config.getBoolean("ViaRewind.enabled");
        isViaRewindDev = config.getBoolean("ViaRewind.dev");
        updateChecker();
    }

    public void updateChecker() {
        long interval = config.getLong("Check-Interval");

        proxy.getScheduler().buildTask(this, () -> {
            try {
                if (isViaVersionEnabled && !isViaVersionDev) {
                    updateVia("ViaVersion", dataDirectory.getParent().toString(), false);
                } else if (isViaVersionEnabled && isViaVersionDev) {
                    updateVia("ViaVersion-Dev", dataDirectory.getParent().toString(), true);
                }
                if (isViaBackwardsEnabled && !isViaBackwardsDev) {
                    updateVia("ViaBackwards", dataDirectory.getParent().toString(), false);
                } else if (isViaBackwardsEnabled && isViaBackwardsDev) {
                    updateVia("ViaBackwards-Dev", dataDirectory.getParent().toString(), true);
                }
                if (isViaRewindEnabled && !isViaRewindDev) {
                    updateVia("ViaRewind", dataDirectory.getParent().toString(), false);
                } else if (isViaRewindEnabled && isViaRewindDev) {
                    updateVia("ViaRewind-Dev", dataDirectory.getParent().toString(), true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).repeat(Duration.ofMinutes(interval)).schedule();
    }

    private Toml loadConfig(Path path) {
        File folder = path.toFile();
        File file = new File(folder, "config.toml");

        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            try (InputStream input = getClass().getResourceAsStream("/" + file.getName())) {
                if (input != null) {
                    Files.copy(input, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
                return null;
            }
        }
        return new Toml().read(file);
    }
}