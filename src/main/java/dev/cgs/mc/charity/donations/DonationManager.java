package dev.cgs.mc.charity.donations;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.Team;

public class DonationManager {

    private static DonationManager instance;

    private DonationManager() {
      register(new HotPotatoEffect());
    }

    private void register(DonationEffect effect) {
      CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
      if (effect instanceof Listener) {
        Listener listener = (Listener)effect;
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
      }
    }

    /** Locks a player for this effect type. They won't receive it again until unlocked **/
    public final void lock(DonationEffect effect, Player player) {
      // TODO
    }

    /** Unlocks a player for this effect type **/
    public final void unlock(DonationEffect effect, Player player) {
      // TODO
    }

    /** Locks a team for this effect type. They won't receive it again until unlocked **/
    public final void lock(DonationEffect effect, Team team) {
      // TODO
    }

    /** Unlocks a team for this effect type **/
    public final void unlock(DonationEffect effect, Team team) {
      // TODO
    }

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("DonationManager is already initialized.");
        }
        instance = new DonationManager();
    }

    public static DonationManager get() {
        if (instance == null) {
            throw new IllegalStateException("DonationManager not initialized yet.");
        }
        return instance;
    }

    public static void cleanup() {
        instance = null;
    }
}
