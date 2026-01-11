package org.cavarest.elementaldragon.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.cooldown.CooldownManager;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Central registry for all fragment types.
 * Provides Single Source of Truth for fragment mappings and registration.
 *
 * This reduces the 6-file modification requirement when adding new fragments:
 * - FragmentType enum (add enum value)
 * - FragmentRegistry.register() (add registration line)
 * - CraftingManager (add recipe)
 * - ElementalItems (add item creation)
 * - CooldownManager (add constants if needed)
 * - ElementalDragon (register command)
 *
 * The registry eliminates hardcoded switch cases and provides centralized
 * access to fragment metadata and canonical names.
 */
public class FragmentRegistry {

  private final Map<FragmentType, Fragment> fragments;
  private final Map<FragmentType, String> canonicalNames;
  private final ElementalDragon plugin;

  /**
   * Create a new FragmentRegistry.
   *
   * @param plugin The plugin instance
   */
  public FragmentRegistry(ElementalDragon plugin) {
    this.plugin = plugin;
    this.fragments = new HashMap<>();
    this.canonicalNames = new HashMap<>();
    initializeMappings();
    registerFragments();
  }

  /**
   * Initialize the canonical name mappings.
   * These map FragmentType to the string keys used in CooldownManager.
   */
  private void initializeMappings() {
    canonicalNames.put(FragmentType.BURNING, CooldownManager.FIRE);
    canonicalNames.put(FragmentType.AGILITY, CooldownManager.AGILE);
    canonicalNames.put(FragmentType.IMMORTAL, CooldownManager.IMMORTAL);
    canonicalNames.put(FragmentType.CORRUPTED, CooldownManager.CORRUPT);
  }

  /**
   * Register all fragment implementations.
   * This is the SINGLE LOCATION where fragments are instantiated.
   */
  private void registerFragments() {
    // Register Burning Fragment
    register(FragmentType.BURNING, () -> new BurningFragment(plugin),
             CooldownManager.FIRE);

    // Register Agility Fragment
    register(FragmentType.AGILITY, () -> new AgilityFragment(plugin),
             CooldownManager.AGILE);

    // Register Immortal Fragment
    register(FragmentType.IMMORTAL, () -> new ImmortalFragment(plugin),
             CooldownManager.IMMORTAL);

    // Register Corrupted Core
    register(FragmentType.CORRUPTED, () -> new CorruptedCoreFragment(plugin),
             CooldownManager.CORRUPT);
  }

  /**
   * Register a fragment type with its factory and canonical name.
   *
   * @param type The fragment type
   * @param fragmentFactory Factory to create the fragment instance
   * @param canonicalName The canonical name for cooldown/UI lookups
   */
  public void register(FragmentType type, Supplier<Fragment> fragmentFactory, String canonicalName) {
    if (type == null) {
      throw new IllegalArgumentException("FragmentType cannot be null");
    }
    if (canonicalName == null) {
      throw new IllegalArgumentException("Canonical name cannot be null");
    }

    Fragment fragment = fragmentFactory.get();
    fragments.put(type, fragment);
    canonicalNames.put(type, canonicalName);

    // Register event listeners for the fragment
    registerFragmentListener(fragment);
  }

  /**
   * Register a fragment's event listeners with the server.
   *
   * @param fragment The fragment to register
   */
  private void registerFragmentListener(Fragment fragment) {
    if (plugin.getServer() != null && fragment instanceof org.bukkit.event.Listener) {
      plugin.getServer().getPluginManager().registerEvents(
        (org.bukkit.event.Listener) fragment,
        plugin
      );
    }
  }

  /**
   * Get the canonical element name for a fragment type.
   * This is the string key used in CooldownManager and other systems.
   *
   * @param type The fragment type
   * @return The canonical name, or null if not found
   */
  public String getCanonicalName(FragmentType type) {
    return canonicalNames.get(type);
  }

  /**
   * Get a fragment instance by type.
   *
   * @param type The fragment type
   * @return The fragment instance, or null if not found
   */
  public Fragment getFragment(FragmentType type) {
    return fragments.get(type);
  }

  /**
   * Check if a fragment type is registered.
   *
   * @param type The fragment type
   * @return true if registered
   */
  public boolean isRegistered(FragmentType type) {
    return fragments.containsKey(type);
  }

  /**
   * Get all registered fragment types.
   *
   * @return Array of registered fragment types
   */
  public FragmentType[] getRegisteredTypes() {
    return fragments.keySet().toArray(new FragmentType[0]);
  }

  /**
   * Get the number of registered fragments.
   *
   * @return Count of registered fragments
   */
  public int getFragmentCount() {
    return fragments.size();
  }

  /**
   * Get the FragmentType for a canonical name.
   *
   * @param canonicalName The canonical name
   * @return The fragment type, or null if not found
   */
  public FragmentType fromCanonicalName(String canonicalName) {
    for (Map.Entry<FragmentType, String> entry : canonicalNames.entrySet()) {
      if (entry.getValue().equals(canonicalName)) {
        return entry.getKey();
      }
    }
    return null;
  }
}
