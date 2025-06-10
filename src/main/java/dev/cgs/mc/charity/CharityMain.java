package dev.cgs.mc.charity;

import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
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
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

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

    // register the voicechanger plugin
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
      // add new effects here
      new HotPotatoEffect(),
      new ExampleEffect()
    );

    // register commands for testing / damage control
    new CommandAPICommand("donation")
    .withAliases("d")
    .withPermission(CommandPermission.OP)
    .withArguments(
        new StringArgument("effect")
          .replaceSuggestions(ArgumentSuggestions.strings(DonationManager.get().getKeys()))
    )
    .executes((sender, args) -> {
        String effect = (String) args.get("effect");
        try {
          DonationManager.get().start(effect);
        } catch(Error e) {
          sender.sendMessage(
            Component.text()
              .color(NamedTextColor.RED)
              .content(e.getMessage())
              .build()
          );
        }
    })
    .register();

    CommandAPICommand teamAssign = new CommandAPICommand("assign")
    .withArguments(new PlayerArgument("player"))
    .withArguments(new StringArgument("team").replaceSuggestions(ArgumentSuggestions.strings(TeamManager.get().getKeys())))
    .executes((sender, args) -> {
      Player p = (Player)args.get("player");
      String teamName = (String)args.get("team");
      Team team  = TeamManager.get().fromLeader(Team.Leader.valueOf(teamName));
      team.assign(p);
      sender.sendMessage("Success!");
    });

    new CommandAPICommand("teams")
    .withAliases("t")
    .withPermission(CommandPermission.OP)
    .withSubcommand(teamAssign)
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
  }
}
