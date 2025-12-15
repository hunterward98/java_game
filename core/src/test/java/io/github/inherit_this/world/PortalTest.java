package io.github.inherit_this.world;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for Portal - teleportation system
 */
@DisplayName("Portal Tests")
class PortalTest {

    // ==================== Factory Method Tests ====================

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("createDungeonEntrance should create dungeon entrance portal")
        void testCreateDungeonEntrance() {
            Portal portal = Portal.createDungeonEntrance(100f, 200f);

            assertNotNull(portal, "Portal should not be null");
            assertEquals(Portal.PortalType.DUNGEON_ENTRANCE, portal.getType(),
                "Should be dungeon entrance type");
            assertEquals(100f, portal.getWorldX(), 0.01f, "X position should match");
            assertEquals(200f, portal.getWorldY(), 0.01f, "Y position should match");
            assertEquals(1, portal.getTargetLevel(), "Should target level 1");
        }

        @Test
        @DisplayName("createTownReturn should create town return portal")
        void testCreateTownReturn() {
            Portal portal = Portal.createTownReturn(300f, 400f);

            assertNotNull(portal, "Portal should not be null");
            assertEquals(Portal.PortalType.TOWN_RETURN, portal.getType(),
                "Should be town return type");
            assertEquals(300f, portal.getWorldX(), 0.01f, "X position should match");
            assertEquals(400f, portal.getWorldY(), 0.01f, "Y position should match");
            assertEquals(0, portal.getTargetLevel(), "Should target level 0");
        }

        @Test
        @DisplayName("Factory methods should handle zero coordinates")
        void testFactoryMethodsZeroCoordinates() {
            Portal entrance = Portal.createDungeonEntrance(0f, 0f);
            Portal townReturn = Portal.createTownReturn(0f, 0f);

            assertEquals(0f, entrance.getWorldX(), 0.01f);
            assertEquals(0f, entrance.getWorldY(), 0.01f);
            assertEquals(0f, townReturn.getWorldX(), 0.01f);
            assertEquals(0f, townReturn.getWorldY(), 0.01f);
        }

        @Test
        @DisplayName("Factory methods should handle negative coordinates")
        void testFactoryMethodsNegativeCoordinates() {
            Portal entrance = Portal.createDungeonEntrance(-100f, -200f);
            Portal townReturn = Portal.createTownReturn(-300f, -400f);

            assertEquals(-100f, entrance.getWorldX(), 0.01f);
            assertEquals(-200f, entrance.getWorldY(), 0.01f);
            assertEquals(-300f, townReturn.getWorldX(), 0.01f);
            assertEquals(-400f, townReturn.getWorldY(), 0.01f);
        }

