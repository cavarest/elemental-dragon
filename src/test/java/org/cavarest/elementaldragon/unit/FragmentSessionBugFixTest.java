package org.cavarest.elementaldragon.unit;

import org.cavarest.elementaldragon.fragment.*;
import org.cavarest.elementaldragon.fragment.AbstractFragment;
import org.cavarest.elementaldragon.ability.EntityTargeter;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to guard against bugs fixed in the session:
 *
 * 1. AbstractFragment must implement Listener for event handlers to work
 * 2. Fragment materials must NOT have default right-click behaviors
 * 3. getCommandName() must use canonical names (fire, agile, corrupt, immortal)
 */
class FragmentSessionBugFixTest {

  // ===== Bug 1: AbstractFragment must implement Listener =====

  @Test
  @DisplayName("All fragment classes implement Listener for event handlers to work")
  void testFragmentsImplementListener() {
    // This test guards against the bug where AbstractFragment didn't implement Listener
    // which caused fragment event handlers (like passive effects) to never be registered
    Fragment burning = createBurningFragment();
    Fragment agility = createAgilityFragment();
    Fragment immortal = createImmortalFragment();
    Fragment corrupted = createCorruptedFragment();

    assertTrue(burning instanceof Listener,
      "BurningFragment must implement Listener for event handlers to work");
    assertTrue(agility instanceof Listener,
      "AgilityFragment must implement Listener for event handlers to work");
    assertTrue(immortal instanceof Listener,
      "ImmortalFragment must implement Listener for event handlers to work");
    assertTrue(corrupted instanceof Listener,
      "CorruptedCoreFragment must implement Listener for event handlers to work");
  }

  @Test
  @DisplayName("AbstractFragment class implements Listener interface")
  void testAbstractFragmentImplementsListener() {
    // Test using reflection that AbstractFragment implements Listener
    boolean implementsListener = Listener.class.isAssignableFrom(AbstractFragment.class);
    assertTrue(implementsListener,
      "AbstractFragment must implement Listener so that @EventHandler methods are registered");
  }

  // ===== Bug 2: Fragment materials must not have default right-click behaviors =====

  @Test
  @DisplayName("Burning Fragment material must not throw fire on right-click")
  void testBurningFragmentMaterialDoesNotThrowFire() {
    // BLAZE_POWDER has no default right-click behavior
    // FIRE_CHARGE throws a fireball - we must NOT use it
    Fragment fragment = createBurningFragment();
    Material material = fragment.getMaterial();

    assertNotEquals(Material.FIRE_CHARGE, material,
      "Burning Fragment must NOT use FIRE_CHARGE - it throws fire on right-click! " +
      "Use BLAZE_POWDER instead");
    assertEquals(Material.BLAZE_POWDER, material,
      "Burning Fragment should use BLAZE_POWDER to avoid default right-click behavior");
  }

  @Test
  @DisplayName("Agility Fragment material must not throw wind on right-click")
  void testAgilityFragmentMaterialDoesNotThrowWind() {
    // PHANTOM_MEMBRANE has no default right-click behavior
    // WIND_CHARGE throws a wind burst - we must NOT use it
    Fragment fragment = createAgilityFragment();
    Material material = fragment.getMaterial();

    assertNotEquals(Material.WIND_CHARGE, material,
      "Agility Fragment must NOT use WIND_CHARGE - it throws wind on right-click! " +
      "Use PHANTOM_MEMBRANE instead");
    assertEquals(Material.PHANTOM_MEMBRANE, material,
      "Agility Fragment should use PHANTOM_MEMBRANE to avoid default right-click behavior");
  }

