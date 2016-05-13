package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;

public class Player extends Entity {
    public final static float FRICTION = 0.99f;
    private final static float TOUCH_MULTIPLY = 4.0f;
    private static final float KEY_ACC = 40f;
    private static final Vector2[] KEY_SIGNS = {new Vector2(0f, 1f), new Vector2(0f, -1f), new Vector2(1f, 0f), new Vector2(-1f, 0f)};
    private static final Vector2[] KEY_ACCELS = {KEY_SIGNS[0].cpy().scl(KEY_ACC), KEY_SIGNS[1].cpy().scl(KEY_ACC), KEY_SIGNS[2].cpy().scl(KEY_ACC), KEY_SIGNS[3].cpy().scl(KEY_ACC)};
    private Vector2 acc;

    public Player(float x, float y) {
        super(new Color(1f, 0.1f, 0.1f, 1f), 30, x, y, 0, 0);
        acc = new Vector2();
    }

    private void handleInput() {
        acc.set(0, 0);
        boolean[] pressed = InputProcessor.CustomKeys.getPressed();
        for (int i = 0; i < InputProcessor.CustomKeys.NUM_KEYS; ++i)
            if (pressed[i])
                acc.add(KEY_ACCELS[i]);
        if (Gdx.input.isTouched())
            vel.set(InputProcessor.getDelta().scl(TOUCH_MULTIPLY));
    }

    @Override
    public Entity update(World world) {
        handleInput();
        vel.scl(FRICTION);
        vel.add(acc);
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