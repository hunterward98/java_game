#!/usr/bin/env python3
"""
Generate placeholder item textures.
Run this once to create basic colored squares for testing.
"""

try:
    from PIL import Image, ImageDraw

    def create_item_icon(filename, color, size=32):
        """Create a simple colored square with a border."""
        img = Image.new('RGBA', (size, size), (0, 0, 0, 0))
        draw = ImageDraw.Draw(img)

        # Draw colored rectangle with black border
        draw.rectangle([1, 1, size-2, size-2], fill=color, outline=(0, 0, 0, 255), width=2)

        # Add inner highlight
        draw.rectangle([4, 4, size-5, size-5], outline=tuple(min(c+30, 255) for c in color[:3]) + (255,), width=1)

        img.save(f'assets/items/{filename}')
        print(f'Created {filename}')

    # Create item icons with different colors
    items = {
        'iron_sword.png': (169, 169, 169, 255),      # Gray
        'steel_axe.png': (192, 192, 192, 255),       # Light gray
        'leather_helmet.png': (139, 90, 43, 255),    # Brown
        'iron_chestplate.png': (169, 169, 169, 255), # Gray
        'health_potion.png': (220, 20, 60, 255),     # Red
        'bread.png': (210, 180, 140, 255),           # Tan
        'iron_ore.png': (105, 105, 105, 255),        # Dark gray
        'wood.png': (139, 90, 43, 255),              # Brown
        'gold_ore.png': (255, 215, 0, 255),          # Gold
        'pickaxe.png': (112, 128, 144, 255),         # Slate gray
        'fishing_rod.png': (139, 90, 43, 255),       # Brown
    }

    for filename, color in items.items():
        create_item_icon(filename, color)

    print(f'\nSuccessfully generated {len(items)} placeholder item textures!')

except ImportError:
    print('Error: PIL/Pillow not installed. Install with: pip install Pillow')
    print('Alternatively, create 32x32 PNG files manually in assets/items/')
