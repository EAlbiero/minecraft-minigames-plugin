package plugin.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class PluginConfig {
    @Getter
    private static final PluginConfig instance = new PluginConfig();

    // Settings

    // Chunk Deletion
    private volatile boolean deleteChunkEnabled = false;
    private volatile int deleteChunkDelaySeconds = 5;
    private volatile int deleteChunkPenaltySeconds = 2;
    private volatile int chunkHighlightIntervalTicks = 10;

    private PluginConfig() {}

}
