package dogged.imager;

import org.bukkit.plugin.java.JavaPlugin;

public final class Imager extends JavaPlugin {

    @Override
    public void onEnable() {
        new ImageCommand(this);
        FileIO.makeDirectory("plugins/Imager/Images");
    }
}
