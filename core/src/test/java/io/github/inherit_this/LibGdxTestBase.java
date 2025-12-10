package io.github.inherit_this;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.Mockito;

/**
 * Base class for tests that require LibGDX to be initialized.
 * Sets up a headless LibGDX application for testing.
 */
public abstract class LibGdxTestBase {

    private static Application application;

    @BeforeAll
    public static void initializeLibGdx() {
        if (application == null) {
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            application = new HeadlessApplication(new ApplicationAdapter() {}, config);

            // Mock GL20 since it's needed by some LibGDX components
            Gdx.gl = Mockito.mock(GL20.class);
            Gdx.gl20 = Mockito.mock(GL20.class);
        }
    }

    @AfterAll
    public static void cleanupLibGdx() {
        if (application != null) {
            application.exit();
            application = null;
        }
    }
}
