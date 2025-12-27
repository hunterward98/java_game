package io.github.inherit_this.debug;

import io.github.inherit_this.combat.DamageInfo;
import io.github.inherit_this.entities.Player;
import io.github.inherit_this.items.WeaponEffect;
import io.github.inherit_this.particles.ParticleSystem;

import java.util.EnumSet;

/**
 * Debug command to drain or restore player stats with particles.
 * Positive values drain (reduce), negative values restore (increase).
 * Useful for testing combat particle effects and future features like poison.
 */
public class DrainCommand implements DebugCommand {
    private final Player player;
    private final ParticleSystem particleSystem;

    public DrainCommand(Player player, ParticleSystem particleSystem) {
        this.player = player;
        this.particleSystem = particleSystem;
    }

    @Override
    public String getName() {
        return "drain";
    }

    @Override
    public String getDescription() {
        return "Drain/restore player stats: drain <health|mana|stamina|all> <amount>";
    }

    @Override
    public void execute(String[] args, DebugConsole console) {
        if (args.length < 2) {
            console.log("Usage: drain <health|mana|stamina|all> <amount>");
            console.log("Use positive numbers to drain, negative to restore");
            console.log("Examples:");
            console.log("  drain health 20 - Drain 20 health (blood particles)");
            console.log("  drain health -20 - Restore 20 health");
            console.log("  drain mana 15 - Drain 15 mana (mana particles)");
            console.log("  drain stamina -10 - Restore 10 stamina");
            console.log("  drain all 5 - Drain 5 from all stats (all particles)");
            return;
        }

        String statType = args[0].toLowerCase();
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            console.log("Invalid amount: " + args[1]);
            return;
        }

        if (amount == 0) {
            console.log("Amount cannot be zero");
            return;
        }

        // Delegate to specific drain methods (position is calculated in Player.takeDamage)
        switch (statType) {
            case "health":
                drainHealth(amount, console);
                break;

            case "mana":
                drainMana(amount, console);
                break;

            case "stamina":
                drainStamina(amount, console);
                break;

            case "all":
                drainAll(amount, console);
                break;

            default:
                console.log("Invalid stat type: " + statType);
                console.log("Valid types: health, mana, stamina, all");
                break;
        }
    }

    private void drainHealth(int amount, DebugConsole console) {
        if (amount > 0) {
            // Drain health with particles (position calculated in Player.takeDamage)
            DamageInfo damageInfo = DamageInfo.attack(amount);
            player.takeDamage(damageInfo, particleSystem);
            console.log("Drained " + amount + " health");
        } else {
            // Restore health (no particles)
            player.getStats().heal(-amount);
            console.log("Restored " + (-amount) + " health");
        }

        console.log("Current: " + (int)player.getStats().getCurrentHealth() + "/" +
                   (int)player.getStats().getMaxHealth());
    }

    private void drainMana(int amount, DebugConsole console) {
        if (amount > 0) {
            // Drain mana with particles (position calculated in Player.takeDamage)
            DamageInfo damageInfo = DamageInfo.attackWithEffects(
                0, // No health damage
                io.github.inherit_this.items.ItemStats.weaponWithEffects(
                    0, 0, 0f,
                    EnumSet.of(WeaponEffect.MANA_DRAIN),
                    amount, 0f, 0f
                )
            );
            player.takeDamage(damageInfo, particleSystem);
            console.log("Drained " + amount + " mana");
        } else {
            // Restore mana (no particles)
            player.getStats().restoreMana(-amount);
            console.log("Restored " + (-amount) + " mana");
        }

        console.log("Current: " + (int)player.getStats().getCurrentMana() + "/" +
                   (int)player.getStats().getMaxMana());
    }

    private void drainStamina(int amount, DebugConsole console) {
        if (amount > 0) {
            // Drain stamina with particles (position calculated in Player.takeDamage)
            DamageInfo damageInfo = DamageInfo.attackWithEffects(
                0, // No health damage
                io.github.inherit_this.items.ItemStats.weaponWithEffects(
                    0, 0, 0f,
                    EnumSet.of(WeaponEffect.STAMINA_DRAIN),
                    0f, amount, 0f
                )
            );
            player.takeDamage(damageInfo, particleSystem);
            console.log("Drained " + amount + " stamina");
        } else {
            // Restore stamina (no particles)
            player.getStats().restoreStamina(-amount);
            console.log("Restored " + (-amount) + " stamina");
        }

        console.log("Current: " + (int)player.getStats().getCurrentStamina() + "/" +
                   (int)player.getStats().getMaxStamina());
    }

    private void drainAll(int amount, DebugConsole console) {
        if (amount > 0) {
            // Drain all stats with particles (position calculated in Player.takeDamage)
            DamageInfo damageInfo = DamageInfo.attackWithEffects(
                amount, // Health damage
                io.github.inherit_this.items.ItemStats.weaponWithEffects(
                    amount, 0, 0f,
                    EnumSet.of(WeaponEffect.MANA_DRAIN, WeaponEffect.STAMINA_DRAIN),
                    amount, amount, 0f
                )
            );
            player.takeDamage(damageInfo, particleSystem);
            console.log("Drained " + amount + " from all stats");
        } else {
            // Restore all stats (no particles)
            player.getStats().heal(-amount);
            player.getStats().restoreMana(-amount);
            player.getStats().restoreStamina(-amount);
            console.log("Restored " + (-amount) + " to all stats");
        }

        console.log("Health: " + (int)player.getStats().getCurrentHealth() + "/" +
                   (int)player.getStats().getMaxHealth());
        console.log("Mana: " + (int)player.getStats().getCurrentMana() + "/" +
                   (int)player.getStats().getMaxMana());
        console.log("Stamina: " + (int)player.getStats().getCurrentStamina() + "/" +
                   (int)player.getStats().getMaxStamina());
    }
}
