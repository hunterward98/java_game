package io.github.inherit_this.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for InteractableType enum.
 */
@DisplayName("InteractableType Tests")
class InteractableTypeTest {

    // ==================== Enum Values Tests ====================

    @Nested
    @DisplayName("Enum Values")
    class EnumValues {

        @Test
        @DisplayName("Should have all expected values")
        void testAllValues() {
            InteractableType[] types = InteractableType.values();

            assertEquals(4, types.length, "Should have exactly 4 types");
            assertTrue(containsType(types, InteractableType.CHEST));
            assertTrue(containsType(types, InteractableType.WORKBENCH));
            assertTrue(containsType(types, InteractableType.ANVIL));
            assertTrue(containsType(types, InteractableType.SHRINE));
        }

        @Test
        @DisplayName("valueOf should return correct enum constant")
        void testValueOf() {
            assertEquals(InteractableType.CHEST, InteractableType.valueOf("CHEST"));
            assertEquals(InteractableType.WORKBENCH, InteractableType.valueOf("WORKBENCH"));
            assertEquals(InteractableType.ANVIL, InteractableType.valueOf("ANVIL"));
            assertEquals(InteractableType.SHRINE, InteractableType.valueOf("SHRINE"));
        }

        @Test
        @DisplayName("valueOf should throw for invalid name")
        void testValueOfInvalid() {
            assertThrows(IllegalArgumentException.class, () -> {
                InteractableType.valueOf("INVALID");
            });
        }

        private boolean containsType(InteractableType[] types, InteractableType target) {
            for (InteractableType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    // ==================== Display Name Tests ====================

    @Nested
    @DisplayName("Display Names")
    class DisplayNames {

        @Test
        @DisplayName("CHEST should have correct display name")
        void testChestDisplayName() {
            assertEquals("Chest", InteractableType.CHEST.getDisplayName());
        }

        @Test
        @DisplayName("WORKBENCH should have correct display name")
        void testWorkbenchDisplayName() {
            assertEquals("Workbench", InteractableType.WORKBENCH.getDisplayName());
        }

        @Test
        @DisplayName("ANVIL should have correct display name")
        void testAnvilDisplayName() {
            assertEquals("Anvil", InteractableType.ANVIL.getDisplayName());
        }

        @Test
        @DisplayName("SHRINE should have correct display name")
        void testShrineDisplayName() {
            assertEquals("Shrine", InteractableType.SHRINE.getDisplayName());
        }

        @Test
        @DisplayName("All display names should be non-empty")
        void testAllDisplayNamesNonEmpty() {
            for (InteractableType type : InteractableType.values()) {
                assertNotNull(type.getDisplayName());
                assertFalse(type.getDisplayName().isEmpty(),
                    type + " display name should not be empty");
            }
        }
    }

    // ==================== Interact Prompt Tests ====================

    @Nested
    @DisplayName("Interaction Prompts")
    class InteractionPrompts {

        @Test
        @DisplayName("CHEST should have correct interact prompt")
        void testChestInteractPrompt() {
            assertEquals("Press E to Open Storage",
                InteractableType.CHEST.getInteractPrompt());
        }

        @Test
        @DisplayName("WORKBENCH should have correct interact prompt")
        void testWorkbenchInteractPrompt() {
            assertEquals("Press E to Craft Items",
                InteractableType.WORKBENCH.getInteractPrompt());
        }

        @Test
        @DisplayName("ANVIL should have correct interact prompt")
        void testAnvilInteractPrompt() {
            assertEquals("Press E to Repair & Upgrade",
                InteractableType.ANVIL.getInteractPrompt());
        }

        @Test
        @DisplayName("SHRINE should have correct interact prompt")
        void testShrineInteractPrompt() {
            assertEquals("Press E to Receive Blessing",
                InteractableType.SHRINE.getInteractPrompt());
        }

        @Test
        @DisplayName("All prompts should start with 'Press E to'")
        void testAllPromptsFormat() {
            for (InteractableType type : InteractableType.values()) {
                String prompt = type.getInteractPrompt();
                assertTrue(prompt.startsWith("Press E to"),
                    type + " prompt should start with 'Press E to'");
            }
        }

        @Test
        @DisplayName("All prompts should be non-empty")
        void testAllPromptsNonEmpty() {
            for (InteractableType type : InteractableType.values()) {
                assertNotNull(type.getInteractPrompt());
                assertFalse(type.getInteractPrompt().isEmpty(),
                    type + " interact prompt should not be empty");
            }
        }
    }

    // ==================== Consistency Tests ====================

    @Nested
    @DisplayName("Consistency")
    class Consistency {

        @Test
        @DisplayName("Each type should have unique display name")
        void testUniqueDisplayNames() {
            InteractableType[] types = InteractableType.values();

            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i].getDisplayName(), types[j].getDisplayName(),
                        "Display names should be unique");
                }
            }
        }

