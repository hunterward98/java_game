# Assets Needed

## Item Placeholder
- `assets/items/placeholder.png` - 32x32 magenta square (fallback for all missing assets)

## Item Assets
All items should match their grid size:
- 1x1 items = 32x32 pixels
- 1x2 items = 32x64 pixels
- 2x1 items = 64x32 pixels
- 2x2 items = 64x64 pixels

### Weapons (1x2 = 32x64)
- `assets/items/iron_sword.png`
- `assets/items/steel_axe.png`

### Armor (1x1 = 32x32)
- `assets/items/leather_helmet.png`
- `assets/items/iron_chestplate.png`

### Consumables (1x1 = 32x32)
- `assets/items/health_potion.png`
- `assets/items/bread.png`

### Materials (1x1 = 32x32)
- `assets/items/iron_ore.png`
- `assets/items/wood.png`
- `assets/items/gold_ore.png`

### Tools (1x2 = 32x64)
- `assets/items/pickaxe.png`
- `assets/items/fishing_rod.png`

## Equipment Slot Placeholders
All equipment placeholders should be 32x32 pixels and clearly show what slot they represent.

- `assets/equipment/main_hand_placeholder.png`
- `assets/equipment/necklace_placeholder.png`
- `assets/equipment/ring_1_placeholder.png`
- `assets/equipment/ring_2_placeholder.png`
- `assets/equipment/torso_placeholder.png`
- `assets/equipment/legs_placeholder.png`
- `assets/equipment/boots_placeholder.png`
- `assets/equipment/helmet_placeholder.png`
- `assets/equipment/shield_placeholder.png`
- `assets/equipment/cape_backpack_placeholder.png`
- `assets/equipment/gloves_placeholder.png`

## UI Assets

### Hotbar Background (Optional)
- `assets/ui/hotbar_background.png` - Background panel for the hotbar UI
- **Dimensions**: Flexible, but recommended ~600x100 pixels
- **Layout**: Should accommodate:
  - 5 item slots (48x48 each with 4px padding)
  - 4 stat bars (200px wide x 12px tall each)
  - Gold display area (~100px wide)
- **Style**: Semi-transparent panel with decorative border
- **Note**: If this file doesn't exist, the game will render a simple gray panel using shapes

## Notes
- If an item asset doesn't match its expected size, the game will use placeholder.png instead
- Equipment placeholders will fall back to items/placeholder.png if not found
- All textures use nearest neighbor filtering for pixel-perfect rendering
- Hotbar background is optional - a fallback gray panel will be rendered if not provided
