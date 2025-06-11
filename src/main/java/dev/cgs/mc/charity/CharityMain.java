package dev.cgs.mc.charity;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.cgs.mc.charity.donations.*;
import dev.cgs.mc.charity.objectives.*;
import dev.cgs.mc.charity.objectives.Objectives;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class CharityMain extends JavaPlugin {
  public static final String PLUGIN_ID = "charity_plugin";
  private VoicePlugin voicechatPlugin;

  @Override
  public void onDisable() {
    Teams.onDisable();
    Donations.onDisable();
    Objectives.onDisable();

    if (voicechatPlugin != null) {
      getServer().getServicesManager().unregister(voicechatPlugin);
      getLogger().info("Successfully unregistered voicechat_interaction plugin");
    }
  }

  @Override
  public void onEnable() {
    // register the voicechanger plugin
    BukkitVoicechatService service =
        getServer().getServicesManager().load(BukkitVoicechatService.class);
    if (service != null) {
      voicechatPlugin = new VoicePlugin();
      getServer().getPluginManager().registerEvents(voicechatPlugin, this);
      service.registerPlugin(voicechatPlugin);
      getLogger().info("Successfully registered voicechat plugin");
    } else {
      getLogger().info("Failed to register voicechat plugin");
    }

    Teams.onEnable();
    Donations.onEnable();
    Objectives.onEnable();

    Donations.get().registerEffects(
        // add new effects here
        new HotPotatoEffect(), new SwapEffect(), new RotateEffect());

    Objectives.get().registerObjectives(new MineDiamondObjective());

    // register commands for testing / damage control
    new CommandAPICommand("donation")
        .withAliases("d")
        .withPermission(CommandPermission.OP)
        .withArguments(
            new MultiLiteralArgument("effect", Donations.get().getKeys().toArray(String[] ::new)))
        .executes((sender, args) -> {
          String effect = (String) args.get("effect");
          try {
            Donations.get().start(effect);
          } catch (Error e) {
            sender.sendMessage(
                Component.text().color(NamedTextColor.RED).content(e.getMessage()).build());
          }
        })
        .register();

    new CommandAPICommand("objective")
        .withAliases("o")
        .withPermission(CommandPermission.OP)
        .withArguments(new MultiLiteralArgument(
            "objective", Objectives.get().getKeys().toArray(String[] ::new)))
        .withArguments(new PlayerArgument("player"))
        .executes((sender, args) -> {
          String objective = (String) args.get("objective");
          Player player = (Player) args.get("player");
          Teams.get().fromPlayer(player).unlock(objective, player);
          Teams.get().saveData();
        })
        .register();

    CommandAPICommand teamAssign =
        new CommandAPICommand("assign")
            .withArguments(new PlayerArgument("player"))
            .withArguments(
                new MultiLiteralArgument("team", Teams.get().getKeys().toArray(String[] ::new)))
            .executes((sender, args) -> {
              Player p = (Player) args.get("player");
              String teamName = (String) args.get("team");
              Team team = Teams.get().fromLeader(Team.Leader.valueOf(teamName));
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
        .withPermission(CommandPermission.OP) // Required permissions
        .executes((sender, args) -> {
          getServer().getOnlinePlayers().forEach(player -> {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item != null && !item.isEmpty() && item.getType() != Material.POTATO) {
              Item e =
                  player.getWorld().dropItem(player.getLocation().add(new Vector(0, 1, 0)), item);
              e.setPickupDelay(40);
              e.setThrower(player.getUniqueId());
              // e.setOwner(player.getUniqueId());
              e.setVelocity(
                  (new Vector(0, 0, 0.5)).rotateAroundY(-player.getYaw() * 3.1415926535 / 180.0));
              player.getInventory().setItemInMainHand(ItemStack.empty());
            }
          });
        })
        .register();
  }
}