        @Test
        @DisplayName("Each type should have unique interact prompt")
        void testUniqueInteractPrompts() {
            InteractableType[] types = InteractableType.values();

            for (int i = 0; i < types.length; i++) {
                for (int j = i + 1; j < types.length; j++) {
                    assertNotEquals(types[i].getInteractPrompt(), types[j].getInteractPrompt(),
                        "Interact prompts should be unique");
                }
            }
        }

        @Test
        @DisplayName("Display name should be different from enum name")
        void testDisplayNameDifferentFromEnumName() {
            for (InteractableType type : InteractableType.values()) {
                assertNotEquals(type.name(), type.getDisplayName(),
                    "Display name should be different from enum constant name");
            }
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should support all types in switch statement")
        void testSwitchStatement() {
            for (InteractableType type : InteractableType.values()) {
                String result;
                switch (type) {
                    case CHEST:
                        result = "Storage";
                        break;
                    case WORKBENCH:
                        result = "Crafting";
                        break;
                    case ANVIL:
                        result = "Repair";
                        break;
                    case SHRINE:
                        result = "Blessing";
                        break;
                    default:
                        result = null;
                        break;
                }

                assertNotNull(result, "Switch should handle all cases");
            }
        }

        @Test
        @DisplayName("Should work with comparison operations")
        void testComparison() {
            assertTrue(InteractableType.CHEST == InteractableType.CHEST);
            assertFalse(InteractableType.CHEST == InteractableType.WORKBENCH);
            assertTrue(InteractableType.CHEST.equals(InteractableType.CHEST));
            assertFalse(InteractableType.CHEST.equals(InteractableType.ANVIL));
        }

        @Test
        @DisplayName("Should be serializable via name")
        void testSerialization() {
            for (InteractableType type : InteractableType.values()) {
                String name = type.name();
                InteractableType restored = InteractableType.valueOf(name);
                assertEquals(type, restored, "Should restore from name");
            }
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle repeated calls to getDisplayName")
        void testRepeatedDisplayNameCalls() {
            InteractableType type = InteractableType.CHEST;
            String name1 = type.getDisplayName();
            String name2 = type.getDisplayName();

            assertEquals(name1, name2, "Should return same value");
        }

        @Test
        @DisplayName("Should handle repeated calls to getInteractPrompt")
        void testRepeatedInteractPromptCalls() {
            InteractableType type = InteractableType.WORKBENCH;
            String prompt1 = type.getInteractPrompt();
            String prompt2 = type.getInteractPrompt();

            assertEquals(prompt1, prompt2, "Should return same value");
        }

        @Test
        @DisplayName("valueOf should be case-sensitive")
        void testValueOfCaseSensitive() {
            assertThrows(IllegalArgumentException.class, () -> {
                InteractableType.valueOf("chest"); // lowercase should fail
            });

            assertThrows(IllegalArgumentException.class, () -> {
                InteractableType.valueOf("Chest"); // mixed case should fail
            });
        }

        @Test
        @DisplayName("Should handle null checks")
        void testNullChecks() {
            InteractableType type = InteractableType.CHEST;
            assertNotEquals(null, type);
            assertFalse(type.equals(null));
        }
    }
}
