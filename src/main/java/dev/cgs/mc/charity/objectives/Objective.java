package dev.cgs.mc.charity.objectives;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// This is a thing that happened because of a donation
public interface Objective {
  public enum Kind { PER_PLAYER, PER_TEAM }

  /** put this on top of your Objective classes OR ELSE (it will crash) */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Meta {
    String key();
    String name();
    Kind kind();
    /** how many points is it worth? */
    int worth();
  }
}
