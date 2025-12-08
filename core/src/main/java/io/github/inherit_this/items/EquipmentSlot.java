package io.github.inherit_this.items;

/**
 * Represents the different equipment slots where items can be worn.
 */
public enum EquipmentSlot {
    MAIN_HAND("Main Hand", 2, 4),
    NECKLACE("Necklace", 1, 1),
    RING_1("Ring 1", 1, 1),
    RING_2("Ring 2", 1, 1),
    TORSO("Torso", 2, 2),
    LEGS("Legs", 2, 2),
    BOOTS("Boots", 2, 2),
    HELMET("Helmet", 2, 2),
    SHIELD("Shield", 2, 3),
    CAPE_BACKPACK("Cape/Backpack", 2, 1),
    GLOVES("Gloves", 1, 1);

    private final String displayName;
    private final int slotWidth;
    private final int slotHeight;

    EquipmentSlot(String displayName, int slotWidth, int slotHeight) {
        this.displayName = displayName;
        this.slotWidth = slotWidth;
        this.slotHeight = slotHeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getSlotWidth() {
        return slotWidth;
    }

    public int getSlotHeight() {
        return slotHeight;
    }
}
