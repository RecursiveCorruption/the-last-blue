package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.recursivecorruption.thelastblue.graphics.Graphics;

import java.util.ArrayList;
import java.util.List;

public class Particle extends Entity {
    public static final float PARTICLE_SIZE = 5f;
    private final int FULL_LIFE = 200;
    private final boolean noFade;
    private int life;

    public Particle(float x, float y, float vx, float vy, Color color, float radius, boolean noFade) {
        super(color, radius, x, y, vx, vy);
        life = FULL_LIFE;
        this.noFade = noFade;
    }

    @Override
    public Entity update(World world, GameState state) {
        color.a = (float) life / (float) FULL_LIFE;
        pos.add(vel.cpy().scl(Gdx.graphics.getDeltaTime()));
        if (noFade) {
            if (pos.x > Graphics.getSX())
                vel.x = -Math.abs(vel.x);
            else if (pos.y < 0)
                vel.y = Math.abs(vel.y);
            if (pos.y > Graphics.getSY())
                vel.y = -Math.abs(vel.y);
            else if (pos.y < 0)
                vel.y = Math.abs(vel.y);
        } else if (--life < 0)
            return this;
        return null;
    }

    @Override
    public List<Entity> createParticles() {
        return new ArrayList<Entity>();
    }

    @Override
    public List<Entity> createParticles(boolean noFade) {
        return new ArrayList<Entity>();
    }
}
