package io.github.inherit_this.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.inherit_this.Main;
import io.github.inherit_this.ui.MenuButton;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.util.FontManager;

public class CharacterCreationScreen extends BaseScreen {

    private Texture background;
    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;
    private Vector3 touchPos = new Vector3();

    private String characterName = "";
    private Rectangle nameInputBox;
    private MenuButton startGameButton;
    private MenuButton backButton;

    private boolean nameInputFocused = false;
    private static final int MAX_NAME_LENGTH = 20;

    // UI styling
    private static final Color INPUT_BOX_COLOR = new Color(0.1f, 0.1f, 0.1f, 0.9f);
    private static final Color INPUT_BOX_FOCUSED_COLOR = new Color(0.2f, 0.2f, 0.3f, 0.9f);
    private static final Color INPUT_BOX_BORDER_COLOR = new Color(0.6f, 0.6f, 0.8f, 1f);

    public CharacterCreationScreen(Main game) {
        super(game);

        // Create camera and viewport
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.apply();
        camera.zoom = 1f / Constants.PIXEL_SCALE;
        camera.position.set(0, 0, 0);
        camera.update();

        background = new Texture("background.jpg");
        shapeRenderer = new ShapeRenderer();
        font = FontManager.getInstance().getMenuFont();

        // Create text input box
        nameInputBox = new Rectangle(-150, 20, 300, 50);

        // Create buttons
        startGameButton = new MenuButton("Start Game", font, 0, 0);
        backButton = new MenuButton("Back", font, 0, 0);

        // Position buttons
        startGameButton.bounds.x = -startGameButton.bounds.width / 2f;
        startGameButton.bounds.y = -40;

        backButton.bounds.x = -backButton.bounds.width / 2f;
        backButton.bounds.y = -100;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Update touch position
        touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(touchPos);

        // Draw background
        batch.begin();
        float halfWidth = viewport.getWorldWidth() / 2;
        float halfHeight = viewport.getWorldHeight() / 2;
        batch.draw(background, -halfWidth, -halfHeight, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();

        // Draw title
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Create Your Character", -120, 100);
        batch.end();

        // Draw name input box
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(nameInputFocused ? INPUT_BOX_FOCUSED_COLOR : INPUT_BOX_COLOR);
        shapeRenderer.rect(nameInputBox.x, nameInputBox.y, nameInputBox.width, nameInputBox.height);
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(INPUT_BOX_BORDER_COLOR);
        shapeRenderer.rect(nameInputBox.x, nameInputBox.y, nameInputBox.width, nameInputBox.height);
        shapeRenderer.end();

        // Draw character name with cursor
        batch.begin();
        font.setColor(Color.WHITE);
        String displayText = characterName.isEmpty() ? "Enter name..." : characterName;
        Color textColor = characterName.isEmpty() ? new Color(0.5f, 0.5f, 0.5f, 1f) : Color.WHITE;
        font.setColor(textColor);

        float textX = nameInputBox.x + 10;
        float textY = nameInputBox.y + nameInputBox.height / 2 + font.getCapHeight() / 2;
        font.draw(batch, displayText, textX, textY);

        // Draw blinking cursor if focused
        if (nameInputFocused && !characterName.isEmpty() && ((int)(System.currentTimeMillis() / 500) % 2 == 0)) {
            font.setColor(Color.WHITE);
            float cursorX = textX + font.getSpaceXadvance() * characterName.length();
            font.draw(batch, "|", cursorX, textY);
        }
        batch.end();

        // Update button hover states
        startGameButton.updateHover(touchPos.x, touchPos.y);
        backButton.updateHover(touchPos.x, touchPos.y);

        // Disable start button if name is empty
        startGameButton.setDisabled(characterName.trim().isEmpty());

        // Render buttons
        batch.begin();
        startGameButton.render(batch, shapeRenderer);
        backButton.render(batch, shapeRenderer);
        batch.end();

        handleInput();
    }

    private void handleInput() {
        // Handle clicking on the input box
        if (Gdx.input.justTouched()) {
            if (nameInputBox.contains(touchPos.x, touchPos.y)) {
                nameInputFocused = true;
            } else {
                nameInputFocused = false;
            }

            // Handle button clicks
            if (startGameButton.contains(touchPos.x, touchPos.y) && !startGameButton.isDisabled()) {
                game.setScreen(new GameScreen(game, characterName.trim()));
            }

            if (backButton.contains(touchPos.x, touchPos.y)) {
                game.setScreen(new MainMenuScreen(game));
            }
        }

        // Handle keyboard input when focused
        if (nameInputFocused) {
            // Handle text input
            for (int i = 0; i < Input.Keys.MAX_KEYCODE; i++) {
                if (Gdx.input.isKeyJustPressed(i)) {
                    handleKeyPress(i);
                }
            }
        }
    }

    private void handleKeyPress(int keycode) {
        if (keycode == Input.Keys.BACKSPACE) {
            if (characterName.length() > 0) {
                characterName = characterName.substring(0, characterName.length() - 1);
            }
        } else if (keycode == Input.Keys.ENTER) {
            if (!characterName.trim().isEmpty()) {
                game.setScreen(new GameScreen(game, characterName.trim()));
            }
        } else if (keycode == Input.Keys.ESCAPE) {
            game.setScreen(new MainMenuScreen(game));
        } else if (characterName.length() < MAX_NAME_LENGTH) {
            // Handle letter and number keys
            char character = getCharFromKeycode(keycode);
            if (character != 0) {
                characterName += character;
            }
        }
    }

    private char getCharFromKeycode(int keycode) {
        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        // Letters
        if (keycode >= Input.Keys.A && keycode <= Input.Keys.Z) {
            char base = (char) ('a' + (keycode - Input.Keys.A));
            return shift ? Character.toUpperCase(base) : base;
        }

        // Numbers
        if (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9) {
            return (char) ('0' + (keycode - Input.Keys.NUM_0));
        }

        // Space
        if (keycode == Input.Keys.SPACE) {
            return ' ';
        }

        return 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(0, 0, 0);
    }

    @Override
    public void dispose() {
        background.dispose();
        shapeRenderer.dispose();
        startGameButton.dispose();
        backButton.dispose();
    }
}
