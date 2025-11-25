# Expansion Ideas for Inheritance

## Priority 1: Core Gameplay Systems

### Combat System
**Description**: Implement basic combat mechanics
- **Enemies**: Create enemy entities (slimes, goblins, skeletons)
  - AI pathfinding using A* algorithm
  - Different enemy types with unique behaviors
  - Health bars above enemies
  - Death animations and loot drops
- **Combat Mechanics**:
  - Left-click to attack with equipped weapon
  - Attack cooldowns based on weapon speed
  - Damage calculation: weapon damage + player stats
  - Hit detection and damage numbers
  - Knockback effects
- **Player Combat Stats**:
  - Attack speed, critical hit chance, dodge chance
  - Damage types (physical, magic, fire, ice, poison)
  - Resistances and armor reduction

### Loot System
**Description**: Random item drops from enemies and chests
- **Loot Tables**: Define drop rates per enemy type
- **Rarity System**: Already have ItemRarity, use it for drop chances
- **Procedural Items**: Generate items with random stats
  - Base item + random stat modifiers
  - Enchantments with varying strengths
  - Item level scaling with player/enemy level
- **Chests**: Placeable in world, contain randomized loot
- **Boss Loot**: Guaranteed rare/epic items

### Crafting System
**Description**: Combine materials to create items
- **Crafting Stations**: Forge, workbench, enchanting table
- **Recipes**: Define ingredient requirements
  - Example: iron_sword = 2x iron_ore + 1x wood
- **Skill Levels**: Crafting skills that improve with use
- **Enchanting**: Add enchantments to existing items
- **Repair System**: Use materials to repair durability

## Priority 2: World Content

### Dungeons & Structures
**Description**: Procedurally generated dungeons
- **Room-based Generation**:
  - Entrance, hallways, treasure rooms, boss rooms
  - Different themes (cave, crypt, castle)
- **Traps**: Spikes, arrows, fire traps
- **Puzzles**: Lever puzzles, key-door systems
- **Boss Rooms**: Unique powerful enemies with mechanics
- **Mini-map**: Show explored dungeon layout

### Biomes & Environments
**Description**: Expand beyond grass and stone
- **New Biomes**:
  - Forest (trees as obstacles)
  - Desert (cacti, sand storms)
  - Snow/Tundra (ice tiles)
  - Swamp (poison water, slow movement)
  - Volcanic (lava, fire damage)
- **Weather System**: Rain, snow, fog (visual effects)
- **Day/Night Cycle**: Affects enemy spawns and visibility
- **Environmental Hazards**: Lava damage, poison zones

### NPCs & Towns
**Description**: Friendly non-combat entities
- **Villages**: Procedurally placed NPC settlements
- **Vendors**:
  - General merchant (buy/sell items)
  - Weapon smith, armor smith
  - Potion vendor, enchanter
- **Quest Givers**: NPCs that provide objectives
- **Dialogue System**: Simple text-based conversations
- **Reputation**: Town reputation affects prices

## Priority 3: Character Progression

### Skill Trees
**Description**: Unlock abilities as you level up
- **Skill Points**: Earned per level
- **Trees**:
  - Warrior (melee damage, tank abilities)
  - Rogue (critical hits, dodge, speed)
  - Mage (magic damage, spells)
  - Survivalist (crafting, gathering bonuses)
- **Active Abilities**: Skills usable with F1-F5 hotbar
- **Passive Abilities**: Permanent stat bonuses

### Class System
**Description**: Choose character archetype
- **Starting Classes**: Warrior, Rogue, Mage, Ranger
- **Class-specific Equipment**: Some items restricted by class
- **Class Abilities**: Unique skills per class
- **Multi-classing**: Unlock secondary class at level 50

### Quest System
**Description**: Objectives for XP and rewards
- **Quest Types**:
  - Kill quests (defeat X enemies)
  - Fetch quests (collect X items)
  - Delivery quests (bring item to NPC)
  - Exploration (discover location)
- **Quest Log UI**: Track active quests
- **Quest Rewards**: XP, gold, items
- **Story Quests**: Main questline

## Priority 4: UI/UX Improvements

### Tooltips
**Description**: Detailed item information on hover
- Show in inventory and hotbar
- Display: stats, enchantments, weight, value, rarity
- Compare equipped item vs inventory item
- Color-coded by rarity

### Mini-map
**Description**: Small map in corner of screen
- Show player position
- Show explored areas
- Icons for NPCs, enemies, chests
- Toggle with 'M' key

### Character Sheet
**Description**: Detailed stat screen
- All player stats (HP, MP, Stamina, damage, armor, etc.)
- Equipment bonuses breakdown
- Total weight/capacity
- Skills and abilities
- Open with 'C' key

### Settings Menu
**Description**: Game configuration
- Key bindings customization
- Audio volume sliders
- Graphics settings (resolution, fullscreen)
- Gameplay options (auto-loot, damage numbers)

## Priority 5: Advanced Features

### Pet/Companion System
**Description**: AI-controlled helpers
- Pets that follow and attack
- Different pet types (wolf, cat, bird)
- Pet inventory for items
- Pet leveling and stats

### Housing System
**Description**: Player-owned homes
- Purchase house in towns
- Place furniture and decorations
- Storage chests (expanded inventory)
- Fast travel to home

### Achievements
**Description**: Track player milestones
- First kill, reach level 10, etc.
- Craft legendary item

### Save/Load System
**Description**: Persistent game state
- Save player stats, inventory, position
- Save world state (killed enemies, opened chests)
- Multiple save slots
- Auto-save every 5 minutes


## Technical Improvements

### Performance Optimization
- Entity pooling (reuse enemy objects)
- Frustum culling (don't render off-screen)
- Chunk unloading for distant chunks
- Texture atlasing (combine textures)

### Code Quality
- Unit tests for core systems
- Refactor god classes
- Improve error handling
- Add logging system

### Tools
- Map editor for custom levels
- Item/enemy editor
- Debug overlay (F3) with:
  - FPS counter
  - Entity count
  - Memory usage
  - Player coordinates
  - Chunk info
