# UI Improvements Summary

## Changes Made

### 1. Fixed UI Following Player Issue [DONE]

**Problem**: Inventory and Equipment UI were following the player around the map because they used the world camera.

**Solution**: Added screen-space OrthographicCamera to both InventoryUI and EquipmentUI, similar to HotbarUI.

**Files Modified**:
- [InventoryUI.java](core/src/main/java/io/github/inherit_this/ui/InventoryUI.java)
  - Added `OrthographicCamera camera` field
  - Added `updateCameraProjection()` and `updateCamera()` methods
  - Updated `render()` to use screen-space camera

- [EquipmentUI.java](core/src/main/java/io/github/inherit_this/ui/EquipmentUI.java)
  - Added `OrthographicCamera camera` field
  - Added `updateCameraProjection()` and `updateCamera()` methods
  - Updated `render()` to use screen-space camera

- [GameScreen.java](core/src/main/java/io/github/inherit_this/screens/GameScreen.java)
  - Updated positioning to use pixel coordinates instead of world coordinates
  - Added camera updates to `resize()` method
  - Inventory and Equipment now centered on screen properly

### 2. Centered Inventory and Equipment UI [DONE]

**Change**: Both UIs are now centered horizontally and vertically on the screen as a group.

**Location**: [GameScreen.java:124-138](core/src/main/java/io/github/inherit_this/screens/GameScreen.java#L124-L138)

```java
// Center both UIs on screen using pixel coordinates
int screenWidth = Gdx.graphics.getWidth();
int screenHeight = Gdx.graphics.getHeight();

float inventoryX = (screenWidth - totalWidth) / 2;
float inventoryY = (screenHeight - inventoryUI.getHeight()) / 2;
```

### 3. Created FontManager System [DONE]

**Purpose**: Centralized font management for consistent pixel font support across all UI components.

**New File**: [FontManager.java](core/src/main/java/io/github/inherit_this/util/FontManager.java)

**Features**:
- Singleton pattern for shared font instances
- Automatically looks for custom pixel font at `assets/fonts/pixel.fnt`
- Falls back to LibGDX default with pixel-perfect rendering
- Provides three font types:
  - `getUIFont()` - For inventory, equipment, hotbar
  - `getConsoleFont()` - For debug console
  - `getTooltipFont()` - For item tooltips (slightly smaller)

**All UI Components Updated**:
- DebugConsole.java
- HotbarUI.java
- InventoryUI.java
- EquipmentUI.java
- ItemTooltip.java

### 4. Added Pixel Font Support [DONE]

**Setup Guide**: [assets/fonts/README.md](assets/fonts/README.md)

**How to Add a Custom Pixel Font**:

1. Download a free pixel font (recommendations in README):
   - m5x7 (recommended - small, clean, readable)
   - Kenney Pixel
   - Press Start 2P

2. Convert `.ttf` to `.fnt` format using:
   - BMFont (Windows)
   - Hiero (Cross-platform)
   - Online tool: https://snowb.org/

3. Place files in `assets/fonts/`:
   - `pixel.fnt` (font descriptor)
   - `pixel.png` (font texture)

4. Game will automatically detect and use the font!

**Current Status**: Using LibGDX default font with pixel-perfect rendering (no filtering) until you add a custom font.

## Testing

Run the game and verify:
- [x] Inventory and equipment stay centered when player moves
- [x] UIs resize properly when window changes
- [x] Font appears pixel-perfect (no blurring/stretching)
- [x] Debug console font is pixel-perfect
- [x] All tooltips use consistent font

## Font Integration Checklist

If you want to add a custom pixel font:

- [ ] Download a pixel font (see recommendations above)
- [ ] Convert to `.fnt` format using BMFont/Hiero
- [ ] Place `pixel.fnt` and `pixel.png` in `assets/fonts/`
- [ ] Run game - font will be automatically loaded
- [ ] Adjust scale in `FontManager.java` if needed

## Technical Notes

### Screen-Space vs World-Space Rendering

All UI now uses **screen-space cameras** that render at pixel coordinates:
- World camera: Follows player, used for tiles and entities
- UI cameras: Fixed to screen, used for inventory/equipment/hotbar/console

### Coordinate Systems

- **World coordinates**: Centered at (0, 0), scaled by PIXEL_SCALE
- **Screen coordinates**: Origin at bottom-left, in pixels (e.g., 1920x1080)

### Font Rendering

All fonts now use:
```java
font.getRegion().getTexture().setFilter(
    Texture.TextureFilter.Nearest,
    Texture.TextureFilter.Nearest
);
```

This prevents blur/anti-aliasing for crisp pixel art.