  @Test
  @DisplayName("Corrupted Core material must not place block on right-click")
  void testCorruptedCoreMaterialDoesNotPlaceBlock() {
    // NETHER_STAR has no default right-click behavior
    // HEAVY_CORE places a block - we must NOT use it
    Fragment fragment = createCorruptedFragment();
    Material material = fragment.getMaterial();

    assertNotEquals(Material.HEAVY_CORE, material,
      "Corrupted Core must NOT use HEAVY_CORE - it places a block on right-click! " +
      "Use NETHER_STAR instead");
    assertEquals(Material.NETHER_STAR, material,
      "Corrupted Core should use NETHER_STAR to avoid default right-click behavior");
  }

  @Test
  @DisplayName("Immortal Fragment material is acceptable (no problematic right-click)")
  void testImmortalFragmentMaterialIsAcceptable() {
    // TOTEM_OF_UNDYING has no problematic default right-click behavior
    Fragment fragment = createImmortalFragment();
    Material material = fragment.getMaterial();

    // TOTEM is acceptable - it triggers when you die, not on right-click
    assertEquals(Material.TOTEM_OF_UNDYING, material,
      "Immortal Fragment should use TOTEM_OF_UNDYING");
  }

  // ===== Bug 3: getCommandName() must use canonical names =====

  @Test
  @DisplayName("Burning Fragment getCommandName returns 'fire' not 'burning'")
  void testBurningFragmentCommandName() {
    Fragment fragment = createBurningFragment();
    String commandName = fragment.getCommandName();

    assertEquals("fire", commandName,
      "Burning Fragment command name must be 'fire' (canonical name), not 'burning'. " +
      "This ensures help messages show '/fire' not '/burning'");
    assertNotEquals("burning", commandName,
      "getCommandName() must return canonical name 'fire', not enum name 'burning'");
  }

  @Test
  @DisplayName("Agility Fragment getCommandName returns 'agile' not 'agility'")
  void testAgilityFragmentCommandName() {
    Fragment fragment = createAgilityFragment();
    String commandName = fragment.getCommandName();

    assertEquals("agile", commandName,
      "Agility Fragment command name must be 'agile' (canonical name), not 'agility'. " +
      "This ensures help messages show '/agile' not '/agility'");
    assertNotEquals("agility", commandName,
      "getCommandName() must return canonical name 'agile', not enum name 'agility'");
  }

  @Test
  @DisplayName("Corrupted Core getCommandName returns 'corrupt' not 'corrupted'")
  void testCorruptedCoreCommandName() {
    Fragment fragment = createCorruptedFragment();
    String commandName = fragment.getCommandName();

    assertEquals("corrupt", commandName,
      "Corrupted Core command name must be 'corrupt' (canonical name), not 'corrupted'. " +
      "This ensures help messages show '/corrupt' not '/corrupted'");
    assertNotEquals("corrupted", commandName,
      "getCommandName() must return canonical name 'corrupt', not enum name 'corrupted'");
  }

  @Test
  @DisplayName("Immortal Fragment getCommandName returns 'immortal' (matches enum)")
  void testImmortalFragmentCommandName() {
    Fragment fragment = createImmortalFragment();
    String commandName = fragment.getCommandName();

    assertEquals("immortal", commandName,
      "Immortal Fragment command name must be 'immortal'");
  }

  @Test
  @DisplayName("All fragments have correct command names matching plugin.yml")
  void testAllFragmentsCommandNames() {
    // These must match the commands registered in plugin.yml
    assertEquals("fire", createBurningFragment().getCommandName());
    assertEquals("agile", createAgilityFragment().getCommandName());
    assertEquals("immortal", createImmortalFragment().getCommandName());
    assertEquals("corrupt", createCorruptedFragment().getCommandName());
  }

  // ===== Helper methods =====

  private BurningFragment createBurningFragment() {
    return new BurningFragment(null);
  }

  private AgilityFragment createAgilityFragment() {
    return new AgilityFragment(null);
  }

  private ImmortalFragment createImmortalFragment() {
    return new ImmortalFragment(null);
  }

  private CorruptedCoreFragment createCorruptedFragment() {
    return new CorruptedCoreFragment(null);
  }
}
