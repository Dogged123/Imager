package dogged.imager;

import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ImageCommand implements CommandExecutor, Listener {

    private final Imager plugin;
    private boolean stopDisplaying;

    public ImageCommand(Imager imager) {
        imager.getCommand("image").setExecutor(this);
        imager.getCommand("image").setTabCompleter(new CommandTabCompleter());
        imager.getServer().getPluginManager().registerEvents(this, imager);
        plugin = imager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("§cYou must be a player to use this command");
            return true;
        }

        if (args.length == 0) {
            p.sendMessage("§cPlease specify the name of the image you want to display");
            return true;
        }

        if (args[0].equals("webcam")) {
            String jarPath = "C:\\Users\\isaac\\IntellIJ Projects\\WebCam\\target\\WebCam-1.0-jar-with-dependencies.jar";
            String imageOutputPath = "webcam.png";

            ProcessBuilder pb = new ProcessBuilder("java", "-jar", jarPath);

            p.getInventory().addItem(new ItemStack(Material.STICK));
            Location loc = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(10).subtract(new Vector((32)/Math.sqrt(64), 0, 0)));

            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if (stopDisplaying) cancel();
                    if (count == 120*20) cancel();
                    count++;

                    //////////////////////////////////////////////////////////////////////////////////////
                    // This is better to be done with sockets. Instead of compiling this many times,    //
                    // it just needs to be compiled once to start a server, which will constantly be    //
                    // updating a BufferedImage. This will be the client, which will request the latest //
                    // image every tick                                                                 //
                    //////////////////////////////////////////////////////////////////////////////////////

                    try {
                        Process process = pb.start();
                        process.waitFor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    BufferedImage image = ImageReader.readFromFile(imageOutputPath);

                    if (image != null) {
                        image = ImageReader.resize(image, 64, 64);

                        Map<Location, Particle.DustOptions> particleLocations = calculateLocations(image, p, loc);

                        for (Location pLoc : particleLocations.keySet()) {
                            p.getWorld().spawnParticle(Particle.DUST, pLoc, 1, particleLocations.get(pLoc));
                        }
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        } else {
            if (!Arrays.asList(FileIO.getDirectoryContents("plugins/Imager/Images")).contains(args[0])) {
                p.sendMessage("§cExisting Images: " + Arrays.toString(FileIO.getDirectoryContents("plugins/Imager/Images")));
                return true;
            }

            BufferedImage image = ImageReader.readFromFile(args[0]);
            Map<Location, Particle.DustOptions> particleLocations = calculateLocations(image, p);
            p.getInventory().addItem(new ItemStack(Material.STICK));

            new BukkitRunnable() {
                int count = 0;
                @Override
                public void run() {
                    if (stopDisplaying) cancel();
                    if (count == 120*20) cancel();
                    count++;

                    for (Location pLoc : particleLocations.keySet()) {
                        p.getWorld().spawnParticle(Particle.DUST, pLoc, 1, particleLocations.get(pLoc));
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }

        return true;
    }

    @EventHandler
    public void onStickClick(PlayerInteractEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() != Material.STICK) return;

        stopDisplaying = true;
        e.getPlayer().getInventory().removeItem(item);
    }

    private Map<Location, Particle.DustOptions> calculateLocations(BufferedImage image, Player p) {
        int width = image.getWidth();
        int height = image.getHeight();

        Location loc = p.getLocation().clone().add(p.getLocation().getDirection().normalize().multiply(10).subtract(new Vector((width/2.0)/Math.sqrt(width), 0, 0)));
        stopDisplaying = false;

        Map<Location, Particle.DustOptions> particleLocations = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Location particleLoc =  loc.clone().add(x / Math.sqrt(width), (height - 1 - y) / Math.sqrt(height), 0);
                java.awt.Color color = new java.awt.Color(image.getRGB(x, y));
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), 1F);
                particleLocations.put(particleLoc, dustOptions);
            }
        }

        return particleLocations;
    }

    private Map<Location, Particle.DustOptions> calculateLocations(BufferedImage image, Player p, Location loc) {
        int width = image.getWidth();
        int height = image.getHeight();

        stopDisplaying = false;

        Map<Location, Particle.DustOptions> particleLocations = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Location particleLoc =  loc.clone().add(x / Math.sqrt(width), (height - 1 - y) / Math.sqrt(height), 0);
                java.awt.Color color = new java.awt.Color(image.getRGB(x, y));
                Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()), 1F);
                particleLocations.put(particleLoc, dustOptions);
            }
        }

        return particleLocations;
    }
}
