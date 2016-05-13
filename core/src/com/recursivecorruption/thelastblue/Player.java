package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.recursivecorruption.thelastblue.graphics.Graphics;

import java.util.List;
import java.util.Random;

public class Player extends Entity {
    public final static float FRICTION = 0.99f;
    private final static float TOUCH_MULTIPLY = 4.0f;

    public Player(float x, float y) {
        super(new Color(1f, 0.1f, 0.1f, 1f), 30, x, y, 0, 0);
    }

    @Override
    public Entity update(World world) {
        float accel = 40f, mult = 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            mult = 0;
            accel = 500;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
            vel.y = vel.y * mult + accel;
        if (Gdx.input.isKeyPressed(Input.Keys.UP))
            vel.y = vel.y * mult - accel;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            vel.x = vel.x * mult + accel;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
            vel.x = vel.x * mult - accel;
        else if (Gdx.input.isTouched())
            vel.set(InputProcessor.getDelta().scl(TOUCH_MULTIPLY));
        vel.scl(FRICTION);
        pos.add(vel.x * Gdx.graphics.getDeltaTime(), vel.y * Gdx.graphics.getDeltaTime());
        float oldX = pos.x, oldY = pos.y;
        pos.x = Math.max(Math.min(Graphics.getSX() - radius, pos.x), 0);
        pos.y = Math.max(Math.min(Graphics.getSY() - radius, pos.y), 0);
        if (oldX != pos.x)
            vel.x = 0;
        if (oldY != pos.y)
            vel.y = 0;
        for (Entity i : world.getEntities())
            if (i instanceof Enemy && collides(i))
                return this;
        return null;
    }

}