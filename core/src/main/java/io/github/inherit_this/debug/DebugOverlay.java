// TODO: try and implement this more lol

// package io.github.inherit_this.debug;

// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.Input;
// import com.badlogic.gdx.graphics.Camera;
// import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.graphics.g2d.BitmapFont;
// import com.badlogic.gdx.graphics.g2d.SpriteBatch;
// import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

// import io.github.inherit_this.world.World;
// import io.github.inherit_this.world.Chunk;
// import io.github.inherit_this.world.Tile;

// /**
//  * Renders debug overlays toggled via F-keys.
//  */
// public class DebugOverlay {
//     public boolean showTileBounds = false;
//     public boolean showChunkBounds = false;
//     public boolean showActiveChunk = false;
//     public boolean showTileOrientation = false;
//     public boolean showGenerationStats = false;

//     private final ShapeRenderer shape = new ShapeRenderer();
//     private final BitmapFont font = new BitmapFont();
//     private final SpriteBatch batch = new SpriteBatch();

//     public void update() {
//         if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) showTileBounds = !showTileBounds;
//         if (Gdx.input.isKeyJustPressed(Input.Keys.F2)) showChunkBounds = !showChunkBounds;
//         if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) showActiveChunk = !showActiveChunk;
//         if (Gdx.input.isKeyJustPressed(Input.Keys.F4)) showTileOrientation = !showTileOrientation;
//         if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) showGenerationStats = !showGenerationStats;
//     }

//     public void render(World world, Camera camera) {
//         shape.setProjectionMatrix(camera.combined);

//         if (showTileBounds) drawTileBounds(world);
//         if (showChunkBounds) drawChunkBounds(world);
//         if (showActiveChunk) drawActiveChunk(world);
//         if (showTileOrientation) drawTileOrientations(world, camera);

//         if (showGenerationStats) drawStats(world);
//     }

//     private void drawTileBounds(World world) {
//         shape.begin(ShapeRenderer.ShapeType.Line);
//         shape.setColor(Color.YELLOW);

//         for (Tile tile : world.getVisibleTiles()) {
//             float x = tile.getX() * Tile.TILE_SIZE;
//             float y = tile.getY() * Tile.TILE_SIZE;
//             shape.rect(x, y, Tile.TILE_SIZE, Tile.TILE_SIZE);
//         }

//         shape.end();
//     }

//     private void drawChunkBounds(World world) {
//         shape.begin(ShapeRenderer.ShapeType.Line);
//         shape.setColor(Color.CYAN);

//         for (Chunk chunk : world.getLoadedChunks()) {
//             float x = chunk.getChunkX() * (world.getTileSize() * world.getChunkSize());
//             float y = chunk.getChunkY() * (world.getTileSize() * world.getChunkSize());
//             float size = world.getTileSize() * world.getChunkSize();

//             shape.rect(x, y, size, size);
//         }

//         shape.end();
//     }

//     private void drawActiveChunk(World world) {
//         Chunk active = world.getActiveChunk();
//         if (active == null) return;

//         float x = active.getChunkX() * (world.getTileSize() * world.getChunkSize());
//         float y = active.getChunkY() * (world.getTileSize() * world.getChunkSize());
//         float size = world.getTileSize() * world.getChunkSize();

//         shape.begin(ShapeRenderer.ShapeType.Filled);
//         shape.setColor(0f, 0.7f, 0f, 0.25f);
//         shape.rect(x, y, size, size);
//         shape.end();
//     }

//     private void drawTileOrientations(World world, Camera camera) {
//         shape.begin(ShapeRenderer.ShapeType.Line);
//         shape.setColor(Color.RED);

//         for (Tile tile : world.getVisibleTiles()) {
//             float cx = tile.getX() * world.getTileSize() + world.getTileSize() / 2f;
//             float cy = tile.getY() * world.getTileSize() + world.getTileSize() / 2f;

//             float len = world.getTileSize() * 0.4f;
//             float dx = 0, dy = 0;

//             int orientation = tile.getOrientation(); // 0..3
//             switch (orientation) {
//                 case 0: dx = 0; dy = len; break;   // Up
//                 case 1: dx = len; dy = 0; break;   // Right
//                 case 2: dx = 0; dy = -len; break;  // Down
//                 case 3: dx = -len; dy = 0; break;  // Left
//             }

//             shape.line(cx, cy, cx + dx, cy + dy);
//         }

//         shape.end();
//     }

//     private void drawStats(World world) {
//         batch.begin();
//         font.draw(batch, "Chunk Gen Time: " + world.getLastGenTimeMs() + " ms", 10, Gdx.graphics.getHeight() - 10);
//         font.draw(batch, "Chunks Loaded: " + world.getLoadedChunks().size(), 10, Gdx.graphics.getHeight() - 30);
//         batch.end();
//     }
// }
