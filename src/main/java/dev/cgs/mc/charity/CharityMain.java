package dev.cgs.mc.charity;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;

import dev.cgs.mc.charity.donations.DonationEffect;
import dev.cgs.mc.charity.donations.DonationManager;
import dev.cgs.mc.charity.donations.ExampleEffect;
import dev.cgs.mc.charity.donations.HotPotatoEffect;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;

public final class CharityMain extends JavaPlugin {


  public static final String PLUGIN_ID = "charity_plugin";
  private VoicePlugin voicechatPlugin;

  @Override
  public void onDisable() {
    TeamManager.onDisable();
    DonationManager.onDisable();

    if (voicechatPlugin != null) {
        getServer().getServicesManager().unregister(voicechatPlugin);
        getLogger().info("Successfully unregistered voicechat_interaction plugin");
    }
  }

  @Override
  public void onEnable() {
    BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
    if (service != null) {
        voicechatPlugin = new VoicePlugin();
        getServer().getPluginManager().registerEvents(voicechatPlugin, this);
        service.registerPlugin(voicechatPlugin);
        getLogger().info("Successfully registered voicechat plugin");
    } else {
        getLogger().info("Failed to register voicechat plugin");
    }
    TeamManager.onEnable();
    DonationManager.onEnable();

    DonationManager.get().registerEffects(
      new HotPotatoEffect(),
      new ExampleEffect()
    );

    new CommandAPICommand("donation")
    .withPermission(CommandPermission.OP)
    .withArguments(
        new StringArgument("effect")
          .replaceSuggestions(ArgumentSuggestions.strings(DonationManager.get().getKeys())),
        new StringArgument("target")
          .replaceSuggestions(
            ArgumentSuggestions.strings(info -> {
              String effect = (String) info.previousArgs().get("effect");
              DonationEffect.Meta meta = DonationManager.get().getMeta(effect);
              return new String[] {meta.targets() == DonationEffect.Target.PLAYER ? "player" : "team"};
            })
          )
    )
    .executes((sender, args) -> {
    })
    .register();
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

    // voice chat stuff
  }
}
