package dev.cgs.mc.charity.objectives;

import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.util.HashMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

@Objective.Meta(key = CatchFishObjective.key, name = "Fishing Expert",
    kind = Objective.Kind.PER_PLAYER, worth = 5, desc = "Catch 20 fish")
public class CatchFishObjective extends Objective implements Listener {
  public static final String key = "catch_20_fish";

  public HashMap<OfflinePlayer, Integer> catches = new HashMap<>();

  @EventHandler
  public void onEnchant(PlayerFishEvent event) {
    Player p = event.getPlayer();
    Entity what = event.getCaught();
    if (what == null)
      return;
    if (what instanceof Item) {
      if (!catches.containsKey(p)) {
        catches.put(p, 0);
      }
      int caught = catches.get(p) + 1;
      catches.put(p, caught);
      if (caught < 20)
        return;
      Team t = Teams.get().fromPlayer(p);
      if (t == null)
        return;
      t.unlock(key, p);
    }
  }
}
