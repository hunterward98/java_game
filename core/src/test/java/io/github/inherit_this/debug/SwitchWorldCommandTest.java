package io.github.inherit_this.debug;

import io.github.inherit_this.screens.GameScreen;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SwitchWorldCommand Tests")
class SwitchWorldCommandTest {

    private GameScreen mockGameScreen;
    private DebugConsole mockConsole;
    private SwitchWorldCommand command;

    @BeforeEach
    void setUp() {
        mockGameScreen = mock(GameScreen.class);
        mockConsole = mock(DebugConsole.class);
        command = new SwitchWorldCommand(mockGameScreen);
    }

    @Nested
    @DisplayName("Command Properties")
    class CommandProperties {

        @Test
        @DisplayName("getName should return 'switchworld'")
        void testGetName() {
            assertEquals("switchworld", command.getName());
        }

        @Test
        @DisplayName("getDescription should return usage information")
        void testGetDescription() {
            String description = command.getDescription();
            assertNotNull(description);
            assertTrue(description.contains("world") || description.contains("Switch"));
        }
    }

    @Nested
    @DisplayName("Valid Execution")
    class ValidExecution {

        @Test
        @DisplayName("Should switch to static world without map path")
        void testSwitchToStaticWorld() {
            command.execute(new String[]{"static"}, mockConsole);

            verify(mockGameScreen).switchWorld("static", null);
            verify(mockConsole).log(contains("Switched to static world"));
        }

        @Test
        @DisplayName("Should switch to static world with map path")
        void testSwitchToStaticWorldWithPath() {
            command.execute(new String[]{"static", "maps/custom.json"}, mockConsole);

            verify(mockGameScreen).switchWorld("static", "maps/custom.json");
            verify(mockConsole).log(contains("maps/custom.json"));
        }

        @Test
        @DisplayName("Should switch to procedural world")
        void testSwitchToProceduralWorld() {
            command.execute(new String[]{"procedural"}, mockConsole);

            verify(mockGameScreen).switchWorld("procedural", null);
            verify(mockConsole).log(contains("Switched to procedural world"));
        }

        @Test
        @DisplayName("Should handle uppercase world type")
        void testUppercaseWorldType() {
            command.execute(new String[]{"STATIC"}, mockConsole);

            verify(mockGameScreen).switchWorld("static", null);
        }

        @Test
        @DisplayName("Should handle mixed case world type")
        void testMixedCaseWorldType() {
            command.execute(new String[]{"Procedural"}, mockConsole);

            verify(mockGameScreen).switchWorld("procedural", null);
        }
    }

    @Nested
    @DisplayName("Invalid Input")
    class InvalidInput {

        @Test
        @DisplayName("Should show usage when no arguments provided")
        void testNoArguments() {
            command.execute(new String[]{}, mockConsole);

            verify(mockConsole, atLeastOnce()).log(contains("Usage"));
            verify(mockConsole, atLeastOnce()).log(contains("switchworld"));
            verifyNoInteractions(mockGameScreen);
        }

        @Test
        @DisplayName("Should reject invalid world type")
        void testInvalidWorldType() {
            command.execute(new String[]{"invalid"}, mockConsole);

            verify(mockConsole).log(contains("Invalid world type"));
            verifyNoInteractions(mockGameScreen);
        }

        @Test
        @DisplayName("Should reject unknown world type")
        void testUnknownWorldType() {
            command.execute(new String[]{"dungeon"}, mockConsole);

            verify(mockConsole).log(contains("Invalid world type"));
            verify(mockConsole).log(contains("static' or 'procedural"));
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("Should handle exception from switchWorld")
        void testSwitchWorldException() {
            doThrow(new RuntimeException("Failed to load map")).when(mockGameScreen)
                .switchWorld(anyString(), any());

            command.execute(new String[]{"static"}, mockConsole);

            verify(mockConsole).log(contains("Error switching world"));
            verify(mockConsole).log(contains("Failed to load map"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("Should ignore extra arguments beyond map path")
        void testExtraArguments() {
            command.execute(new String[]{"static", "maps/test.json", "extra"}, mockConsole);

            verify(mockGameScreen).switchWorld("static", "maps/test.json");
        }

        @Test
        @DisplayName("Should handle empty map path")
        void testEmptyMapPath() {
            command.execute(new String[]{"static", ""}, mockConsole);

            verify(mockGameScreen).switchWorld("static", "");
        }
    }
}
