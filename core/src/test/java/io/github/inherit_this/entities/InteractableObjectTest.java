package io.github.inherit_this.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for InteractableObject - chests, workbenches, etc.
 */
@DisplayName("InteractableObject Tests")
class InteractableObjectTest {

    @Mock
    private Texture mockTexture;

    @Mock
    private Model mockModel;

    private InteractableObject chest;
    private InteractableObject workbench;
    private InteractableObject anvil;
    private InteractableObject shrine;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chest = new InteractableObject(mockModel, mockTexture, 10f, 10f, InteractableType.CHEST);
        workbench = new InteractableObject(mockModel, mockTexture, 20f, 20f, InteractableType.WORKBENCH);
        anvil = new InteractableObject(mockModel, mockTexture, 30f, 30f, InteractableType.ANVIL);
        shrine = new InteractableObject(mockModel, mockTexture, 40f, 40f, InteractableType.SHRINE);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("Should initialize with correct position")
        void testInitializationPosition() {
            assertEquals(10f, chest.getPosition().x, 0.01f, "X position should match");
            assertEquals(10f, chest.getPosition().y, 0.01f, "Y position should match");
        }

        @Test
        @DisplayName("Should initialize with correct type")
        void testInitializationType() {
            assertEquals(InteractableType.CHEST, chest.getType(), "Type should be CHEST");
            assertEquals(InteractableType.WORKBENCH, workbench.getType(), "Type should be WORKBENCH");
            assertEquals(InteractableType.ANVIL, anvil.getType(), "Type should be ANVIL");
            assertEquals(InteractableType.SHRINE, shrine.getType(), "Type should be SHRINE");
        }

        @Test
        @DisplayName("Should initialize with model")
        void testInitializationModel() {
            assertNotNull(chest.getModel(), "Model should not be null");
            assertEquals(mockModel, chest.getModel(), "Model should match");
        }

        @Test
        @DisplayName("Should initialize as not interacted")
        void testInitializationNotInteracted() {
            assertFalse(chest.hasBeenInteracted(), "Should not be interacted initially");
        }

