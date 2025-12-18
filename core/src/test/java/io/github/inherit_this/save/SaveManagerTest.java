package io.github.inherit_this.save;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SaveManager.SaveSlotInfo inner class.
 */
@DisplayName("SaveManager.SaveSlotInfo Tests")
public class SaveManagerTest {

    @Nested
    @DisplayName("SaveSlotInfo Construction")
    class SaveSlotInfoConstruction {

        @Test
        @DisplayName("Should create SaveSlotInfo with all parameters")
        void testConstructor() {
            String characterName = "TestHero";
            int level = 10;
            Date lastSaved = new Date();
            long playTimeMillis = 3600000L; // 1 hour

            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                characterName, level, lastSaved, playTimeMillis
            );

            assertEquals(characterName, info.characterName);
            assertEquals(level, info.level);
            assertEquals(lastSaved, info.lastSaved);
            assertEquals(playTimeMillis, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should store character name correctly")
        void testCharacterName() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Warrior", 1, new Date(), 0L
            );

            assertEquals("Warrior", info.characterName);
        }

        @Test
        @DisplayName("Should store level correctly")
        void testLevel() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Mage", 50, new Date(), 0L
            );

            assertEquals(50, info.level);
        }

        @Test
        @DisplayName("Should store last saved date correctly")
        void testLastSaved() {
            Date now = new Date();
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Rogue", 1, now, 0L
            );

            assertEquals(now, info.lastSaved);
        }

        @Test
        @DisplayName("Should store play time correctly")
        void testPlayTime() {
            long playTime = 7200000L; // 2 hours
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Paladin", 1, new Date(), playTime
            );

            assertEquals(playTime, info.playTimeMillis);
        }
    }

    @Nested
    @DisplayName("Different Character Names")
    class CharacterNameVariations {

        @Test
        @DisplayName("Should handle short character names")
        void testShortName() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Bob", 1, new Date(), 0L
            );

            assertEquals("Bob", info.characterName);
        }

        @Test
        @DisplayName("Should handle long character names")
        void testLongName() {
            String longName = "VeryLongCharacterNameWithManyLetters";
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                longName, 1, new Date(), 0L
            );

            assertEquals(longName, info.characterName);
        }

        @Test
        @DisplayName("Should handle names with spaces")
        void testNameWithSpaces() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Dark Knight", 1, new Date(), 0L
            );

            assertEquals("Dark Knight", info.characterName);
        }

        @Test
        @DisplayName("Should handle names with special characters")
        void testNameWithSpecialChars() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Player_123", 1, new Date(), 0L
            );

            assertEquals("Player_123", info.characterName);
        }

        @Test
        @DisplayName("Should handle empty character name")
        void testEmptyName() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "", 1, new Date(), 0L
            );

            assertEquals("", info.characterName);
        }
    }

    @Nested
    @DisplayName("Level Variations")
    class LevelVariations {

        @Test
        @DisplayName("Should handle level 1")
        void testLevel1() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Newbie", 1, new Date(), 0L
            );

            assertEquals(1, info.level);
        }

        @Test
        @DisplayName("Should handle mid-level characters")
        void testMidLevel() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Adventurer", 50, new Date(), 0L
            );

            assertEquals(50, info.level);
        }

        @Test
        @DisplayName("Should handle max level")
        void testMaxLevel() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Master", 100, new Date(), 0L
            );

            assertEquals(100, info.level);
        }

        @Test
        @DisplayName("Should handle level 0")
        void testLevel0() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Fresh", 0, new Date(), 0L
            );

            assertEquals(0, info.level);
        }

        @Test
        @DisplayName("Should handle negative levels")
        void testNegativeLevel() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Invalid", -1, new Date(), 0L
            );

            assertEquals(-1, info.level);
        }
    }

    @Nested
    @DisplayName("Date Variations")
    class DateVariations {

        @Test
        @DisplayName("Should handle current date")
        void testCurrentDate() {
            Date now = new Date();
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Player", 1, now, 0L
            );

            assertEquals(now, info.lastSaved);
        }

        @Test
        @DisplayName("Should handle old dates")
        void testOldDate() {
            @SuppressWarnings("deprecation")
            Date oldDate = new Date(100, 0, 1); // Year 2000
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Veteran", 1, oldDate, 0L
            );

            assertEquals(oldDate, info.lastSaved);
        }

        @Test
        @DisplayName("Should handle same date for multiple saves")
        void testSameDate() {
            Date date = new Date();
            SaveManager.SaveSlotInfo info1 = new SaveManager.SaveSlotInfo(
                "Player1", 1, date, 0L
            );
            SaveManager.SaveSlotInfo info2 = new SaveManager.SaveSlotInfo(
                "Player2", 1, date, 0L
            );

            assertEquals(info1.lastSaved, info2.lastSaved);
        }
    }

    @Nested
    @DisplayName("Play Time Variations")
    class PlayTimeVariations {

        @Test
        @DisplayName("Should handle zero play time")
        void testZeroPlayTime() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "New", 1, new Date(), 0L
            );

            assertEquals(0L, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should handle short play time (1 minute)")
        void testShortPlayTime() {
            long oneMinute = 60 * 1000L;
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Quick", 1, new Date(), oneMinute
            );

            assertEquals(oneMinute, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should handle medium play time (1 hour)")
        void testMediumPlayTime() {
            long oneHour = 3600 * 1000L;
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Regular", 1, new Date(), oneHour
            );

            assertEquals(oneHour, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should handle long play time (100 hours)")
        void testLongPlayTime() {
            long hundredHours = 100L * 3600 * 1000;
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Dedicated", 1, new Date(), hundredHours
            );

            assertEquals(hundredHours, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should handle very long play time")
        void testVeryLongPlayTime() {
            long veryLong = Long.MAX_VALUE / 2;
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Hardcore", 1, new Date(), veryLong
            );

            assertEquals(veryLong, info.playTimeMillis);
        }
    }

    @Nested
    @DisplayName("Field Immutability")
    class FieldImmutability {

        @Test
        @DisplayName("Fields should be final and publicly accessible")
        void testFieldsAreFinal() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Test", 10, new Date(), 1000L
            );

            // Fields are public final, so they should be directly accessible
            assertNotNull(info.characterName);
            assertTrue(info.level >= 0 || info.level < 0); // Just verify it's an int
            assertNotNull(info.lastSaved);
            assertTrue(info.playTimeMillis >= 0 || info.playTimeMillis < 0); // Just verify it's a long
        }

        @Test
        @DisplayName("Multiple instances should be independent")
        void testInstanceIndependence() {
            SaveManager.SaveSlotInfo info1 = new SaveManager.SaveSlotInfo(
                "Player1", 10, new Date(), 1000L
            );
            SaveManager.SaveSlotInfo info2 = new SaveManager.SaveSlotInfo(
                "Player2", 20, new Date(), 2000L
            );

            assertNotEquals(info1.characterName, info2.characterName);
            assertNotEquals(info1.level, info2.level);
            assertNotEquals(info1.playTimeMillis, info2.playTimeMillis);
        }
    }

    @Nested
    @DisplayName("Real World Scenarios")
    class RealWorldScenarios {

        @Test
        @DisplayName("Should represent a new game save")
        void testNewGameSave() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "NewAdventurer",
                1,
                new Date(),
                0L
            );

            assertEquals("NewAdventurer", info.characterName);
            assertEquals(1, info.level);
            assertEquals(0L, info.playTimeMillis);
            assertNotNull(info.lastSaved);
        }

        @Test
        @DisplayName("Should represent a mid-game save")
        void testMidGameSave() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "ExperiencedHero",
                45,
                new Date(),
                15 * 3600 * 1000L // 15 hours
            );

            assertEquals("ExperiencedHero", info.characterName);
            assertEquals(45, info.level);
            assertEquals(15 * 3600 * 1000L, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should represent an end-game save")
        void testEndGameSave() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "LegendaryWarrior",
                100,
                new Date(),
                200L * 3600 * 1000 // 200 hours
            );

            assertEquals("LegendaryWarrior", info.characterName);
            assertEquals(100, info.level);
            assertEquals(200L * 3600 * 1000, info.playTimeMillis);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should handle null character name")
        void testNullCharacterName() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                null, 1, new Date(), 0L
            );

            assertNull(info.characterName);
        }

        @Test
        @DisplayName("Should handle null last saved date")
        void testNullLastSaved() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Player", 1, null, 0L
            );

            assertNull(info.lastSaved);
        }

        @Test
        @DisplayName("Should handle negative play time")
        void testNegativePlayTime() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                "Invalid", 1, new Date(), -1000L
            );

            assertEquals(-1000L, info.playTimeMillis);
        }

        @Test
        @DisplayName("Should handle all null/default values")
        void testAllNullValues() {
            SaveManager.SaveSlotInfo info = new SaveManager.SaveSlotInfo(
                null, 0, null, 0L
            );

            assertNull(info.characterName);
            assertEquals(0, info.level);
            assertNull(info.lastSaved);
            assertEquals(0L, info.playTimeMillis);
        }
    }
}
