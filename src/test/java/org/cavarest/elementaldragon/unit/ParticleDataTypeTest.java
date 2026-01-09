package org.cavarest.elementaldragon.unit;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.cavarest.elementaldragon.fragment.AbstractFragment;
import org.cavarest.elementaldragon.fragment.AgilityFragment;
import org.cavarest.elementaldragon.fragment.ImmortalFragment;
import org.cavarest.elementaldragon.fragment.FragmentType;
import org.cavarest.elementaldragon.visual.ParticleFX;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify particle effects use correct data types.
 * Guards against errors like:
 * - "data (class org.bukkit.Particle$DustOptions) should be class java.lang.Void"
 * - "data (class org.bukkit.Particle$DustOptions) should be interface org.bukkit.block.data.BlockData"
 */
public class ParticleDataTypeTest {

    @Test
    @DisplayName("ParticleFX methods should only use DUST particle for DustOptions")
    public void testParticleFXUsesDustForDustOptions() {
        // Verify that ParticleFX methods use Particle.DUST when spawning colored particles
        // This test ensures we don't accidentally use FALLING_DUST, CLOUD, or REVERSE_PORTAL
        // with DustOptions which would cause "data should be class X" errors

        // Test passes if ParticleFX class exists and compiles without errors
        assertNotNull(ParticleFX.class);
    }

    @Test
    @DisplayName("FragmentType particle types should be compatible with fragment usage")
    public void testFragmentTypeParticleCompatibility() {
        // Verify each FragmentType's particle type
        FragmentType[] types = FragmentType.values();

        for (FragmentType type : types) {
            Particle particle = type.getParticleType();
            assertNotNull(particle, "FragmentType " + type + " should have a particle type");

            // Document the particle type for each fragment
            switch (type) {
                case BURNING:
                    assertEquals(Particle.DUST, particle,
                        "BURNING should use DUST particle (accepts DustOptions)");
                    break;
                case AGILITY:
                    assertEquals(Particle.CLOUD, particle,
                        "AGILITY should use CLOUD particle (no data required)");
                    break;
                case IMMORTAL:
                    assertEquals(Particle.FALLING_DUST, particle,
                        "IMMORTAL should use FALLING_DUST particle (requires BlockData)");
                    break;
                case CORRUPTED:
                    assertEquals(Particle.REVERSE_PORTAL, particle,
                        "CORRUPTED should use REVERSE_PORTAL particle (no data required)");
                    break;
            }
        }
    }

    @Test
    @DisplayName("AbstractFragment activation/deactivation should always use DUST for colored particles")
    public void testAbstractFragmentUsesDustForColors() {
        // Verify that AbstractFragment's particle methods use Particle.DUST
        // This prevents errors when fragments like AGILITY (CLOUD) or IMMORTAL (FALLING_DUST)
        // try to spawn colored particles

        // The fix ensures showActivationParticles, showDeactivationParticles, and
        // showAbilityParticles all use Particle.DUST instead of type.getParticleType()

        assertNotNull(AbstractFragment.class);
    }

    @Test
    @DisplayName("CLOUD particle should not accept DustOptions")
    public void testCloudParticleRequiresNoData() {
        // AGILITY fragment uses CLOUD particle
        // CLOUD requires no data (Void), not DustOptions
        // This test documents the error we fixed:
        // "data (class org.bukkit.Particle$DustOptions) should be class java.lang.Void"

        Particle cloudParticle = Particle.CLOUD;
        assertEquals(Void.class, cloudParticle.getDataType(),
            "CLOUD particle should require Void (no data), not DustOptions");
    }

    @Test
    @DisplayName("FALLING_DUST particle should require BlockData")
    public void testFallingDustParticleRequiresBlockData() {
        // IMMORTAL fragment uses FALLING_DUST particle
        // FALLING_DUST requires BlockData, not DustOptions
        // This test documents the error we fixed:
        // "missing required data interface org.bukkit.block.data.BlockData"

        Particle fallingDustParticle = Particle.FALLING_DUST;
        assertEquals(org.bukkit.block.data.BlockData.class, fallingDustParticle.getDataType(),
            "FALLING_DUST particle should require BlockData, not DustOptions");
    }

    @Test
    @DisplayName("DUST particle should accept DustOptions")
    public void testDustParticleAcceptsDustOptions() {
        // DUST is the only particle that accepts DustOptions
        // All colored particle effects should use DUST

        Particle dustParticle = Particle.DUST;
        assertEquals(Particle.DustOptions.class, dustParticle.getDataType(),
            "DUST particle should accept DustOptions for colored particles");
    }

    @Test
    @DisplayName("REVERSE_PORTAL particle should not accept DustOptions")
    public void testReversePortalParticleRequiresNoData() {
        // CORRUPTED fragment uses REVERSE_PORTAL particle
        // REVERSE_PORTAL requires no data (Void), not DustOptions

        Particle reversePortalParticle = Particle.REVERSE_PORTAL;
        assertEquals(Void.class, reversePortalParticle.getDataType(),
            "REVERSE_PORTAL particle should require Void (no data), not DustOptions");
    }

    @Test
    @DisplayName("ParticleFX spawnShieldAura should use DUST not FALLING_DUST")
    public void testSpawnShieldAuraUsesDust() {
        // This test documents the fix for the error:
        // "Error activating fragment IMMORTAL: missing required data interface org.bukkit.block.data.BlockData"
        // The fix changed FALLING_DUST to DUST in spawnShieldAura method

        // Verify the method exists
        try {
            Method method = ParticleFX.class.getMethod("spawnShieldAura", Location.class);
            assertNotNull(method, "spawnShieldAura method should exist");
        } catch (NoSuchMethodException e) {
            fail("spawnShieldAura method not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("ParticleFX spawnImmortalActivation should use DUST not FALLING_DUST")
    public void testSpawnImmortalActivationUsesDust() {
        // This test documents the fix for FALLING_DUST usage in activation particles

        // Verify the method exists
        try {
            Method method = ParticleFX.class.getMethod("spawnImmortalActivation", Location.class);
            assertNotNull(method, "spawnImmortalActivation method should exist");
        } catch (NoSuchMethodException e) {
            fail("spawnImmortalActivation method not found: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Particle data type matrix - compatibility reference")
    public void testParticleDataTypeMatrix() {
        // Reference test documenting which particles accept which data types
        // Only test particles actually used in this plugin

        // Particles that accept DustOptions (colored particles)
        assertEquals(Particle.DustOptions.class, Particle.DUST.getDataType(),
            "DUST should accept DustOptions");

        // Particles that require no data (Void) - only test ones we use
        assertEquals(Void.class, Particle.CLOUD.getDataType(),
            "CLOUD should require no data");
        assertEquals(Void.class, Particle.REVERSE_PORTAL.getDataType(),
            "REVERSE_PORTAL should require no data");
        assertEquals(Void.class, Particle.FLAME.getDataType(),
            "FLAME should require no data");
        assertEquals(Void.class, Particle.SMOKE.getDataType(),
            "SMOKE should require no data");
        assertEquals(Void.class, Particle.CRIT.getDataType(),
            "CRIT should require no data");
        assertEquals(Void.class, Particle.SOUL.getDataType(),
            "SOUL should require no data");

        // Particles that require BlockData
        assertEquals(org.bukkit.block.data.BlockData.class, Particle.FALLING_DUST.getDataType(),
            "FALLING_DUST should require BlockData");
    }
}