package dev.cgs.mc.charity.donations;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import dev.cgs.mc.charity.CharityMain;
import dev.cgs.mc.charity.donations.DonationEffect.Tier;
import dev.cgs.mc.charity.teams.Teams;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;

@DonationEffect.Meta(key = "smelt", name = "Instant Furnace", tier = Tier.TIER_2)
public class SmeltEffect extends DonationEffect {

        private static final Map<Material, ItemStack> smeltCache = new HashMap<>();

    /**
     * Converts every smeltable item in the player's inventory into its smelted result,
     * using vanilla furnace recipes. Preserves amount.
     */
    public static void smeltInventory(Player player) {
        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack input = contents[i];
            if (input == null || input.getType().isAir()) continue;
            if (Tag.ITEMS_ENCHANTABLE_ARMOR.isTagged(input.getType())) continue;
            if (Tag.ITEMS_BREAKS_DECORATED_POTS.isTagged(input.getType())) continue;
            if (input.getType() == Material.WATER_BUCKET || input.getType() == Material.LAVA_BUCKET) {
                contents[i] = new ItemStack(Material.BUCKET);
                return;
            }

            ItemStack smelted = getSmeltedResult(input.getType());
            if (smelted == null) continue;

            ItemStack result = smelted.clone();
            result.setAmount(input.getAmount());

            contents[i] = result;
        }

        player.getInventory().setContents(contents);
    }

    private static ItemStack getSmeltedResult(Material input) {
        // Cached result?
        if (smeltCache.containsKey(input)) {
            return smeltCache.get(input);
        }

        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            if (recipe instanceof FurnaceRecipe furnace) {
                if (furnace.getInputChoice().test(new ItemStack(input))) {
                    ItemStack output = furnace.getResult();
                    smeltCache.put(input, output);
                    return output;
                }
            }
        }

        smeltCache.put(input, null);
        return null;
    }

  @Override
  public void start(CharityMain plugin) {
    
    Teams.get().getOnlinePlayers().forEach(player -> {
      smeltInventory(player);
      player.playSound(Sound.sound(Key.key("entity.generic.burn"), Sound.Source.MASTER, 1.0f, 1.0f),
          Sound.Emitter.self());
    });
  }
}
