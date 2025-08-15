package dogged.imager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        List<String> subCommands = new ArrayList<>();

        if (command.toString().contains("image")) {
            subCommands.addAll(Arrays.asList(FileIO.getDirectoryContents("plugins/Imager/Images")));
            subCommands.add("webcam");
        }

        return subCommands;
    }
}
