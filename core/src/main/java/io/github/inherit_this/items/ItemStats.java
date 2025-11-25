package io.github.inherit_this.items;

/**
 * Stat bonuses that an item provides.
 * Used for weapons, armor, and enchanted items.
 */
public class ItemStats {
    private final int damage;           // Weapon damage
    private final int armor;            // Armor protection
    private final int durability;       // Item durability/health
    private final float attackSpeed;    // Attacks per second (e.g., 1.5 = fast, 0.8 = slow)
    private final int strength;         // Strength bonus
    private final int dexterity;        // Dexterity bonus
    private final int vitality;         // Vitality bonus
    private final int magic;            // Magic bonus

    public ItemStats(int damage, int armor, int durability, float attackSpeed, int strength, int dexterity, int vitality, int magic) {
        this.damage = damage;
        this.armor = armor;
        this.durability = durability;
        this.attackSpeed = attackSpeed;
        this.strength = strength;
        this.dexterity = dexterity;
        this.vitality = vitality;
        this.magic = magic;
    }

    // Factory method for no stats
    public static ItemStats none() {
        return new ItemStats(0, 0, 0, 0f, 0, 0, 0, 0);
    }

    // Factory method for weapon
    public static ItemStats weapon(int damage, int durability, float attackSpeed) {
        return new ItemStats(damage, 0, durability, attackSpeed, 0, 0, 0, 0);
    }

    // Factory method for weapon (backward compatibility - default attack speed 1.0)
    public static ItemStats weapon(int damage, int durability) {
        return weapon(damage, durability, 1.0f);
    }

    // Factory method for armor
    public static ItemStats armor(int armorValue, int durability) {
        return new ItemStats(0, armorValue, durability, 0f, 0, 0, 0, 0);
    }

    // Getters
    public int getDamage() { return damage; }
    public int getArmor() { return armor; }
    public int getDurability() { return durability; }
    public float getAttackSpeed() { return attackSpeed; }
    public int getStrength() { return strength; }
    public int getDexterity() { return dexterity; }
    public int getVitality() { return vitality; }
    public int getMagic() { return magic; }

    public boolean hasStats() {
        return damage > 0 || armor > 0 || strength > 0 || dexterity > 0 || vitality > 0 || magic > 0;
    }
}
