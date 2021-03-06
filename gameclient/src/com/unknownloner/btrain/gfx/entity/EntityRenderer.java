package com.unknownloner.btrain.gfx.entity;

import com.unknownloner.btrain.gfx.SpriteBatch;
import com.unknownloner.btrain.logic.Entity;
import com.unknownloner.btrain.logic.EntityType;

public abstract class EntityRenderer<T extends Entity> {

    /**
     * Renders the entity with a given offset
     * @param t Entity to render
     * @param x X offset
     * @param y Y offset
     */
    public abstract void render(SpriteBatch batch, T t, double x, double y);


    //Static components of this nonsense
    private static EntityRenderer[] renderers = new EntityRenderer[256];

    private static void add(EntityType t, EntityRenderer r) {
        renderers[t.ordinal()] = r;
    }

    public static void renderEntity(SpriteBatch batch, Entity e, double x, double y) {
        renderers[e.type().ordinal()].render(batch, e, x, y);
    }

    static {
        add(EntityType.PLAYER, new PlayerRenderer());
        add(EntityType.BULLET, new BulletRenderer());
        add(EntityType.PLAYER_BULLET, new PlayerBulletRenderer());
        add(EntityType.ENEMY, new EnemyRenderer());
    }



}
