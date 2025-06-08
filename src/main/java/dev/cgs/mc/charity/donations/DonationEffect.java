package dev.cgs.mc.charity.donations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import dev.cgs.mc.charity.Team;

// This is a thing that happened because of a donation
public abstract class DonationEffect {

  public enum Target {
    /** Effect targets a random player **/
    PLAYER,
    /** Effect targets the entire team **/
    TEAM
  }

  public enum Kind {
    /** This effect helps the team **/
    POSITIVE,
    /** This effect harms the team **/
    NEGATIVE
  }

  /** put this on top of your DonationEffect classes OR ELSE (it will crash) */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Meta {
    String key();
    String name();
    Target targets();
    Kind kind();
  }

  /** Locks a player for this effect type. They won't receive it again until unlocked **/
  public final void lock() {
    DonationManager.get().lock(this);
  }

  /** Unlocks a player for this effect type **/
  public final void unlock() {
    DonationManager.get().unlock(this);
  }

  public abstract void start();
}
