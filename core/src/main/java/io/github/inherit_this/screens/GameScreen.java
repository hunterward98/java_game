package io.github.inherit_this.screens;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Input;
import io.github.inherit_this.Main;
import io.github.inherit_this.world.World;
import io.github.inherit_this.world.Chunk;
import io.github.inherit_this.world.Tile;
import io.github.inherit_this.entities.*;

public class GameScreen extends ScreenAdapter {

    private final Main game;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Texture playerTex;
    private Player player;
    private float playerSpeed = 200f;
    private World world = new World();

    private static final int TILE_SIZE = 32;
    private static final int CHUNK_SIZE = 8;
    private static final int CHUNK_PIXEL_SIZE = TILE_SIZE * CHUNK_SIZE;

    public GameScreen(Main game) {
        this.game = game;
        this.batch = game.getBatch();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false);

        playerTex = new Texture("character.png");
        player = new Player(0, 0, playerTex, game); // TODO: load position from save state
    }

    @Override
    public void render(float delta) {
        handleMovement(delta);
        handlePause();

        camera.position.set(player.getPosition().x, player.getPosition().y, 0);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderVisibleChunks();
        player.renderPlayer();

        batch.end();
    }

    private void handleMovement(float delta) {
        float move = playerSpeed * delta;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) player.getPosition().y += move;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) player.getPosition().y -= move;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player.getPosition().x -= move;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player.getPosition().x += move;
    }

    private void handlePause() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
        }
    }

    private void renderVisibleChunks() {
        float left = camera.position.x - camera.viewportWidth / 2f;
        float right = camera.position.x + camera.viewportWidth / 2f;
        float bottom = camera.position.y - camera.viewportHeight / 2f;
        float top = camera.position.y + camera.viewportHeight / 2f;

        int chunkXStart = (int) Math.floor(left / CHUNK_PIXEL_SIZE);
        int chunkXEnd   = (int) Math.floor(right / CHUNK_PIXEL_SIZE);
        int chunkYStart = (int) Math.floor(bottom / CHUNK_PIXEL_SIZE);
        int chunkYEnd   = (int) Math.floor(top / CHUNK_PIXEL_SIZE);

        for (int cx = chunkXStart; cx <= chunkXEnd; cx++) {
            for (int cy = chunkYStart; cy <= chunkYEnd; cy++) {
                renderChunk(cx, cy);
            }
        }
    }

    private static final float EDGE_BLEND_SIZE = 4f;

    private void renderChunk(int cx, int cy) {
        Chunk chunk = world.getOrCreateChunk(cx, cy);
        float baseX = cx * CHUNK_PIXEL_SIZE;
        float baseY = cy * CHUNK_PIXEL_SIZE;

        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                Tile tile = chunk.getTile(x, y);
                float tileScreenX = baseX + x * TILE_SIZE;
                float tileScreenY = baseY + y * TILE_SIZE;

                batch.enableBlending();

                batch.draw(tile.getTexture(), tileScreenX, tileScreenY, TILE_SIZE, TILE_SIZE);

                int worldTileX = cx * CHUNK_SIZE + x;
                int worldTileY = cy * CHUNK_SIZE + y;

                Tile leftNeighbor = world.getTileAtWorldCoords(worldTileX - 1, worldTileY);
                if (leftNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        leftNeighbor.getTexture(),
                        tileScreenX, tileScreenY,
                        EDGE_BLEND_SIZE, TILE_SIZE,
                        TILE_SIZE - (int) EDGE_BLEND_SIZE, 0,
                        (int) EDGE_BLEND_SIZE, TILE_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile rightNeighbor = world.getTileAtWorldCoords(worldTileX + 1, worldTileY);
                if (rightNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        rightNeighbor.getTexture(),
                        tileScreenX + TILE_SIZE - EDGE_BLEND_SIZE, tileScreenY,
                        EDGE_BLEND_SIZE, TILE_SIZE,
                        0, 0,
                        (int) EDGE_BLEND_SIZE, TILE_SIZE,
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
                        TILE_SIZE, EDGE_BLEND_SIZE,
                        0, TILE_SIZE - (int) EDGE_BLEND_SIZE,
                        TILE_SIZE, (int) EDGE_BLEND_SIZE,
                        false, false
                    );
                    batch.setColor(1, 1, 1, 1f);
                }

                Tile topNeighbor = world.getTileAtWorldCoords(worldTileX, worldTileY + 1);
                if (topNeighbor != null) {
                    batch.setColor(1, 1, 1, 0.5f);
                    batch.draw(
                        topNeighbor.getTexture(),
                        tileScreenX, tileScreenY + TILE_SIZE - EDGE_BLEND_SIZE,
                        TILE_SIZE, EDGE_BLEND_SIZE,
                        0, 0,
                        TILE_SIZE, (int) EDGE_BLEND_SIZE,
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