        @Test
        @DisplayName("Should initialize at different positions")
        void testDifferentPositions() {
            assertEquals(10f, chest.getPosition().x, 0.01f);
            assertEquals(20f, workbench.getPosition().x, 0.01f);
            assertEquals(30f, anvil.getPosition().x, 0.01f);
            assertEquals(40f, shrine.getPosition().x, 0.01f);
        }
    }

    // ==================== Interaction Tests ====================

    @Nested
    @DisplayName("Interaction System")
    class InteractionSystem {

        @Test
        @DisplayName("interact should mark object as interacted")
        void testInteract() {
            assertFalse(chest.hasBeenInteracted(), "Should not be interacted initially");

            boolean result = chest.interact();

            assertTrue(result, "Interaction should return true");
            assertTrue(chest.hasBeenInteracted(), "Should be marked as interacted");
        }

        @Test
        @DisplayName("interact should be callable multiple times")
        void testInteractMultipleTimes() {
            chest.interact();
            chest.interact();
            chest.interact();

            assertTrue(chest.hasBeenInteracted(), "Should remain interacted");
        }

        @Test
        @DisplayName("resetInteraction should clear interacted flag")
        void testResetInteraction() {
            chest.interact();
            assertTrue(chest.hasBeenInteracted(), "Should be interacted");

            chest.resetInteraction();

            assertFalse(chest.hasBeenInteracted(), "Should be reset to not interacted");
        }

        @Test
        @DisplayName("resetInteraction on non-interacted object should work")
        void testResetInteractionNonInteracted() {
            assertFalse(chest.hasBeenInteracted());

            chest.resetInteraction();

            assertFalse(chest.hasBeenInteracted(), "Should remain not interacted");
        }

        @Test
        @DisplayName("Different objects should track interaction independently")
        void testIndependentInteraction() {
            chest.interact();

            assertTrue(chest.hasBeenInteracted(), "Chest should be interacted");
            assertFalse(workbench.hasBeenInteracted(), "Workbench should not be interacted");
            assertFalse(anvil.hasBeenInteracted(), "Anvil should not be interacted");
        }
    }

    // ==================== Proximity Detection Tests ====================

    @Nested
    @DisplayName("Proximity Detection")
    class ProximityDetection {

        @Test
        @DisplayName("isPlayerNear should return true when player at object position")
        void testIsPlayerNearAtPosition() {
            assertTrue(chest.isPlayerNear(10.5f, 10.5f, 1.0f),
                "Player at center of object should be near");
        }

        @Test
        @DisplayName("isPlayerNear should return true when player within range")
        void testIsPlayerNearWithinRange() {
            assertTrue(chest.isPlayerNear(10.0f, 10.0f, 1.0f),
                "Player at object corner should be within range");
            assertTrue(chest.isPlayerNear(11.0f, 10.0f, 1.0f),
                "Player 0.5 tiles away should be within range");
        }

        @Test
        @DisplayName("isPlayerNear should return false when player out of range")
        void testIsPlayerNearOutOfRange() {
            assertFalse(chest.isPlayerNear(15.0f, 15.0f, 1.0f),
                "Player far away should not be near");
            assertFalse(chest.isPlayerNear(20.0f, 20.0f, 1.0f),
                "Player very far away should not be near");
        }

        @Test
        @DisplayName("isPlayerNear should work with different interaction distances")
        void testIsPlayerNearDifferentDistances() {
            // Small distance - chest at (10,10), player at (12,12)
            // Distance from (10.5,10.5) to (12,12) = sqrt(1.5^2 + 1.5^2) ≈ 2.12
            assertFalse(chest.isPlayerNear(12.0f, 12.0f, 0.5f),
                "Player should be out of small range");

            // Medium distance (2.12 < 3.0)
            assertTrue(chest.isPlayerNear(12.0f, 12.0f, 3.0f),
                "Player should be within medium range");

            // Large distance
            assertTrue(chest.isPlayerNear(15.0f, 15.0f, 10.0f),
                "Player should be within large range");
        }

        @Test
        @DisplayName("isPlayerNear should handle edge of range")
        void testIsPlayerNearEdge() {
            // Player exactly at range distance
            float range = 2.0f;
            // Distance from (10.5, 10.5) with range 2.0
            assertTrue(chest.isPlayerNear(12.5f, 10.5f, range),
                "Player at edge of range should be near");
        }

        @Test
        @DisplayName("isPlayerNear should work with zero range")
        void testIsPlayerNearZeroRange() {
            assertTrue(chest.isPlayerNear(10.5f, 10.5f, 0.0f),
                "Player at exact center with zero range should be near");
            assertFalse(chest.isPlayerNear(11.0f, 10.5f, 0.0f),
                "Player away from center with zero range should not be near");
        }

        @Test
        @DisplayName("isPlayerNear should handle negative player positions")
        void testIsPlayerNearNegativePositions() {
            InteractableObject negativeChest = new InteractableObject(
                mockModel, mockTexture, -10f, -10f, InteractableType.CHEST
            );

            assertTrue(negativeChest.isPlayerNear(-9.5f, -9.5f, 1.0f),
                "Should work with negative positions");
            assertFalse(negativeChest.isPlayerNear(-5.0f, -5.0f, 1.0f),
                "Should detect out of range with negative positions");
        }
    }

    // ==================== Contains Tests ====================

    @Nested
    @DisplayName("Bounds Checking")
    class BoundsChecking {

        @Test
        @DisplayName("contains should return true for position inside object")
        void testContainsInside() {
            assertTrue(chest.contains(10.0f, 10.0f),
                "Should contain position at object corner");
            assertTrue(chest.contains(10.5f, 10.5f),
                "Should contain position at object center");
            assertTrue(chest.contains(10.9f, 10.9f),
                "Should contain position near edge");
        }

        @Test
        @DisplayName("contains should return false for position outside object")
        void testContainsOutside() {
            assertFalse(chest.contains(9.9f, 10.0f),
                "Should not contain position to the left");
            assertFalse(chest.contains(11.0f, 10.0f),
                "Should not contain position to the right");
            assertFalse(chest.contains(10.0f, 9.9f),
                "Should not contain position below");
            assertFalse(chest.contains(10.0f, 11.0f),
                "Should not contain position above");
        }

        @Test
        @DisplayName("contains should handle edge cases")
        void testContainsEdgeCases() {
            assertTrue(chest.contains(10.0f, 10.0f), "Left edge should be inclusive");
            assertTrue(chest.contains(10.999f, 10.999f), "Just inside right edge");
            assertFalse(chest.contains(11.0f, 11.0f), "Right edge should be exclusive");
        }

        @Test
        @DisplayName("contains should work for different objects")
        void testContainsDifferentObjects() {
            assertTrue(workbench.contains(20.5f, 20.5f),
                "Workbench should contain its center");
            assertFalse(workbench.contains(10.5f, 10.5f),
                "Workbench should not contain chest position");
        }
    }

    // ==================== Getter Tests ====================

    @Nested
    @DisplayName("Getters")
    class Getters {

        @Test
        @DisplayName("getModel should return model")
        void testGetModel() {
            assertEquals(mockModel, chest.getModel(), "Model should match");
        }

        @Test
        @DisplayName("getType should return type")
        void testGetType() {
            assertEquals(InteractableType.CHEST, chest.getType());
            assertEquals(InteractableType.WORKBENCH, workbench.getType());
            assertEquals(InteractableType.ANVIL, anvil.getType());
            assertEquals(InteractableType.SHRINE, shrine.getType());
        }

        @Test
        @DisplayName("hasBeenInteracted should reflect interaction state")
        void testHasBeenInteracted() {
            assertFalse(chest.hasBeenInteracted());
            chest.interact();
            assertTrue(chest.hasBeenInteracted());
        }

        @Test
        @DisplayName("getPosition should return position vector")
        void testGetPosition() {
            assertNotNull(chest.getPosition(), "Position should not be null");
            assertEquals(10f, chest.getPosition().x, 0.01f);
            assertEquals(10f, chest.getPosition().y, 0.01f);
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should handle complete interaction workflow")
        void testCompleteWorkflow() {
            // Initial state
            assertFalse(chest.hasBeenInteracted());
            assertEquals(InteractableType.CHEST, chest.getType());

            // Player approaches
            assertFalse(chest.isPlayerNear(5.0f, 5.0f, 1.0f), "Player too far");
            assertTrue(chest.isPlayerNear(10.5f, 10.5f, 1.0f), "Player close enough");

            // Player interacts
            boolean result = chest.interact();
            assertTrue(result);
            assertTrue(chest.hasBeenInteracted());

            // Player walks away and comes back
            assertFalse(chest.isPlayerNear(20.0f, 20.0f, 1.0f), "Player walked away");
            assertTrue(chest.hasBeenInteracted(), "Should remember interaction");

            // Reset for another player or respawn
            chest.resetInteraction();
            assertFalse(chest.hasBeenInteracted());
        }

        @Test
        @DisplayName("Should handle multiple objects independently")
        void testMultipleObjects() {
            // Interact with chest
            chest.interact();
            assertTrue(chest.hasBeenInteracted());

            // Other objects should not be affected
            assertFalse(workbench.hasBeenInteracted());
            assertFalse(anvil.hasBeenInteracted());

            // Each object has its own position
            assertTrue(chest.contains(10.5f, 10.5f));
            assertFalse(chest.contains(20.5f, 20.5f));
            assertTrue(workbench.contains(20.5f, 20.5f));
        }

        @Test
        @DisplayName("Should handle player moving between objects")
        void testPlayerMovement() {
            // Player at chest
            assertTrue(chest.isPlayerNear(10.5f, 10.5f, 1.0f));
            assertFalse(workbench.isPlayerNear(10.5f, 10.5f, 1.0f));

            // Player moves to workbench
            assertFalse(chest.isPlayerNear(20.5f, 20.5f, 1.0f));
            assertTrue(workbench.isPlayerNear(20.5f, 20.5f, 1.0f));
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle objects at origin")
        void testObjectAtOrigin() {
            InteractableObject origin = new InteractableObject(
                mockModel, mockTexture, 0f, 0f, InteractableType.CHEST
            );

            assertEquals(0f, origin.getPosition().x, 0.01f);
            assertEquals(0f, origin.getPosition().y, 0.01f);
            assertTrue(origin.contains(0.0f, 0.0f));
            assertTrue(origin.isPlayerNear(0.5f, 0.5f, 1.0f));
        }

        @Test
        @DisplayName("Should handle objects at extreme positions")
        void testExtremePositions() {
            InteractableObject far = new InteractableObject(
                mockModel, mockTexture, 9999f, 9999f, InteractableType.CHEST
            );

            assertEquals(9999f, far.getPosition().x, 0.01f);
            assertTrue(far.contains(9999.5f, 9999.5f));
        }

        @Test
        @DisplayName("Should handle repeated resets")
        void testRepeatedResets() {
            for (int i = 0; i < 100; i++) {
                chest.resetInteraction();
                assertFalse(chest.hasBeenInteracted());
            }
        }

        @Test
        @DisplayName("Should handle repeated interactions")
        void testRepeatedInteractions() {
            for (int i = 0; i < 100; i++) {
                chest.interact();
                assertTrue(chest.hasBeenInteracted());
            }
        }

        @Test
        @DisplayName("Should handle very large interaction distances")
        void testVeryLargeDistance() {
            assertTrue(chest.isPlayerNear(1000f, 1000f, 10000f),
                "Should work with very large distance");
        }

        @Test
        @DisplayName("Should handle all interactable types")
        void testAllTypes() {
            InteractableType[] types = InteractableType.values();
            assertEquals(4, types.length, "Should have 4 interactable types");

            for (InteractableType type : types) {
                InteractableObject obj = new InteractableObject(
                    mockModel, mockTexture, 0f, 0f, type
                );
                assertEquals(type, obj.getType());
                assertFalse(obj.hasBeenInteracted());
            }
        }
    }
}
