package io.github.inherit_this.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Input;
import io.github.inherit_this.Main;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.entities.*;
import io.github.inherit_this.util.Constants;
import io.github.inherit_this.debug.*;

public class GameScreen extends ScreenAdapter {

    private final Main game;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture playerTex;
    private Player player;
    private World world = new World();
    private DebugConsole debugConsole;
    private InputMultiplexer inputMultiplexer;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        playerTex = new Texture("character.png");
        player = new Player(0, 0, playerTex, game, world); // TODO: load position from save state

        debugConsole = new DebugConsole();
        debugConsole.registerCommand(new HelpCommand(debugConsole.getCommands()));
        debugConsole.registerCommand(new NoClipCommand(player));
        debugConsole.registerCommand(new RegenWorldCommand(world));
        debugConsole.registerCommand(new ReloadChunkCommand(world, player));

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(debugConsole);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        handleInput();

        if (!debugConsole.isOpen()) {
            player.update(delta);
        }

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderVisibleChunks();
        player.renderPlayer();

        batch.end();

        debugConsole.render();
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.GRAVE)) {
            debugConsole.toggle();
        }

        if (!debugConsole.isOpen() && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
        }
    }

    private void renderVisibleChunks() {
        float left = camera.position.x - camera.viewportWidth / 2f;
        float right = camera.position.x + camera.viewportWidth / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float top = camera.position.y + camera.viewportHeight / 2f;

        int chunkXStart = (int) Math.floor(left / Constants.CHUNK_PIXEL_SIZE);
        int chunkXEnd   = (int) Math.floor(right / Constants.CHUNK_PIXEL_SIZE);
        int chunkYStart = (int) Math.floor(bottom / Constants.CHUNK_PIXEL_SIZE);
        int chunkYEnd   = (int) Math.floor(top / Constants.CHUNK_PIXEL_SIZE);

        for (int cx = chunkXStart; cx <= chunkXEnd; cx++) {
            for (int cy = chunkYStart; cy <= chunkYEnd; cy++) {
                renderChunk(cx, cy);
            }
        }
    }

    private void renderChunk(int cx, int cy) {
        Chunk chunk = world.getOrCreateChunk(cx, cy);
        float baseX = cx * Constants.CHUNK_PIXEL_SIZE;
        float baseY = cy * Constants.CHUNK_PIXEL_SIZE;

        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                Tile tile = chunk.getTile(x, y);
                float tileScreenX = baseX + x * Constants.TILE_SIZE;
                float tileScreenY = baseY + y * Constants.TILE_SIZE;

                batch.enableBlending();

                batch.draw(tile.getTexture(), tileScreenX, tileScreenY, Constants.TILE_SIZE, Constants.TILE_SIZE);

                int worldTileX = cx * Constants.CHUNK_SIZE + x;
                int worldTileY = cy * Constants.CHUNK_SIZE + y;

                Tile leftNeighbor = world.getTileAtWorldCoords(worldTileX - 1, worldTileY);
                if (leftNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        leftNeighbor.getTexture(),
                        tileScreenX, tileScreenY,
                        Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        Constants.TILE_SIZE - (int) Constants.EDGE_BLEND_SIZE, 0,
                        (int) Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile rightNeighbor = world.getTileAtWorldCoords(worldTileX + 1, worldTileY);
                if (rightNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        rightNeighbor.getTexture(),
                        tileScreenX + Constants.TILE_SIZE - Constants.EDGE_BLEND_SIZE, tileScreenY,
                        Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        0, 0,
                        (int) Constants.EDGE_BLEND_SIZE, Constants.TILE_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile bottomNeighbor = world.getTileAtWorldCoords(worldTileX, worldTileY - 1);
                if (bottomNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        bottomNeighbor.getTexture(),
                        tileScreenX, tileScreenY,
                        Constants.TILE_SIZE, Constants.EDGE_BLEND_SIZE,
                        0, Constants.TILE_SIZE - (int) Constants.EDGE_BLEND_SIZE,
                        Constants.TILE_SIZE, (int) Constants.EDGE_BLEND_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile topNeighbor = world.getTileAtWorldCoords(worldTileX, worldTileY + 1);
                if (topNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        topNeighbor.getTexture(),
                        tileScreenX, tileScreenY + Constants.TILE_SIZE - Constants.EDGE_BLEND_SIZE,
                        Constants.TILE_SIZE, Constants.EDGE_BLEND_SIZE,
                        0, 0,
                        Constants.TILE_SIZE, (int) Constants.EDGE_BLEND_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }
            }
        }
    }

    @Override
    public void dispose() {
        playerTex.dispose();
    }
}
