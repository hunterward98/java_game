package io.github.inherit_this.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Array;
import io.github.inherit_this.util.FontManager;

import java.util.Arrays;

/**
 * Toggleable debug console with input handling, command registry, and a scrollback log.
 */
public class DebugConsole implements InputProcessor {
    private boolean open = false;
    private final StringBuilder input = new StringBuilder();
    private final Array<String> log = new Array<>();
    private final ObjectMap<String, DebugCommand> commands = new ObjectMap<>();

    private final BitmapFont font;
    private final ShapeRenderer shape;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;

    // Visual config
    private final int height = 240;
    private final int edgeFade = 30;
    private final float backgroundAlpha = 0.82f;

    public DebugConsole() {
        font = FontManager.getInstance().getConsoleFont();
        shape = new ShapeRenderer();
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        updateCamera();

        log.add("Debug Console initialized. Type 'help' for commands.");
    }

    /** Update camera to match screen dimensions (call on resize). */
    public void updateCamera() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        camera.setToOrtho(false, w, h);
        camera.update();
    }

    public void toggle() {
        open = !open;
    }

    public boolean isOpen() {
        return open;
    }

    public void registerCommand(DebugCommand cmd) {
        if (cmd == null) return;
        commands.put(cmd.getName(), cmd);
    }

    public ObjectMap<String, DebugCommand> getCommands() {
        return commands;
    }

    public void log(String line) {
        log.add(line);
        // cap log size to avoid unbounded growth
        if (log.size > 400) log.removeRange(0, 100);
    }

    /**
     * Call each frame (if handling keyboard polling preferred). If using InputMultiplexer,
     * ensure this object is added as an InputProcessor so keyTyped/keyDown handlers below run.
     */
    public void update() {
        // Nothing needed here if using InputProcessor methods for input.
    }

    private void process(String line) {
        if (line == null) return;
        line = line.trim();
        if (line.isEmpty()) return;

        log("> " + line);

        String[] parts = line.split("\\s+");
        String name = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        DebugCommand cmd = commands.get(name);
        if (cmd == null) {
            log("Unknown command: " + name + " (type 'help')");
            return;
        }

        try {
            cmd.execute(args, this);
        } catch (Exception e) {
            log("Command error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void render() {
        // TODO: call in main render
        if (!open) return;

        int w = Gdx.graphics.getWidth();
        int h = height;

        // Use screen-space camera to prevent font stretching
        camera.update();
        shape.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.setColor(0f, 0f, 0f, backgroundAlpha);
        shape.rect(0, 0, w, h);
        shape.end();

        shape.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < edgeFade; i++) {
            float alpha = (1f - (i / (float) edgeFade)) * backgroundAlpha * 0.9f;
            shape.setColor(0f, 0f, 0f, alpha);
            shape.rect(0, h + i, w, 1);
        }
        shape.end();

        batch.begin();
        font.setColor(0f, 1f, 0.3f, 1f);

        float y = h - 20;
        int linesThatFit = Math.max(0, (h - 40) / 18);
        int start = Math.max(0, log.size - linesThatFit);
        for (int i = start; i < log.size; i++) {
            String entry = log.get(i);
            font.draw(batch, entry, 12, y);
            y -= 18;
        }

        String inputLine = "> " + input.toString();
        font.draw(batch, inputLine, 12, 18);

        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        if (!open) return false;

        if (keycode == Input.Keys.ENTER) {
            String toProcess = input.toString();
            input.setLength(0);
            process(toProcess);
            return true;
        }

        if (keycode == Input.Keys.BACKSPACE) {
            if (input.length() > 0) input.deleteCharAt(input.length() - 1);
            return true;
        }

        if (keycode == Input.Keys.ESCAPE) {
            toggle();
            return true;
        }

        if (keycode == Input.Keys.V && (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) || Gdx.input.isKeyPressed(Input.Keys.CONTROL_RIGHT))) {
            String clip = Gdx.app.getClipboard().getContents();
            if (clip != null) input.append(clip);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (!open) return false;

        if (character == '\r' || character == '\n') return false;
        if (character == '\b') return false;
        if (character == '`') return false; // Don't append the toggle key
        if (character >= 32 && character < 127) {
            input.append(character);
            return true;
        }
        return false;
    }

    @Override public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
