package dev.cgs.mc.charity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.PlayerArgument;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public final class CharityMain extends JavaPlugin implements Listener {

  static String POTATO_LORE = "It's really hot!";
  @EventHandler
  public void onDrop(PlayerDropItemEvent event) {
    Item dropped = event.getItemDrop();
    if (CharityMain.isPotato(dropped.getItemStack())) {
        event.setCancelled(true);
      }
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
      if (CharityMain.isPotato(hand)) {
        this.bossBar.setProgress(1.0);
        this.bossBar.removePlayer(attacker);
        this.bossBar.addPlayer(attackee);
        givePotato(attackee);
        inv.setItemInMainHand(ItemStack.empty());
      }
    }
  }

  @EventHandler
  public void onEat(PlayerItemConsumeEvent event) {
      if (CharityMain.isPotato(event.getItem())) {
        explodePlayer((CraftPlayer)event.getPlayer());
      }
  }
  @EventHandler
    public void onClick(InventoryClickEvent e) {
        CraftPlayer p = (CraftPlayer) e.getWhoClicked();
        Inventory i = e.getInventory();
        if(CharityMain.isPotato(e.getCurrentItem())) {
            if(!i.equals(p.getInventory())) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if(CharityMain.isPotato(e.getCursor())) {
            e.setCancelled(true);
        }
    }
  // @EventHandler
  //   public void onMenuClick(InventoryClickEvent e) {
  //       getLogger().info(e.toString());
  //       if(e.getClickedInventory() == null)return;
  //       getLogger().info(e.getClickedInventory().getType().toString());
  //       if(!e.getInventory().getType().equals(InventoryType.PLAYER)){
  //       getLogger().info("A");
  //       if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){
  //       getLogger().info("B");
  //       ItemStack item = e.getCursor();
  //       if(item != null && CharityMain.isPotato(item)) {
  //       getLogger().info("C");
  //         e.setCancelled(true);
  //       }
  //     }
  //       }
  // }


  public void explodePlayer(CraftPlayer player) {
      this.bossBar.removeAll();
      PlayerInventory inv = player.getInventory();
      int i = 0;
      for(ItemStack is : inv) {
        if (CharityMain.isPotato(is)) {
          inv.setItem(i, ItemStack.empty());
        }
        i++;
      }
      this.activePotato = false;
      player.getWorld().createExplosion(player, 4.0f, true, false);
      player.damage(100, DamageSource.builder(DamageType.IN_FIRE).build());
  }

  public static boolean isPotato(ItemStack stack) {
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

  public boolean activePotato = false;

  public BossBar bossBar;
  public UUID holder;

  public void givePotato(CraftPlayer player) {
    this.holder = player.getUniqueId();
    this.bossBar.addPlayer(player);
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

  @Override
  public void onDisable() {
    this.bossBar.removeAll();
  }

  @Override
  public void onEnable() {
    this.bossBar = Bukkit.createBossBar("Hot Potato", BarColor.RED, BarStyle.SEGMENTED_20);
    this.bossBar.removeAll();
    this.getServer().getPluginManager().registerEvents(this, this);
    new CommandAPICommand("butterfingers")
    .withPermission(CommandPermission.OP)               // Required permissions
    .executes((sender, args) -> {
        getServer().getOnlinePlayers().forEach(player -> {
          ItemStack item = player.getInventory().getItemInMainHand();
          if (item != null && !item.isEmpty() && !CharityMain.isPotato(item)) {
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
    new CommandAPICommand("hot-potato")
    .withArguments(new PlayerArgument("player")) // The arguments
    .withPermission(CommandPermission.OP)               // Required permissions
    .executes((sender, args) -> {
        if (this.activePotato) return;
        CraftPlayer player = (CraftPlayer) args.get("player");
        this.activePotato = true;
        this.bossBar.setProgress(1.0);
        getServer().getScheduler().runTaskTimer(this, task -> {
          if (!this.activePotato) {
                task.cancel();
                return;
          }
          double new_progress = Math.max(this.bossBar.getProgress() - 0.004, 0.0);
          this.bossBar.setProgress(new_progress);
          if (new_progress == 0.0) {
            Player p = getServer().getPlayer(holder);
            if (p != null && p instanceof CraftPlayer) {
              this.explodePlayer((CraftPlayer)p);
            }
            this.activePotato = false;
          }
        }, 0L, 1L);
        givePotato(player);
    })
    .register();
  }
}
