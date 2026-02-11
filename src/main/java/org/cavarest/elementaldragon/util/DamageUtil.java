package org.cavarest.elementaldragon.util;

import org.bukkit.entity.LivingEntity;

/**
 * Utility class for dealing true damage that bypasses all protection.
 *
 * True damage ignores:
 * - Armor and armor enchantments
 * - Potion effects (Resistance, etc.)
 * - Shield blocking
 * - Any damage reduction modifiers
 *
 * This is accomplished by directly manipulating the entity's health
 * via {@link LivingEntity#setHealth(double)}, which bypasses all
 * damage calculation and protection systems.
 */
public class DamageUtil {

  /**
   * Deal true damage to a target, bypassing all protection.
   *
   * <p>True damage is applied by directly reducing the target's health
   * attribute. This completely bypasses:</p>
   * <ul>
   *   <li>Armor and armor toughness</li>
   *   <li>Enchantments (Protection, etc.)</li>
   *   <li>Potion effects (Resistance, etc.)</li>
   *   <li>Shield blocking</li>
   *   <li>Damage modifiers</li>
   * </ul>
   *
   * @param target The target entity to damage
   * @param damage The amount of damage to deal (2.0 = 1 heart)
   */
  public static void dealTrueDamage(LivingEntity target, double damage) {
    if (target == null || target.isDead()) {
      return;
    }

    double currentHealth = target.getHealth();
    double newHealth = Math.max(0, currentHealth - damage);
    target.setHealth(newHealth);
  }

  /**
   * Check if true damage would kill the target.
   *
   * @param target The target entity to check
   * @param damage The amount of damage to check
   * @return true if the damage would reduce health to 0 or below
   */
  public static boolean wouldKill(LivingEntity target, double damage) {
    if (target == null || target.isDead()) {
      return false;
    }

    return target.getHealth() <= damage;
  }
}
