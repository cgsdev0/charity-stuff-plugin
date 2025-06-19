package dev.cgs.mc.charity;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import dev.cgs.mc.charity.donations.*;
import dev.cgs.mc.charity.objectives.*;
import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CharityMain extends JavaPlugin {
  public static final String PLUGIN_ID = "charity_plugin";
  private VoicePlugin voicechatPlugin;

  public static NamespacedKey anvilKey = new NamespacedKey("charity-main", "falling-anvil");
  public static NamespacedKey waterGunKey = new NamespacedKey("charity-main", "water_gun");
  public static NamespacedKey mysticArrowKey = new NamespacedKey("charity-main", "mystic_arrow");
  public static NamespacedKey potatoKey = new NamespacedKey("charity-main", "hot_potato");

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
    World hub = creator.createWorld();
    if (hub != null) {
      hub.setPVP(false);
      hub.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
      hub.setGameRule(GameRule.DO_INSOMNIA, false);
      hub.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
      hub.setSpawnFlags(false, false);
      hub.setTime(6000);
      hub.setSpawnLocation(0, 100, 0);
      hub.getBlockAt(0, 98, 0).setType(Material.BEDROCK);
    }

    creator = new WorldCreator("parkour");
    creator.generator(new VoidChunkGenerator());
    creator.environment(World.Environment.NORMAL);
    World parkour = creator.createWorld();
    if (parkour != null) {
      parkour.setPVP(false);
      parkour.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
      parkour.setGameRule(GameRule.DO_INSOMNIA, false);
      parkour.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
      parkour.setSpawnFlags(false, false);
      parkour.setTime(6000);
      parkour.setSpawnLocation(0, 1, 0);
      parkour.getBlockAt(0, 0, 0).setType(Material.BEDROCK);
    }

    World world = getServer().getWorld("world");

    // register the voice changer plugin
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

    // clang-format off

    Donations.get().registerEffects(
      new HotPotatoEffect(),
      new SwapEffect(),
      new RotateEffect(),
      new ButterfingersEffect(),
      new AnvilRainEffect(),
      new AmongUsEffect(),
      new SweetToothEffect(),
      new SmiteEffect(),
      new MansBestFriendEffect(),
      new WaterGunEffect(),
      new OneHealthEffect(),
      new SharedHealthbarEffect(),
      new LookoutEffect(),
      new ALilToTheLeftEffect(),
      new BigScaleEffect(),
      new SmallScaleEffect(),
      new TimeSlowEffect()
    );

    Objectives.get().registerObjectives(
      new MineDiamondObjective(),
      new EnchanterObjective(),
      new ZombieDoctorObjective(),
      new LocalBreweryObjective(),
      new HeroOfVillageObjective(),
      new CavesAndCliffsObjective(),
      new HowDidWeGetHereObjective(),
      new FreeTheEndObjective(),
      new BeaconatorObjective(),
      new TrialChamberObjective(),
      new BuildHouseObjective(),
      new BuildHeadquartersObjective(),
      new BuildStableObjective(),
      new BuildFarmObjective(),
      new BuildMapWallObjective(),
      new MaxEnchantObjective(),
      new CatchFishObjective(),
      new FullIronObjective(),
      new FishEnchantedObjective(),
      new GlowberryObjective(),
      new WitherObjective(),
      new WardenObjective(),
      new TridentObjective(),
      new HeartOfTheSeaObjective()
    );
    // clang-format on

    // register commands for testing / damage control
    new CommandAPICommand("hub")
        .withPermission(CommandPermission.OP)
        .withOptionalArguments(new PlayerArgument("player"))
        .executes((sender, args) -> {
          Player player = (Player) args.get("player");
          if (player == null)
            player = (Player) sender;
          player.teleportAsync(hub.getSpawnLocation());
          player.setGameMode(GameMode.ADVENTURE);
        })
        .register();

    new CommandAPICommand("parkour")
        .withPermission(CommandPermission.OP)
        .withOptionalArguments(new PlayerArgument("player"))
        .executes((sender, args) -> {
          Player player = (Player) args.get("player");
          if (player == null)
            player = (Player) sender;
          player.teleportAsync(parkour.getSpawnLocation());
          player.setGameMode(GameMode.ADVENTURE);
        })
        .register();

    new CommandAPICommand("spawn")
        .withPermission(CommandPermission.OP)
        .withOptionalArguments(new PlayerArgument("player"))
        .executes((sender, args) -> {
          Player player = (Player) args.get("player");
          if (player == null)
            player = (Player) sender;
          player.teleportAsync(world.getSpawnLocation());
          player.setGameMode(GameMode.SURVIVAL);
        })
        .register();

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

    new CommandAPICommand("tier")
        .withPermission(CommandPermission.OP)
        .withArguments(new MultiLiteralArgument("tier",
            Stream.of(DonationEffect.Tier.values()).map(Enum::name).toArray(String[] ::new)))
        .executes((sender, args) -> {
          String tier = (String) args.get("tier");
          Donations.get().queue(DonationEffect.Tier.valueOf(tier));
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
            .withArguments(new OfflinePlayerArgument("player"))
            .withArguments(
                new MultiLiteralArgument("team", Teams.get().getKeys().toArray(String[] ::new)))
            .executes((sender, args) -> {
              OfflinePlayer p = (OfflinePlayer) args.get("player");
              String teamName = (String) args.get("team");
              Team team = Teams.get().fromLeader(Team.Leader.valueOf(teamName));
              if (p.isOnline()) {
                Player onlinePlayer = getServer().getPlayer(p.getUniqueId());
                team.assign(onlinePlayer);
                sender.sendMessage("Added online player!");
              } else {
                team.assignOffline(p);
                sender.sendMessage("Added offline player!");
              }
            });

    new CommandAPICommand("teams")
        .withAliases("t")
        .withPermission(CommandPermission.OP)
        .withSubcommand(teamAssign)
        .register();
  }
}
