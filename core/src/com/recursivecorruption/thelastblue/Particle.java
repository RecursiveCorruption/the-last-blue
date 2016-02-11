package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

public class Particle {
    private final float FRICTION = 2f;
    private float x, y,vx,vy, radius;
    private final int FULL_LIFE = 200;
    private int life;
    private Color color;
    public static final float PARTICLE_SIZE = 5f;
    private boolean noFade;

    public Particle(float x, float y, float vx, float vy, Color color, float radius, boolean noFade)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
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
        renderer.square(color, new Vector2(x, y), radius);
    }

    boolean update()
    {
        x += vx;
        y += vy;
        if (noFade) {
            if (x> Graphics.getSX())
                vx = -Math.abs(vx);
            else if (x< 0)
                vx = Math.abs(vx);
            if (y> Graphics.getSY())
                vy = -Math.abs(vy);
            else if (y< 0)
                vy = Math.abs(vy);
            return false;
        }
        else
            return --life<0;
    }
}
