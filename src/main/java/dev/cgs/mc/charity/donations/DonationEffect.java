package dev.cgs.mc.charity.donations;

import dev.cgs.mc.charity.CharityMain;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// This is a thing that happened because of a donation
public abstract class DonationEffect {
  // TODO: 10% chance of upgrading from neutral -> mild??
  public enum Tier {
    /** Minimal chaos. $1 - $10 donation **/
    TIER_1,
    /** Mild chaos. $10 - $100 donation **/
    TIER_2,
    /** Maximum chaos! $100+ donation **/
    TIER_3
  }

  /** put this on top of your DonationEffect classes OR ELSE (it will crash) */
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Meta {
    String key();
    String name();
    Tier tier();
    /** Use this if you need to share the lock with other effects. **/
    String mutex() default "";
    /** Supress the 3 warning dings for this effect. **/
    boolean no_warning() default false;
    /** Don't show the default title text for this effect. **/
    boolean no_title() default false;
  }

  /** Locks a player for this effect type. They won't receive it again until unlocked **/
  public final void lock() {
    Donations.get().lock(this);
  }

  /** Unlocks a player for this effect type **/
  public final void unlock() {
    Donations.get().unlock(this);
  }

  public abstract void start(CharityMain plugin);
}
