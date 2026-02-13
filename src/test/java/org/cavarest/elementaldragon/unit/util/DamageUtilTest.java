package org.cavarest.elementaldragon.unit.util;

import org.bukkit.entity.LivingEntity;
import org.cavarest.elementaldragon.util.DamageUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DamageUtil true damage functionality.
 */
@DisplayName("DamageUtil Tests")
public class DamageUtilTest {

    @Mock
    private LivingEntity entity;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("dealTrueDamage reduces health by damage amount")
    public void testDealTrueDamageReducesHealth() {
        when(entity.getHealth()).thenReturn(20.0);
        when(entity.isDead()).thenReturn(false);

        DamageUtil.dealTrueDamage(entity, 5.0);

        verify(entity).setHealth(15.0);
    }

    @Test
    @DisplayName("dealTrueDamage clamps health to minimum 0")
    public void testDealTrueDamageClampsToZero() {
        when(entity.getHealth()).thenReturn(3.0);
        when(entity.isDead()).thenReturn(false);

        DamageUtil.dealTrueDamage(entity, 5.0);

        verify(entity).setHealth(0.0);
    }

    @Test
    @DisplayName("dealTrueDamage does nothing on dead entity")
    public void testDealTrueDamageDoesNothingOnDeadEntity() {
        when(entity.isDead()).thenReturn(true);

        DamageUtil.dealTrueDamage(entity, 5.0);

        verify(entity, never()).setHealth(anyDouble());
    }

    @Test
    @DisplayName("dealTrueDamage does nothing on null entity")
    public void testDealTrueDamageDoesNothingOnNullEntity() {
        // Should not throw exception
        assertDoesNotThrow(() -> DamageUtil.dealTrueDamage(null, 5.0));
    }

    @Test
    @DisplayName("wouldKill returns true when damage >= health")
    public void testWouldKillReturnsTrueWhenDamageExceedsHealth() {
        when(entity.getHealth()).thenReturn(5.0);
        when(entity.isDead()).thenReturn(false);

        boolean result = DamageUtil.wouldKill(entity, 5.0);

        assertTrue(result);
    }

    @Test
    @DisplayName("wouldKill returns true when damage > health")
    public void testWouldKillReturnsTrueWhenDamageGreaterThanHealth() {
        when(entity.getHealth()).thenReturn(3.0);
        when(entity.isDead()).thenReturn(false);

        boolean result = DamageUtil.wouldKill(entity, 5.0);

        assertTrue(result);
    }

    @Test
    @DisplayName("wouldKill returns false when damage < health")
    public void testWouldKillReturnsFalseWhenDamageLessThanHealth() {
        when(entity.getHealth()).thenReturn(10.0);
        when(entity.isDead()).thenReturn(false);

        boolean result = DamageUtil.wouldKill(entity, 5.0);

        assertFalse(result);
    }

    @Test
    @DisplayName("wouldKill returns false on dead entity")
    public void testWouldKillReturnsFalseOnDeadEntity() {
        when(entity.isDead()).thenReturn(true);

        boolean result = DamageUtil.wouldKill(entity, 5.0);

        assertFalse(result);
    }

    @Test
    @DisplayName("wouldKill returns false on null entity")
    public void testWouldKillReturnsFalseOnNullEntity() {
        boolean result = DamageUtil.wouldKill(null, 5.0);

        assertFalse(result);
    }
}
