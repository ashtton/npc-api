package me.gleeming.npc.nms;

public interface InjectorListener {
    /**
     * Called whenever an entity is interacted with
     * @param entityId Entity ID
     */
    void interacted(int entityId);
}
