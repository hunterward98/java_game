package io.github.inherit_this.world;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DungeonConfigTest {

    @Test
    public void testConstructorWithAllParameters() {
        long seed = 12345L;
        int level = 5;
        int width = 100;
        int height = 80;
        DungeonConfig.DungeonStyle style = DungeonConfig.DungeonStyle.OPEN;
        DungeonConfig.DungeonLayout layout = DungeonConfig.DungeonLayout.WINDING;

        DungeonConfig config = new DungeonConfig(seed, level, width, height, style, layout);

        assertEquals(seed, config.getSeed());
        assertEquals(level, config.getDungeonLevel());
        assertEquals(width, config.getWidthInChunks());
        assertEquals(height, config.getHeightInChunks());
        assertEquals(style, config.getStyle());
        assertEquals(layout, config.getLayout());
    }

    @Test
    public void testDefaultDimensionsConstructor() {
        long seed = 67890L;
        int level = 10;
        DungeonConfig.DungeonStyle style = DungeonConfig.DungeonStyle.NARROW;
        DungeonConfig.DungeonLayout layout = DungeonConfig.DungeonLayout.STRAIGHT;

        DungeonConfig config = new DungeonConfig(seed, level, style, layout);

        assertEquals(seed, config.getSeed());
        assertEquals(level, config.getDungeonLevel());
        assertEquals(64, config.getWidthInChunks());
        assertEquals(64, config.getHeightInChunks());
        assertEquals(style, config.getStyle());
        assertEquals(layout, config.getLayout());
    }

    @Test
    public void testOpenStyleParameters() {
        DungeonConfig config = new DungeonConfig(
            1000L,
            1,
            DungeonConfig.DungeonStyle.OPEN,
            DungeonConfig.DungeonLayout.WINDING
        );

        assertEquals(5, config.getRoomMinSize());
        assertEquals(12, config.getRoomMaxSize());
        assertEquals(3, config.getCorridorWidth());
        assertEquals(0.7f, config.getRoomDensity(), 0.001f);
    }

    @Test
    public void testNarrowStyleParameters() {
        DungeonConfig config = new DungeonConfig(
            2000L,
            1,
            DungeonConfig.DungeonStyle.NARROW,
            DungeonConfig.DungeonLayout.STRAIGHT
        );

        assertEquals(3, config.getRoomMinSize());
        assertEquals(6, config.getRoomMaxSize());
        assertEquals(1, config.getCorridorWidth());
        assertEquals(0.3f, config.getRoomDensity(), 0.001f);
    }

    @Test
    public void testCreateRandomGeneratesUniqueSeeds() {
        DungeonConfig config1 = DungeonConfig.createRandom(
            5,
            DungeonConfig.DungeonStyle.OPEN,
            DungeonConfig.DungeonLayout.WINDING
        );

        // Sleep briefly to ensure different nanoTime
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        DungeonConfig config2 = DungeonConfig.createRandom(
            5,
            DungeonConfig.DungeonStyle.OPEN,
            DungeonConfig.DungeonLayout.WINDING
        );

        // Seeds should be different since they use System.nanoTime()
        assertNotEquals(config1.getSeed(), config2.getSeed());
    }

    @Test
    public void testCreateRandomWithDifferentLevels() {
        DungeonConfig config = DungeonConfig.createRandom(
            25,
            DungeonConfig.DungeonStyle.NARROW,
            DungeonConfig.DungeonLayout.STRAIGHT
        );

        assertEquals(25, config.getDungeonLevel());
        assertEquals(64, config.getWidthInChunks());
        assertEquals(64, config.getHeightInChunks());
    }

    @Test
    public void testDungeonStyleEnum() {
        assertEquals(2, DungeonConfig.DungeonStyle.values().length);
        assertNotNull(DungeonConfig.DungeonStyle.valueOf("OPEN"));
        assertNotNull(DungeonConfig.DungeonStyle.valueOf("NARROW"));
    }

    @Test
    public void testDungeonLayoutEnum() {
        assertEquals(2, DungeonConfig.DungeonLayout.values().length);
        assertNotNull(DungeonConfig.DungeonLayout.valueOf("WINDING"));
        assertNotNull(DungeonConfig.DungeonLayout.valueOf("STRAIGHT"));
    }

    @Test
    public void testDifferentStyleLayoutCombinations() {
        // Test all combinations to ensure they work
        DungeonConfig config1 = new DungeonConfig(
            1L, 1,
            DungeonConfig.DungeonStyle.OPEN,
            DungeonConfig.DungeonLayout.WINDING
        );

        DungeonConfig config2 = new DungeonConfig(
            2L, 1,
            DungeonConfig.DungeonStyle.OPEN,
            DungeonConfig.DungeonLayout.STRAIGHT
        );

        DungeonConfig config3 = new DungeonConfig(
            3L, 1,
            DungeonConfig.DungeonStyle.NARROW,
            DungeonConfig.DungeonLayout.WINDING
        );

        DungeonConfig config4 = new DungeonConfig(
            4L, 1,
            DungeonConfig.DungeonStyle.NARROW,
            DungeonConfig.DungeonLayout.STRAIGHT
        );

        // Verify they all have correct styles/layouts
        assertEquals(DungeonConfig.DungeonStyle.OPEN, config1.getStyle());
        assertEquals(DungeonConfig.DungeonLayout.WINDING, config1.getLayout());

        assertEquals(DungeonConfig.DungeonStyle.OPEN, config2.getStyle());
        assertEquals(DungeonConfig.DungeonLayout.STRAIGHT, config2.getLayout());

        assertEquals(DungeonConfig.DungeonStyle.NARROW, config3.getStyle());
        assertEquals(DungeonConfig.DungeonLayout.WINDING, config3.getLayout());

        assertEquals(DungeonConfig.DungeonStyle.NARROW, config4.getStyle());
        assertEquals(DungeonConfig.DungeonLayout.STRAIGHT, config4.getLayout());
    }
}
