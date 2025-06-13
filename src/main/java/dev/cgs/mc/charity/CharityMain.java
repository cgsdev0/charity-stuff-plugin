package dev.cgs.mc.charity;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.cgs.mc.charity.donations.*;
import dev.cgs.mc.charity.objectives.*;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
    WorldCreator creator = new WorldCreator("team_selection");
    creator.generator(new VoidChunkGenerator());
    creator.environment(World.Environment.NORMAL);
    World world = creator.createWorld();
    if (world != null) {
      world.setSpawnLocation(0, 100, 0);
      world.getBlockAt(0, 99, 0).setType(Material.BEDROCK);
    }

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

    for (World w : getServer().getWorlds()) {
      w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
      w.setGameRule(GameRule.KEEP_INVENTORY, true);
    }

    Donations.get().registerEffects(
        // add new effects here
        new HotPotatoEffect(), new SwapEffect(), new RotateEffect(), new ButterfingersEffect()
    );

    Objectives.get().registerObjectives(new MineDiamondObjective(), new EnchanterObjective(),
        new ZombieDoctorObjective(), new LocalBreweryObjective(), new HeroOfVillageObjective(),
        new CavesAndCliffsObjective(), new HowDidWeGetHereObjective(), new FreeTheEndObjective(),
        new BeaconatorObjective(), new TrialChamberObjective(), new BuildHouseObjective(),
        new BuildHeadquartersObjective(), new BuildStableObjective(), new BuildFarmObjective(),
        new BuildMapWallObjective(), new MaxEnchantObjective(), new CatchFishObjective()

    );

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
  }
}
