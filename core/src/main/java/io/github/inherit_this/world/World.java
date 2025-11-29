package io.github.inherit_this.world;

/**
 * Legacy World class maintained for backward compatibility.
 * New code should use WorldProvider interface with ProceduralWorld or StaticWorld implementations.
 *
 * @deprecated Use {@link ProceduralWorld} for procedurally generated worlds
 * or {@link StaticWorld} for static/hand-crafted worlds
 */
@Deprecated
public class World extends ProceduralWorld {

    public World() {
        super();
    }
}
