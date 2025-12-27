package io.github.inherit_this.debug;

import com.badlogic.gdx.math.Vector2;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.entities.PlayerStats;
import io.github.inherit_this.particles.ParticleSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DrainCommand Tests")
class DrainCommandTest {

    private Player mockPlayer;
    private PlayerStats mockStats;
    private ParticleSystem mockParticleSystem;
    private DebugConsole mockConsole;
    private DrainCommand command;

    @BeforeEach
    void setUp() {
        mockPlayer = mock(Player.class);
        mockStats = mock(PlayerStats.class);
        mockParticleSystem = mock(ParticleSystem.class);
        mockConsole = mock(DebugConsole.class);

        when(mockPlayer.getStats()).thenReturn(mockStats);
        when(mockPlayer.getPosition()).thenReturn(new Vector2(5f, 5f));

        command = new DrainCommand(mockPlayer, mockParticleSystem);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'drain'")
        void testGetName() {
            assertEquals("drain", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("drain") || description.contains("stats"));
        }
    }

    @Nested
    @DisplayName("Drain Health")
    class DrainHealth {

        @Test
        @DisplayName("Should drain health with positive value")
        void testDrainHealthPositive() {
            when(mockStats.getCurrentHealth()).thenReturn(80f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"health", "20"}, mockConsole);

            verify(mockPlayer).takeDamage(any(), eq(mockParticleSystem));
            verify(mockConsole).log(contains("Drained"));
        }

        @Test
        @DisplayName("Should restore health with negative value")
        void testRestoreHealthNegative() {
            when(mockStats.getCurrentHealth()).thenReturn(80f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"health", "-20"}, mockConsole);

            verify(mockStats).heal(20f);
            verify(mockConsole).log(contains("Restored"));
        }
    }

    @Nested
    @DisplayName("Drain Mana")
    class DrainMana {

        @Test
        @DisplayName("Should drain mana with positive value")
        void testDrainManaPositive() {
            when(mockStats.getCurrentMana()).thenReturn(80f);
            when(mockStats.getMaxMana()).thenReturn(100f);

            command.execute(new String[]{"mana", "15"}, mockConsole);

            verify(mockPlayer).takeDamage(any(), eq(mockParticleSystem));
            verify(mockConsole).log(contains("Drained"));
        }

        @Test
        @DisplayName("Should restore mana with negative value")
        void testRestoreManaNegative() {
            when(mockStats.getCurrentMana()).thenReturn(80f);
            when(mockStats.getMaxMana()).thenReturn(100f);

            command.execute(new String[]{"mana", "-15"}, mockConsole);

            verify(mockStats).restoreMana(15f);
            verify(mockConsole).log(contains("Restored"));
        }
    }

    @Nested
    @DisplayName("Drain Stamina")
    class DrainStamina {

        @Test
        @DisplayName("Should drain stamina with positive value")
        void testDrainStaminaPositive() {
            when(mockStats.getCurrentStamina()).thenReturn(80f);
            when(mockStats.getMaxStamina()).thenReturn(100f);

            command.execute(new String[]{"stamina", "10"}, mockConsole);

            verify(mockPlayer).takeDamage(any(), eq(mockParticleSystem));
            verify(mockConsole).log(contains("Drained"));
        }

        @Test
        @DisplayName("Should restore stamina with negative value")
        void testRestoreStaminaNegative() {
            when(mockStats.getCurrentStamina()).thenReturn(80f);
            when(mockStats.getMaxStamina()).thenReturn(100f);

            command.execute(new String[]{"stamina", "-10"}, mockConsole);

            verify(mockStats).restoreStamina(10f);
            verify(mockConsole).log(contains("Restored"));
        }
    }

    @Nested
    @DisplayName("Drain All Stats")
    class DrainAll {

        @Test
        @DisplayName("Should drain all stats with positive value")
        void testDrainAllPositive() {
            when(mockStats.getCurrentHealth()).thenReturn(80f);
            when(mockStats.getMaxHealth()).thenReturn(100f);
            when(mockStats.getCurrentMana()).thenReturn(80f);
            when(mockStats.getMaxMana()).thenReturn(100f);
            when(mockStats.getCurrentStamina()).thenReturn(80f);
            when(mockStats.getMaxStamina()).thenReturn(100f);

            command.execute(new String[]{"all", "5"}, mockConsole);

            verify(mockPlayer).takeDamage(any(), eq(mockParticleSystem));
            verify(mockConsole).log(contains("Drained"));
            verify(mockConsole).log(contains("Health"));
            verify(mockConsole).log(contains("Mana"));
            verify(mockConsole).log(contains("Stamina"));
        }

        @Test
        @DisplayName("Should restore all stats with negative value")
        void testRestoreAllNegative() {
            when(mockStats.getCurrentHealth()).thenReturn(80f);
            when(mockStats.getMaxHealth()).thenReturn(100f);
            when(mockStats.getCurrentMana()).thenReturn(80f);
            when(mockStats.getMaxMana()).thenReturn(100f);
            when(mockStats.getCurrentStamina()).thenReturn(80f);
            when(mockStats.getMaxStamina()).thenReturn(100f);

            command.execute(new String[]{"all", "-5"}, mockConsole);

            verify(mockStats).heal(5f);
            verify(mockStats).restoreMana(5f);
            verify(mockStats).restoreStamina(5f);
            verify(mockConsole).log(contains("Restored"));
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show usage when no arguments provided")
        void testNoArguments() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
            verify(mockConsole, atLeast(4)).log(anyString()); // Usage message has multiple lines
        }

        @Test
        @DisplayName("Should show usage when only stat type provided")
        void testOnlyStatType() {
            command.execute(new String[]{"health"}, mockConsole);

            verify(mockConsole).log(contains("Usage"));
        }

        @Test
        @DisplayName("Should handle invalid stat type")
        void testInvalidStatType() {
            command.execute(new String[]{"invalid", "10"}, mockConsole);

            verify(mockConsole).log(contains("Invalid stat type"));
        }

        @Test
        @DisplayName("Should handle invalid number format")
        void testInvalidNumberFormat() {
            command.execute(new String[]{"health", "abc"}, mockConsole);

            verify(mockConsole).log(contains("Invalid amount"));
        }

        @Test
        @DisplayName("Should reject zero amount")
        void testZeroAmount() {
            command.execute(new String[]{"health", "0"}, mockConsole);

            verify(mockConsole).log(contains("Amount cannot be zero"));
        }
    }

    @Nested
    @DisplayName("Case Insensitivity")
    class CaseInsensitivity {

        @Test
        @DisplayName("Should accept uppercase stat types")
        void testUppercaseStatType() {
            when(mockStats.getCurrentHealth()).thenReturn(80f);
            when(mockStats.getMaxHealth()).thenReturn(100f);

            command.execute(new String[]{"HEALTH", "10"}, mockConsole);

            verify(mockPlayer).takeDamage(any(), any());
        }

        @Test
        @DisplayName("Should accept mixed case stat types")
        void testMixedCaseStatType() {
            when(mockStats.getCurrentMana()).thenReturn(80f);
            when(mockStats.getMaxMana()).thenReturn(100f);

            command.execute(new String[]{"MaNa", "10"}, mockConsole);

            verify(mockPlayer).takeDamage(any(), any());
        }
    }
}
