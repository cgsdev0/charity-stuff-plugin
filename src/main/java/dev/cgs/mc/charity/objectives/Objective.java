package dev.cgs.mc.charity.objectives;

import dev.cgs.mc.charity.teams.Team;
import dev.cgs.mc.charity.teams.Teams;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.bukkit.entity.Player;

// This is a thing that happened because of a donation
public abstract class Objective {
  public enum Kind { PER_PLAYER, PER_TEAM }

  /** put this on top of your Objective classes OR ELSE (it will crash) */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Meta {
    String key();
    String name();
    Kind kind();
    /** how many points is it worth? */
    int worth();
    /**
     * what advancement is this objective linked to? (Can be found on wiki; make sure it starts
     * with "minecraft:") *
     */
    String advancement() default "";
    /**
     * maybe we can do these ones automatically if they are tied to an advancement
     */
    String desc() default "";
  }

  public Objective.Meta meta;

  public Objective() {
    Class<?> subclass = this.getClass();
    Objective.Meta meta = subclass.getAnnotation(Objective.Meta.class);
    if (meta == null) {
      throw new AssertionError("missing meta annotation!");
    }
    this.meta = meta;
  }

  public boolean unlock(Player player) {
    Team t = Teams.get().fromPlayer(player);
    if (t == null) {
      return false;
    }
    return t.unlock(meta.key(), player);
  }
}