        @Test
        @DisplayName("Factory methods should handle large coordinates")
        void testFactoryMethodsLargeCoordinates() {
            Portal entrance = Portal.createDungeonEntrance(100000f, 200000f);

            assertEquals(100000f, entrance.getWorldX(), 0.01f);
            assertEquals(200000f, entrance.getWorldY(), 0.01f);
        }
    }

    // ==================== Getter Tests ====================

    @Nested
    @DisplayName("Getters")
    class Getters {

        @Test
        @DisplayName("getType should return portal type")
        void testGetType() {
            Portal entrance = Portal.createDungeonEntrance(0f, 0f);
            Portal townReturn = Portal.createTownReturn(0f, 0f);

            assertEquals(Portal.PortalType.DUNGEON_ENTRANCE, entrance.getType());
            assertEquals(Portal.PortalType.TOWN_RETURN, townReturn.getType());
        }

        @Test
        @DisplayName("getWorldX should return X coordinate")
        void testGetWorldX() {
            Portal portal = Portal.createDungeonEntrance(123.45f, 0f);
            assertEquals(123.45f, portal.getWorldX(), 0.01f);
        }

        @Test
        @DisplayName("getWorldY should return Y coordinate")
        void testGetWorldY() {
            Portal portal = Portal.createDungeonEntrance(0f, 678.90f);
            assertEquals(678.90f, portal.getWorldY(), 0.01f);
        }

        @Test
        @DisplayName("getTargetLevel should return target level")
        void testGetTargetLevel() {
            Portal entrance = Portal.createDungeonEntrance(0f, 0f);
            Portal townReturn = Portal.createTownReturn(0f, 0f);

            assertEquals(1, entrance.getTargetLevel(), "Dungeon entrance targets level 1");
            assertEquals(0, townReturn.getTargetLevel(), "Town return targets level 0");
        }
    }

    // ==================== Player Proximity Tests ====================

    @Nested
    @DisplayName("Player Proximity Detection")
    class PlayerProximity {

        @Test
        @DisplayName("isPlayerNear should return true when player is at portal position")
        void testIsPlayerNearAtPortal() {
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            assertTrue(portal.isPlayerNear(100f, 100f, 64f),
                "Player at portal position should be near");
        }

        @Test
        @DisplayName("isPlayerNear should return true when player is within range")
        void testIsPlayerNearWithinRange() {
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            assertTrue(portal.isPlayerNear(110f, 110f, 64f),
                "Player 10 units away should be within 64 unit range");
            assertTrue(portal.isPlayerNear(150f, 100f, 64f),
                "Player 50 units away should be within 64 unit range");
        }

        @Test
        @DisplayName("isPlayerNear should return false when player is out of range")
        void testIsPlayerNearOutOfRange() {
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            assertFalse(portal.isPlayerNear(200f, 200f, 64f),
                "Player 141 units away should be out of 64 unit range");
            assertFalse(portal.isPlayerNear(1000f, 1000f, 64f),
                "Player far away should be out of range");
        }

        @Test
        @DisplayName("isPlayerNear should work with different interaction ranges")
        void testIsPlayerNearDifferentRanges() {
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            // Small range
            assertFalse(portal.isPlayerNear(110f, 110f, 5f),
                "Player should be out of small range");

            // Medium range
            assertTrue(portal.isPlayerNear(110f, 110f, 20f),
                "Player should be within medium range");

            // Large range
            assertTrue(portal.isPlayerNear(200f, 200f, 200f),
                "Player should be within large range");
        }

        @Test
        @DisplayName("isPlayerNear should handle exact edge of range")
        void testIsPlayerNearExactEdge() {
            Portal portal = Portal.createDungeonEntrance(0f, 0f);

            // Player exactly at range distance (using Pythagorean theorem)
            // For range 10, player at (6, 8) should be exactly at edge (sqrt(36+64) = 10)
            assertTrue(portal.isPlayerNear(6f, 8f, 10f),
                "Player at exact range edge should be near");

            // Just outside range
            assertFalse(portal.isPlayerNear(6f, 8f, 9.9f),
                "Player just outside range should not be near");
        }

        @Test
        @DisplayName("isPlayerNear should work with zero range")
        void testIsPlayerNearZeroRange() {
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            assertTrue(portal.isPlayerNear(100f, 100f, 0f),
                "Player at portal with zero range should be near");
            assertFalse(portal.isPlayerNear(100.1f, 100f, 0f),
                "Player away from portal with zero range should not be near");
        }

        @Test
        @DisplayName("isPlayerNear should handle negative player positions")
        void testIsPlayerNearNegativePositions() {
            Portal portal = Portal.createDungeonEntrance(-100f, -100f);

            assertTrue(portal.isPlayerNear(-110f, -110f, 64f),
                "Should work with negative player positions");
            assertFalse(portal.isPlayerNear(-200f, -200f, 64f),
                "Should detect out of range with negative positions");
        }
    }

    // ==================== Interaction Text Tests ====================

    @Nested
    @DisplayName("Interaction Text")
    class InteractionText {

        @Test
        @DisplayName("getInteractionText should return dungeon entrance text")
        void testGetInteractionTextDungeonEntrance() {
            Portal portal = Portal.createDungeonEntrance(0f, 0f);
            String text = portal.getInteractionText();

            assertNotNull(text, "Interaction text should not be null");
            assertTrue(text.contains("dungeon"), "Should mention dungeon");
            assertTrue(text.contains("E"), "Should mention E key");
        }

        @Test
        @DisplayName("getInteractionText should return town return text")
        void testGetInteractionTextTownReturn() {
            Portal portal = Portal.createTownReturn(0f, 0f);
            String text = portal.getInteractionText();

            assertNotNull(text, "Interaction text should not be null");
            assertTrue(text.contains("town"), "Should mention town");
            assertTrue(text.contains("E"), "Should mention E key");
        }

        @Test
        @DisplayName("Interaction texts should be different for each type")
        void testInteractionTextsDifferent() {
            Portal entrance = Portal.createDungeonEntrance(0f, 0f);
            Portal townReturn = Portal.createTownReturn(0f, 0f);

            assertNotEquals(entrance.getInteractionText(), townReturn.getInteractionText(),
                "Different portal types should have different interaction texts");
        }
    }

    // ==================== PortalType Enum Tests ====================

    @Nested
    @DisplayName("PortalType Enum")
    class PortalTypeEnum {

        @Test
        @DisplayName("PortalType should have DUNGEON_ENTRANCE value")
        void testPortalTypeDungeonEntrance() {
            assertNotNull(Portal.PortalType.DUNGEON_ENTRANCE,
                "DUNGEON_ENTRANCE should exist");
        }

        @Test
        @DisplayName("PortalType should have TOWN_RETURN value")
        void testPortalTypeTownReturn() {
            assertNotNull(Portal.PortalType.TOWN_RETURN,
                "TOWN_RETURN should exist");
        }

        @Test
        @DisplayName("PortalType values should be accessible")
        void testPortalTypeValues() {
            Portal.PortalType[] values = Portal.PortalType.values();

            assertEquals(2, values.length, "Should have exactly 2 portal types");
            assertTrue(containsPortalType(values, Portal.PortalType.DUNGEON_ENTRANCE),
                "Should contain DUNGEON_ENTRANCE");
            assertTrue(containsPortalType(values, Portal.PortalType.TOWN_RETURN),
                "Should contain TOWN_RETURN");
        }

        private boolean containsPortalType(Portal.PortalType[] types, Portal.PortalType target) {
            for (Portal.PortalType type : types) {
                if (type == target) return true;
            }
            return false;
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration Scenarios")
    class IntegrationScenarios {

        @Test
        @DisplayName("Should handle complete portal lifecycle")
        void testCompletePortalLifecycle() {
            // Create portal
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            // Check properties
            assertEquals(Portal.PortalType.DUNGEON_ENTRANCE, portal.getType());
            assertEquals(100f, portal.getWorldX(), 0.01f);
            assertEquals(100f, portal.getWorldY(), 0.01f);

            // Check proximity
            assertTrue(portal.isPlayerNear(110f, 110f, 64f));

            // Get interaction text
            String text = portal.getInteractionText();
            assertNotNull(text);
        }

        @Test
        @DisplayName("Should handle multiple portals independently")
        void testMultiplePortals() {
            Portal portal1 = Portal.createDungeonEntrance(0f, 0f);
            Portal portal2 = Portal.createDungeonEntrance(1000f, 1000f);
            Portal portal3 = Portal.createTownReturn(500f, 500f);

            // Each portal should be independent
            assertNotEquals(portal1.getWorldX(), portal2.getWorldX());
            assertNotEquals(portal1.getType(), portal3.getType());

            // Proximity checks should be independent
            assertTrue(portal1.isPlayerNear(10f, 10f, 64f));
            assertFalse(portal2.isPlayerNear(10f, 10f, 64f));
        }
    }

    // ==================== Edge Cases ====================

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle very large interaction ranges")
        void testVeryLargeInteractionRange() {
            Portal portal = Portal.createDungeonEntrance(0f, 0f);

            assertTrue(portal.isPlayerNear(10000f, 10000f, 100000f),
                "Should handle very large interaction range");
        }

        @Test
        @DisplayName("Should handle float precision edge cases")
        void testFloatPrecisionEdgeCases() {
            Portal portal = Portal.createDungeonEntrance(0.1f, 0.1f);

            assertTrue(portal.isPlayerNear(0.1f, 0.1f, 1f),
                "Should handle float precision correctly");
        }

        @Test
        @DisplayName("Should maintain immutability of position")
        void testPositionImmutability() {
            Portal portal = Portal.createDungeonEntrance(100f, 100f);

            float x1 = portal.getWorldX();
            float y1 = portal.getWorldY();

            // Get multiple times - should always be same
            assertEquals(x1, portal.getWorldX(), 0.01f);
            assertEquals(y1, portal.getWorldY(), 0.01f);
        }
    }
}
