package io.github.inherit_this.particles;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ParticleSystem.MaterialType enum.
 */
@DisplayName("MaterialType Tests")
public class MaterialTypeTest {

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected material types")
        void testAllValues() {
            ParticleSystem.MaterialType[] types = ParticleSystem.MaterialType.values();
            assertEquals(6, types.length, "Should have exactly 6 material types");
        }

        @Test
        @DisplayName("Should have WOOD material type")
        void testWoodExists() {
            assertNotNull(ParticleSystem.MaterialType.WOOD);
        }

        @Test
        @DisplayName("Should have STONE material type")
        void testStoneExists() {
            assertNotNull(ParticleSystem.MaterialType.STONE);
        }

        @Test
        @DisplayName("Should have BLOOD material type")
        void testBloodExists() {
            assertNotNull(ParticleSystem.MaterialType.BLOOD);
        }

        @Test
        @DisplayName("Should have MANA material type")
        void testManaExists() {
            assertNotNull(ParticleSystem.MaterialType.MANA);
        }

        @Test
        @DisplayName("Should have STAMINA material type")
        void testStaminaExists() {
            assertNotNull(ParticleSystem.MaterialType.STAMINA);
        }

        @Test
        @DisplayName("Should have CURSED material type")
        void testCursedExists() {
            assertNotNull(ParticleSystem.MaterialType.CURSED);
        }

        @Test
        @DisplayName("valueOf should work for all types")
        void testValueOf() {
            assertEquals(ParticleSystem.MaterialType.WOOD,
                ParticleSystem.MaterialType.valueOf("WOOD"));
            assertEquals(ParticleSystem.MaterialType.STONE,
                ParticleSystem.MaterialType.valueOf("STONE"));
            assertEquals(ParticleSystem.MaterialType.BLOOD,
                ParticleSystem.MaterialType.valueOf("BLOOD"));
            assertEquals(ParticleSystem.MaterialType.MANA,
                ParticleSystem.MaterialType.valueOf("MANA"));
            assertEquals(ParticleSystem.MaterialType.STAMINA,
                ParticleSystem.MaterialType.valueOf("STAMINA"));
            assertEquals(ParticleSystem.MaterialType.CURSED,
                ParticleSystem.MaterialType.valueOf("CURSED"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                ParticleSystem.MaterialType.valueOf("INVALID");
            });
            assertThrows(IllegalArgumentException.class, () -> {
                ParticleSystem.MaterialType.valueOf("METAL");
            });
        }
    }

    @Nested
    @DisplayName("Ordinal Values")
    class OrdinalValues {

        @Test
        @DisplayName("Each material type should have unique ordinal")
        void testUniqueOrdinals() {
            assertNotEquals(ParticleSystem.MaterialType.WOOD.ordinal(), 
                ParticleSystem.MaterialType.STONE.ordinal());
        }

        @Test
        @DisplayName("Ordinals should be sequential from 0")
        void testSequentialOrdinals() {
            ParticleSystem.MaterialType[] types = ParticleSystem.MaterialType.values();
            for (int i = 0; i < types.length; i++) {
                assertEquals(i, types[i].ordinal(), "Ordinal should match array index");
            }
        }
    }

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support switch statements")
        void testSwitchStatement() {
            for (ParticleSystem.MaterialType type : ParticleSystem.MaterialType.values()) {
                String particleType;
                switch (type) {
                    case WOOD:
                        particleType = "Wood particles";
                        break;
                    case STONE:
                        particleType = "Stone particles";
                        break;
                    case BLOOD:
                        particleType = "Blood particles";
                        break;
                    case MANA:
                        particleType = "Mana particles";
                        break;
                    case STAMINA:
                        particleType = "Stamina particles";
                        break;
                    case CURSED:
                        particleType = "Cursed particles";
                        break;
                    default:
                        particleType = null;
                        break;
                }
                assertNotNull(particleType, "Switch should handle all material types");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(ParticleSystem.MaterialType.WOOD == ParticleSystem.MaterialType.WOOD);
            assertFalse(ParticleSystem.MaterialType.WOOD == ParticleSystem.MaterialType.STONE);
            assertTrue(ParticleSystem.MaterialType.STONE.equals(ParticleSystem.MaterialType.STONE));
            assertFalse(ParticleSystem.MaterialType.STONE.equals(ParticleSystem.MaterialType.WOOD));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (ParticleSystem.MaterialType type : ParticleSystem.MaterialType.values()) {
                String name = type.name();
                ParticleSystem.MaterialType restored = ParticleSystem.MaterialType.valueOf(name);
                assertEquals(type, restored, "Should restore from name");
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            ParticleSystem.MaterialType type = ParticleSystem.MaterialType.WOOD;
            assertNotEquals(null, type);
            assertFalse(type.equals(null));
        }

        @Test
        @DisplayName("Should handle toString")
        void testToString() {
            for (ParticleSystem.MaterialType type : ParticleSystem.MaterialType.values()) {
                String str = type.toString();
                assertNotNull(str);
                assertFalse(str.isEmpty());
                assertEquals(type.name(), str, "toString should match name");
            }
        }

        @Test
        @DisplayName("Should maintain identity")
        void testIdentity() {
            ParticleSystem.MaterialType wood1 = ParticleSystem.MaterialType.WOOD;
            ParticleSystem.MaterialType wood2 = ParticleSystem.MaterialType.valueOf("WOOD");
            assertSame(wood1, wood2, "Same enum constant should be identical");
        }

        @Test
        @DisplayName("Name should be uppercase")
        void testNameUppercase() {
            for (ParticleSystem.MaterialType type : ParticleSystem.MaterialType.values()) {
                String name = type.name();
                assertEquals(name.toUpperCase(), name, "Enum name should be uppercase");
            }
        }
    }

    @Nested
    @DisplayName("Use Cases")
    class UseCases {

        @Test
        @DisplayName("Material types should represent breakable object and combat materials")
        void testBreakableObjectMaterials() {
            // WOOD for crates, barrels
            assertNotNull(ParticleSystem.MaterialType.WOOD);

            // STONE for pots, ceramics
            assertNotNull(ParticleSystem.MaterialType.STONE);

            // BLOOD for health damage effects
            assertNotNull(ParticleSystem.MaterialType.BLOOD);

            // MANA for mana drain effects
            assertNotNull(ParticleSystem.MaterialType.MANA);

            // STAMINA for stamina drain effects
            assertNotNull(ParticleSystem.MaterialType.STAMINA);

            // CURSED for cursed mushroom dripping effects
            assertNotNull(ParticleSystem.MaterialType.CURSED);
        }

        @Test
        @DisplayName("Each material type should be distinguishable")
        void testDistinguishable() {
            assertNotEquals(ParticleSystem.MaterialType.WOOD, ParticleSystem.MaterialType.STONE);
            assertNotEquals(ParticleSystem.MaterialType.BLOOD, ParticleSystem.MaterialType.MANA);
            assertNotEquals(ParticleSystem.MaterialType.MANA, ParticleSystem.MaterialType.STAMINA);
        }
    }
}
