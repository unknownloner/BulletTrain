package com.unknownloner.btrain.logic;

import com.unknownloner.btrain.math.Vec2;

public class EntityPlayer extends EntityLiving {

    public Input input;
    public int shootCooldown = 7;

    public int health = 100;

    private int shootDelay;


    public EntityPlayer(Level level, Input input) {
        super(level);
        bounds.size.set(28.0, 24.0);
        this.input = input;
    }

    @Override
    public void tick() {
        if(health <= 0){
            isDead = true;
        }
        double vx = 0.0;
        double vy = 0.0;

        if (input.left()) {
            vx -= 1.0;
        }
        if (input.right()) {
            vx += 1.0;
        }
        if (input.up()) {
            vy += 1.0;
        }
        if (input.down()) {
            vy -= 1.0;
        }

        if (vx != 0 || vy != 0) {
            Vec2 vel = new Vec2(vx, vy);
            vel.normalize();
            vel.scale(3.0);
            if (vel.y < 0)
                vel.y *= 0.2;

            move(vel.x, vel.y);
        }

        if (shootDelay > 0)
            shootDelay--;
        if (input.shooting() && shootDelay == 0) {
            shootDelay = shootCooldown;
            fireBullet();
        }

        super.tick();
    }

    public void fireBullet() {
        EntityPlayerBullet left   = new EntityPlayerBullet(level, pos.x, pos.y + 8, 10.0, 105 * Math.PI / 180D);
        EntityPlayerBullet center = new EntityPlayerBullet(level, pos.x, pos.y + 8, 10.0,  90 * Math.PI / 180D);
        EntityPlayerBullet right  = new EntityPlayerBullet(level, pos.x, pos.y + 8, 10.0,  75 * Math.PI / 180D);
        level.spawnEntity(left);
        level.spawnEntity(center);
        level.spawnEntity(right);
    }

    @Override
    public EntityType type() {
        return EntityType.PLAYER;
    }

}
