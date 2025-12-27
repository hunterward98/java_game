package io.github.inherit_this.items;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    // Weapon effects
    private final Set<WeaponEffect> weaponEffects;  // Special weapon effects (mana drain, life steal, etc.)
    private final float manaDrainAmount;            // Mana to drain per hit
    private final float staminaDrainAmount;         // Stamina to drain per hit
    private final float lifeStealPercent;           // Percentage of damage returned as health (0.0-1.0)

    public ItemStats(int damage, int armor, int durability, float attackSpeed, int strength, int dexterity, int vitality, int magic) {
        this(damage, armor, durability, attackSpeed, strength, dexterity, vitality, magic,
             Collections.emptySet(), 0f, 0f, 0f);
    }

    public ItemStats(int damage, int armor, int durability, float attackSpeed, int strength, int dexterity, int vitality, int magic,
                     Set<WeaponEffect> weaponEffects, float manaDrainAmount, float staminaDrainAmount, float lifeStealPercent) {
        this.damage = damage;
        this.armor = armor;
        this.durability = durability;
        this.attackSpeed = attackSpeed;
        this.strength = strength;
        this.dexterity = dexterity;
        this.vitality = vitality;
        this.magic = magic;
        this.weaponEffects = new HashSet<>(weaponEffects);
        this.manaDrainAmount = manaDrainAmount;
        this.staminaDrainAmount = staminaDrainAmount;
        this.lifeStealPercent = lifeStealPercent;
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

    // Factory method for weapon with effects
    public static ItemStats weaponWithEffects(int damage, int durability, float attackSpeed,
                                               Set<WeaponEffect> effects, float manaDrain, float staminaDrain, float lifeSteal) {
        return new ItemStats(damage, 0, durability, attackSpeed, 0, 0, 0, 0,
                           effects, manaDrain, staminaDrain, lifeSteal);
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

    // Weapon effect getters
    public Set<WeaponEffect> getWeaponEffects() {
        return Collections.unmodifiableSet(weaponEffects);
    }
    public float getManaDrainAmount() { return manaDrainAmount; }
    public float getStaminaDrainAmount() { return staminaDrainAmount; }
    public float getLifeStealPercent() { return lifeStealPercent; }

    public boolean hasWeaponEffects() {
        return !weaponEffects.isEmpty();
    }

    public boolean hasStats() {
        return damage > 0 || armor > 0 || strength > 0 || dexterity > 0 || vitality > 0 || magic > 0;
    }
}
