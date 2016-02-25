package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player extends Entity{
    public final static float FRICTION = 2f, TOUCH_MULTIPLY = 4.0f;
    public int score = 0;
    private Random rand;

    public void createParticles(List<Particle> particles)
    {
        createParticles(particles,false);
    }

    public void createParticles(List<Particle> particles, boolean noFade)
    {
        for (float x = pos.x; x <= pos.x+ radius;x+=Particle.PARTICLE_SIZE)
            for (float y = pos.y; y <= pos.y+ radius;y+=Particle.PARTICLE_SIZE)
                particles.add(new Particle(x,y,Gdx.graphics.getDeltaTime()*vel.x*0.3f+(rand.nextFloat()-0.5f), Gdx.graphics.getDeltaTime()*vel.y*0.3f+(rand.nextFloat()-0.5f), color, Particle.PARTICLE_SIZE, noFade));
    }

    public Player(float x, float y)
    {
        super(new Color(1f,0.1f,0.1f,1f), 30,x,y,0,0);
        rand = new Random();
    }

    private static float accel(float velo)
    {
        return 30f;//100f*(1f-Math.max(0f,Math.min(1f,Math.max(0,Math.abs(velo)-500)/1500)));
    }

    @Override
    public Entity update(List<Entity> entities)
    {
        float cap = 4000f* Graphics.getScaleConstant(), accel = 40f, mult = 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            mult = 0;
            accel = 500;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            vel.y = Math.min(cap, vel.y * mult + accel);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            vel.y = Math.max(-cap, vel.y * mult - accel);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            vel.x = Math.min(cap, vel.x * mult + accel);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            vel.x = Math.max(-cap,vel.x*mult - accel);
        else if (Gdx.input.isTouched())
            vel.set(InputProcessor.getDelta().scl(TOUCH_MULTIPLY));
        else if (!vel.epsilonEquals(0f,0f,0.0001f))
        {
            float xS = vel.x<0?-1:1;
            float yS = vel.y<0?-1:1;
            vel.sub(xS*FRICTION,yS*FRICTION);
            if (xS!=(vel.x<0?-1:1))
                vel.x = 0f;
            if (yS!=(vel.y<0?-1:1))
                vel.y = 0f;
        }
        pos.add(vel.x*Gdx.graphics.getDeltaTime(), vel.y*Gdx.graphics.getDeltaTime());
        float oldX = pos.x, oldY = pos.y;
        pos.x = Math.max(Math.min(Graphics.getSX()- radius,pos.x),0);
        pos.y = Math.max(Math.min(Graphics.getSY()- radius,pos.y),0);
        if (oldX != pos.x)
            vel.x = 0;
        if (oldY != pos.y)
            vel.y = 0;
        for (Entity i:entities)
            if (i instanceof Enemy && collides(i))
                return this;
        return null;
    }

}