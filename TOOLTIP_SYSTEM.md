# Tooltip System Implementation

## Status: Complete

The tooltip system has been fully integrated into all UI components.

## Features

### Tooltip Display
- **Item Name**: Colored by rarity (white, green, blue, purple, orange)
- **Base Damage**: Shows for weapons only
- **Attack Speed**: Shows for weapons with attack speed > 0
- **Value**: Only shown when `showValue = true` (for shop interfaces)

### Integration Points

1. **InventoryUI** - Shows tooltip when hovering over items
   - Only displays when not dragging an item
   - Automatically positions to avoid screen edges

2. **HotbarUI** - Shows tooltip for hotbar slots (F1-F5)
   - Displays when hovering over hotbar slots
   - Works with both assigned and unassigned slots

3. **EquipmentUI** - Shows tooltip for equipped items
   - Displays when hovering over equipment slots
   - Shows stats for equipped items only

## Attack Speed System

### ItemStats Enhancement
- Added `attackSpeed` field (float) to ItemStats
- Default attack speed: 1.0 (normal speed)
- Fast weapons: 1.5+
- Slow weapons: 0.8 or less

### Factory Methods
```java
// Weapon with custom attack speed
ItemStats.weapon(damage, durability, attackSpeed);

// Weapon with default attack speed (1.0)
ItemStats.weapon(damage, durability);
```

### Equipment Stat Calculation
- Calculates average attack speed from all equipped weapons
- Combines all other stats additively

## Usage Examples

### Creating a Fast Weapon
```java
new Item(
    "dagger",
    "Steel Dagger",
    "A quick striking blade",
    ItemType.WEAPON,
    ItemRarity.COMMON,
    texture,
    1, 2,
    1,
    150,
    0.8f,  // weight
    ItemStats.weapon(15, 100, 1.8f),  // damage, durability, attack speed
    null
);
```

### Creating a Slow Weapon
```java
new Item(
    "warhammer",
    "Iron Warhammer",
    "A massive crushing weapon",
    ItemType.WEAPON,
    ItemRarity.UNCOMMON,
    texture,
    2, 2,
    1,
    300,
    5.0f,  // weight
    ItemStats.weapon(50, 150, 0.6f),  // damage, durability, attack speed
    null
);
```

### Shop Interface Tooltip
```java
// In shop UI, pass true to show value
tooltip.render(batch, item, mouseX, mouseY, true);

// In inventory/equipment, value is hidden by default
tooltip.render(batch, item, mouseX, mouseY);
```

## Technical Details

### Positioning Logic
- Tooltips appear 15px to the right of the cursor
- If tooltip would go off-screen right, it appears to the left
- If tooltip would go off-screen top, it moves down

### Rendering Pipeline
1. Calculate all text lines and colors
2. Measure maximum line width
3. Calculate tooltip dimensions
4. Adjust position to stay on screen
5. Draw background and border (ShapeRenderer)
6. Draw text lines (SpriteBatch)

### Performance
- Tooltips only render when hovering over items
- No performance impact when not hovering
- Shared ItemTooltip instance per UI component

## Testing

Use debug commands to test:
```
give iron_sword 1
give steel_axe 1
```

Then:
1. Open inventory with 'I' key
2. Hover over items to see tooltips
3. Items in hotbar will show tooltips when hovered
4. Equipped items show tooltips when hovered

## Future Enhancements

When shop system is implemented:
- Pass `showValue = true` to tooltip.render()
- Can add buy/sell prices
- Can add "Owned: X" counter

When more item types are added:
- Armor values will auto-display
- Durability will show for damaged items
- Enchantments will appear in cyan
- Description text can be added
