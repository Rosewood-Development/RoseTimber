package dev.rosewood.rosetimber.manager;

import dev.rosewood.rosetimber.RoseTimber;

public abstract class Manager {

    protected RoseTimber roseTimber;

    Manager(RoseTimber roseTimber) {
        this.roseTimber = roseTimber;
    }

    /**
     * Reloads the Manager's settings
     */
    public abstract void reload();

    /**
     * Cleans up the Manager's resources
     */
    public abstract void disable();

}
