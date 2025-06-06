package dev.cgs.mc.charity.donations;

import java.util.List;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.Team;
import dev.cgs.mc.charity.TeamManager;
import dev.cgs.mc.charity.donations.DonationEffect.Target;
import dev.cgs.mc.charity.donations.DonationEffect.Kind;

@DonationEffect.Meta(
  key="hot-potato",
  name="Hot Potato",
  targets=Target.PLAYER,
  kind=Kind.NEGATIVE
)
public class HotPotatoEffect extends DonationEffect implements Listener {

  private class TeamState {
    public BossBar bossBar;
    public UUID holder;
  }

  private HashMap<Team.Leader, TeamState> states = new HashMap<>();

  private TeamState getState(Team team) {
    Team.Leader key = team.getLeader();
    if (!states.containsKey(key)) {
      TeamState state = new TeamState();
      state.bossBar = Bukkit.createBossBar("Hot Potato", BarColor.RED, BarStyle.SEGMENTED_20);
      state.bossBar.removeAll();
      states.put(key, state);
      return state;
    }
    return states.get(key);
  }

  private TeamState getState(Player player) {
    Team team = TeamManager.get().fromPlayer(player);
    return getState(team);
  }

  @Override
  public void start(Team team, List<Player> affected) {
    TeamState state = getState(team);
    CharityMain plugin = JavaPlugin.getPlugin(CharityMain.class);
    lock(team);
    CraftPlayer player = (CraftPlayer)affected.get(0);
    state.bossBar.setProgress(1.0);
    plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
      double new_progress = Math.max(state.bossBar.getProgress() - 0.004, 0.0);
      state.bossBar.setProgress(new_progress);
      if (new_progress == 0.0) {
        Player p = plugin.getServer().getPlayer(state.holder);
        if (p != null && p instanceof CraftPlayer) {
          this.explodePlayer((CraftPlayer)p);
        }
      }
    }, 0L, 1L);
    givePotato(player);
  }

  static String POTATO_LORE = "It's really hot!";

  private static boolean isPotato(ItemStack stack) {
    if (stack == null) return false;
    ItemMeta im = stack.getItemMeta();
    if (im != null &&  im.hasLore()) {
      List<String> lore = im.getLore();
      if (lore.contains(POTATO_LORE)) {
        return true;
      }
    }
    return false;
  }

  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    Item dropped = event.getItemDrop();
    if (HotPotatoEffect.isPotato(dropped.getItemStack())) {
        event.setCancelled(true);
      }
  }


  public void givePotato(CraftPlayer player) {
    TeamState state = getState(player);
    state.holder = player.getUniqueId();
    state.bossBar.addPlayer(player);
    // Step 1. check if inventory is full
    ItemStack potato = new ItemStack(Material.POTATO);
    ItemMeta meta = potato.getItemMeta();
    List<String> lore = new ArrayList<>();
    lore.add(POTATO_LORE);
    meta.setLore(lore);
    potato.setItemMeta(meta);
    PlayerInventory inv = player.getInventory();
    ItemStack hand = inv.getItemInMainHand();
    if (hand != null && !hand.isEmpty()) {
      boolean swapped = false;
      int i = -1;
      for (ItemStack slot : inv) {
        i++;
        if (i >= 36 && i <= 39) continue;
        if (slot != null && slot.equals(hand)) continue;
        if (slot == null || slot.isEmpty()) {
          // swap hand to here
          inv.setItem(i, hand);
          swapped = true;
          break;
        }
      }
      if (!swapped) {
        // last resort - drop the item
        player.getWorld().dropItem(player.getLocation(), hand);
      }
    }
    // Step 3. give them the potato
    inv.setItemInMainHand(potato);
  }

  @EventHandler
  public void onAttack(EntityDamageByEntityEvent event) {
    Entity damager = event.getDamager();
    Entity attacked = event.getEntity();
    if (attacked instanceof CraftPlayer && damager instanceof CraftPlayer) {
      CraftPlayer attacker = (CraftPlayer)damager;
      CraftPlayer attackee = (CraftPlayer)attacked;
      PlayerInventory inv = attacker.getInventory();
      ItemStack hand = inv.getItemInMainHand();
      if (HotPotatoEffect.isPotato(hand)) {
        Team a = TeamManager.get().fromPlayer(attacker);
        Team b = TeamManager.get().fromPlayer(attackee);
        if (a != b) {
          // should we allow cross-team potato? idk
          return;
        }
        TeamState state = getState(a);
        state.bossBar.setProgress(1.0);
        state.bossBar.removePlayer(attacker);
        state.bossBar.addPlayer(attackee);
        givePotato(attackee);
        inv.setItemInMainHand(ItemStack.empty());
      }
    }
  }

  public void explodePlayer(CraftPlayer player) {
      unlock(TeamManager.get().fromPlayer(player));
      TeamState state = getState(player);
      state.bossBar.removeAll();
      PlayerInventory inv = player.getInventory();
      int i = 0;
      for(ItemStack is : inv) {
        if (HotPotatoEffect.isPotato(is)) {
          inv.setItem(i, ItemStack.empty());
        }
        i++;
      }
      player.getWorld().createExplosion(player, 4.0f, true, false);
      player.damage(100, DamageSource.builder(DamageType.IN_FIRE).build());
  }

  @EventHandler
  public void onEat(PlayerItemConsumeEvent event) {
      if (HotPotatoEffect.isPotato(event.getItem())) {
        explodePlayer((CraftPlayer)event.getPlayer());
      }
  }

  @EventHandler
    public void onClick(InventoryClickEvent e) {
        CraftPlayer p = (CraftPlayer) e.getWhoClicked();
        Inventory i = e.getInventory();
        if(HotPotatoEffect.isPotato(e.getCurrentItem())) {
            if(!i.equals(p.getInventory())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(HotPotatoEffect.isPotato(e.getCursor())) {
            e.setCancelled(true);
        }
    }
}
