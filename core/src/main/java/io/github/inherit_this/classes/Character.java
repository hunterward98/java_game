package io.github.inherit_this.classes;
import com.badlogic.gdx.math.Vector2;

public class Character extends Entity {
    // draining characteristics
    protected float health;
    protected float max_health;
    protected float stamina;
    protected float max_stamina;
    
    // damage characteristics
    // statuses

    // movement and display characteristics
    protected float accel_rate;
    protected float decel_rate;
    protected int direction_faced; // 0 (right), 1 (bottom), 2 (left), 3 (top)
    protected Vector2 velocity;

    public Character(String texturePath, float x, float y) {
        super(texturePath, x, y);
        this.health = 20;
        this.max_health = 20;
        this.stamina = 1000;
        this.max_stamina = 1000;

        this.accel_rate = 1;
        this.decel_rate = 1;
        this.direction_faced = 1;
        this.velocity = new Vector2(0,0);
    }

    public void move(Vector2 delta) {
        position.add(delta);
    }
}
