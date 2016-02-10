package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

public class Player {
    private Vector2 pos, vel, touch;
    public final static float FRICTION = 2f, RADIUS = 30f, TOUCH_MULTIPLY = 4.0f;
    public static final Color COLOR = Color.FIREBRICK;
    public int score = 0;
    private Random rand;

    public Vector2 getPos()
    {
        return pos;
    }

    public void createParticles(List<Particle> particles)
    {
        createParticles(particles,false);
    }

    public void createParticles(List<Particle> particles, boolean noFade)
    {
        for (float x = pos.x; x <= pos.x+RADIUS;x+=Particle.PARTICLE_SIZE)
            for (float y = pos.y; y <= pos.y+RADIUS;y+=Particle.PARTICLE_SIZE)
                particles.add(new Particle(x,y,Gdx.graphics.getDeltaTime()*vel.x*0.3f+(rand.nextFloat()-0.5f), Gdx.graphics.getDeltaTime()*vel.y*0.3f+(rand.nextFloat()-0.5f), COLOR, Particle.PARTICLE_SIZE, noFade));
    }

    private boolean collides(Enemy enemy)
    {
        return enemy.collides(pos,RADIUS);
    }

    public Player(float x, float y)
    {
        pos = new Vector2(x, y);
        vel = new Vector2(0f,0f);
        touch = new Vector2(TheLastBlueGame.getX(), TheLastBlueGame.getY());
        rand = new Random();
    }

    public void draw(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.setColor(COLOR);
        shapeRenderer.rect(pos.x,pos.y,RADIUS,RADIUS);
        if (Gdx.input.isTouched()) {
            shapeRenderer.setColor(Color.GRAY);
            shapeRenderer.circle(touch.x,touch.y, 20f);
        }
    }
    private static float accel(float velo)
    {
        return 30f;//100f*(1f-Math.max(0f,Math.min(1f,Math.max(0,Math.abs(velo)-500)/1500)));
    }

    public boolean update(List<Enemy> enemies)
    {
        float cap = 4000f* TheLastBlueGame.getScaleConstant(), accel = 40f, mult = 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            mult = 0;
            accel = 500;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            vel.y = Math.min(cap, vel.y*mult + accel);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            vel.y = Math.max(-cap, vel.y*mult - accel);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            vel.x = Math.min(cap,vel.x*mult+accel);
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            vel.x = Math.max(-cap,vel.x*mult - accel);
        if (Gdx.input.justTouched())
            touch.set(TheLastBlueGame.getX(), TheLastBlueGame.getY());
        else if (Gdx.input.isTouched())
        {
            Vector2 delta = new Vector2(TheLastBlueGame.getX(), TheLastBlueGame.getY());
            delta.sub(touch);
            delta.scl(TOUCH_MULTIPLY);
            vel.set(delta);
        }
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
        pos.x = Math.max(Math.min(TheLastBlueGame.getSX()-RADIUS,pos.x),0);
        pos.y = Math.max(Math.min(TheLastBlueGame.getSY()-RADIUS,pos.y),0);
        if (oldX != pos.x)
            vel.x = 0;
        if (oldY != pos.y)
            vel.y = 0;
        for (Enemy i:enemies)
            if (collides(i))
                return true;
        return false;
    }

}