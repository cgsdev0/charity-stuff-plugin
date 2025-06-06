package dev.cgs.mc.charity.objectives;

// This is a thing that happened because of a donation
public interface Objective {

  /** put this on top of your Objective classes OR ELSE (it will crash) */
  public @interface Meta {
    String name();
    /** how many times can you repeat it? 1 means you can't repeat it */
    int repeatable() default 1;
    /** how many points is it worth? */
    int worth();
  }
}
