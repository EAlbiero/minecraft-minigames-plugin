package plugin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PluginConfig {
    private static final PluginConfig instance = new PluginConfig();

    // Settings
    private volatile boolean deleteChunkEnabled = false;
    private volatile int deleteChunkDelaySeconds = 5;
    private volatile int deleteChunkPenaltySeconds = 2;

    private PluginConfig() {}

    public static PluginConfig getInstance() {
        return instance;
    }
}
