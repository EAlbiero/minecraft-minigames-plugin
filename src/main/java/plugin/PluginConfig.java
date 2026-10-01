package plugin;

public class PluginConfig {
    private boolean deleteChunk = false;

    public PluginConfig() {}
    public boolean getDeleteChunk() {
        return this.deleteChunk;
    }
    public void setDeleteChunk(boolean newValue) {
        this.deleteChunk = newValue;
    }
}
