package io.github.inherit_this.items;

/**
 * Represents a stack of items with quantity.
 * Used in inventory slots to track both the item type and how many.
 */
public class ItemStack {
    private final Item item;
    private int quantity;

    public ItemStack(Item item, int quantity) {
        this.item = item;
        this.quantity = Math.min(quantity, item.getMaxStackSize());
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, Math.min(quantity, item.getMaxStackSize()));
    }

    public void addQuantity(int amount) {
        setQuantity(quantity + amount);
    }

    public void removeQuantity(int amount) {
        setQuantity(quantity - amount);
    }

    public boolean isFull() {
        return quantity >= item.getMaxStackSize();
    }

    public boolean isEmpty() {
        return quantity <= 0;
    }

    public int getRemainingCapacity() {
        return item.getMaxStackSize() - quantity;
    }

    /**
     * Tries to merge another stack into this one.
     * @return The amount that couldn't be merged (0 if fully merged)
     */
    public int merge(ItemStack other) {
        if (!item.getId().equals(other.item.getId())) {
            return other.quantity;
        }

        int canAdd = getRemainingCapacity();
        int toAdd = Math.min(canAdd, other.quantity);
        addQuantity(toAdd);
        other.removeQuantity(toAdd);
        return other.quantity;
    }

    public ItemStack copy() {
        return new ItemStack(item, quantity);
    }

    @Override
    public String toString() {
        return item.getName() + " x" + quantity;
    }
}
