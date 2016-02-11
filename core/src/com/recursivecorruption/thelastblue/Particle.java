package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

public class Particle {
    private final float FRICTION = 2f;
    private Vector2 pos, vel;
    private float radius;
    private final int FULL_LIFE = 200;
    private int life;
    private final Color color;
    public static final float PARTICLE_SIZE = 5f;
    private final boolean noFade;

    public Particle(float x, float y, float vx, float vy, Color color, float radius, boolean noFade)
    {
        pos = new Vector2(x,y);
        vel = new Vector2(vx,vy);
        this.color = new Color(color);
        this.radius = radius;
        life = FULL_LIFE;
        this.noFade = noFade;
    }

    public Particle(float x, float y, float vx, float vy, Color color, float radius) {
        this(x, y, vx, vy, color, radius, false);
    }
    public void draw(Renderer renderer)
    {
        color.a = (float)life/(float)FULL_LIFE;
        renderer.square(color, pos, radius);
    }

    boolean update()
    {
        pos.add(vel);
        if (noFade) {
            if (pos.x> Graphics.getSX())
                vel.x = -Math.abs(vel.x);
            else if (pos.y< 0)
                vel.y = Math.abs(vel.y);
            if (pos.y> Graphics.getSY())
                vel.y = -Math.abs(vel.y);
            else if (pos.y< 0)
                vel.y = Math.abs(vel.y);
            return false;
        }
        else
            return --life<0;
    }
}
