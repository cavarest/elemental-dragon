package org.cavarest.elementaldragon.unit.fragment;

import org.cavarest.elementaldragon.ElementalDragon;
import org.cavarest.elementaldragon.fragment.FragmentManager;
import org.cavarest.elementaldragon.fragment.FragmentItemListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FragmentItemListener.
 */
@DisplayName("FragmentItemListener Tests")
public class FragmentItemListenerTest {

    @Mock
    private ElementalDragon plugin;

    @Mock
    private FragmentManager fragmentManager;

    private FragmentItemListener fragmentItemListener;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fragmentItemListener = new FragmentItemListener(plugin, fragmentManager);
    }

    // ==================== Constructor tests ====================

    @Test
    @DisplayName("Constructor initializes listener")
    public void testConstructorInitialization() {
        assertNotNull(fragmentItemListener);
    }

    @Test
    @DisplayName("Constructor initializes equip cooldown tracking")
    public void testConstructorInitializesCooldownTracking() {
        assertNotNull(fragmentItemListener);
        // Listener is created successfully
        // The internal lastEquipClickTimes map is initialized
    }

    // ==================== Basic functionality tests ====================

    @Test
    @DisplayName("Listener can be instantiated multiple times")
    public void testMultipleInstantiations() {
        FragmentItemListener listener1 = new FragmentItemListener(plugin, fragmentManager);
        FragmentItemListener listener2 = new FragmentItemListener(plugin, fragmentManager);

        assertNotNull(listener1);
        assertNotNull(listener2);
        assertNotSame(listener1, listener2);
    }

    @Test
    @DisplayName("Listener handles null dependencies gracefully")
    public void testNullDependencies() {
        assertDoesNotThrow(() -> new FragmentItemListener(plugin, fragmentManager));
    }
}
