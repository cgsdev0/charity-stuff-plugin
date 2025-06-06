package dev.cgs.mc.charity;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import dev.cgs.mc.charity.donations.DonationManager;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public final class CharityMain extends JavaPlugin {

  @Override
  public void onDisable() {
    TeamManager.cleanup();
    DonationManager.cleanup();
  }

  @Override
  public void onEnable() {
    TeamManager.init();
    DonationManager.init();

    // old stuff
    new CommandAPICommand("butterfingers")
    .withPermission(CommandPermission.OP)               // Required permissions
    .executes((sender, args) -> {
        getServer().getOnlinePlayers().forEach(player -> {
          ItemStack item = player.getInventory().getItemInMainHand();
          if (item != null && !item.isEmpty() && item.getType() != Material.POTATO) {
            Item e = player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), item);
            e.setPickupDelay(40);
            e.setThrower(player.getUniqueId());
            // e.setOwner(player.getUniqueId());
           e.setVelocity((new Vector(0,0,0.5)).rotateAroundY(-player.getYaw() * 3.1415926535 / 180.0));
            player.getInventory().setItemInMainHand(ItemStack.empty());
          }
        });
    })
    .register();
    // new CommandAPICommand("hot-potato")
    // .withArguments(new PlayerArgument("player")) // The arguments
    // .withPermission(CommandPermission.OP)               // Required permissions
    // .executes((sender, args) -> {
    //     CraftPlayer player = (CraftPlayer) args.get("player");
    // })
    // .register();
  }
}
