package io.github.inherit_this.items;

/**
 * Represents the different equipment slots where items can be worn.
 */
public enum EquipmentSlot {
    MAIN_HAND("Main Hand"),
    NECKLACE("Necklace"),
    RING_1("Ring 1"),
    RING_2("Ring 2"),
    TORSO("Torso"),
    LEGS("Legs"),
    BOOTS("Boots"),
    HELMET("Helmet"),
    SHIELD("Shield"),
    CAPE_BACKPACK("Cape/Backpack"),
    GLOVES("Gloves");

    private final String displayName;

    EquipmentSlot(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
