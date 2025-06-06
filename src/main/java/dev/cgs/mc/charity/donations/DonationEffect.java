package dev.cgs.mc.charity.donations;

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
  public @interface Meta {
    String name();
    Target targets();
    Kind kind();
  }

  private Set<Player> lockedPlayers;

  public final boolean isAffected(Player player) {
    return lockedPlayers.contains(player);
  }

  /** Locks a player for this effect type. They won't receive it again until unlocked **/
  public final void lock(Player player) {
    lockedPlayers.add(player);
    DonationManager.get().lock(this, player);
  }

  /** Unlocks a player for this effect type **/
  public final void unlock(Player player) {
    lockedPlayers.remove(player);
    DonationManager.get().unlock(this, player);
  }

  /** Locks a team for this effect type. They won't receive it again until unlocked **/
  public final void lock(Team team) {
    lockedPlayers.addAll(team.getPlayers());
    DonationManager.get().lock(this, team);
  }

  /** Unlocks a team for this effect type **/
  public final void unlock(Team team) {
    lockedPlayers.removeAll(team.getPlayers());
    DonationManager.get().unlock(this, team);
  }

  public abstract void start(Team team, List<Player> affected);
}
