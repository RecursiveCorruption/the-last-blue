package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;

public class Particle extends Entity{
    private final float FRICTION = 2f;
    private final int FULL_LIFE = 200;
    private int life;
    public static final float PARTICLE_SIZE = 5f;
    private final boolean noFade;

    public Particle(float x, float y, float vx, float vy, Color color, float radius, List<Entity> entities, boolean noFade)
    {
        super(color, radius, x,y,vx,vy, entities);
        life = FULL_LIFE;
        this.noFade = noFade;
    }

    public Particle(float x, float y, float vx, float vy, Color color, float radius, List<Entity> entities) {
        this(x, y, vx, vy, color, radius, entities, false);
    }

    @Override
    public List<Entity> update()
    {
        color.a = (float)life/(float)FULL_LIFE;
        pos.add(vel.cpy().scl(Gdx.graphics.getDeltaTime()));
        List<Entity> list = new ArrayList<Entity>();
        if (noFade) {
            if (pos.x> Graphics.getSX())
                vel.x = -Math.abs(vel.x);
            else if (pos.y< 0)
                vel.y = Math.abs(vel.y);
            if (pos.y> Graphics.getSY())
                vel.y = -Math.abs(vel.y);
            else if (pos.y< 0)
                vel.y = Math.abs(vel.y);
        }
        else if (--life<0)
            list.add(this);
        return list;
    }

    @Override
    public List<Entity> createParticles() { return new ArrayList<Entity>();}

    @Override
    public List<Entity> createParticles(boolean noFade) {return new ArrayList<Entity>();}
}
