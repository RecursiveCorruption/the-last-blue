package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;

public final class InputProcessor {
    private static Vector2 pos = new Vector2(0, 0);

    private InputProcessor() {
    }

    //Called once per frame somewhere else
    public static void update() {
        if (Gdx.input.justTouched())
            pos.set(Graphics.getInputX(), Graphics.getInputY());
        else if (!Gdx.input.isTouched())
            pos.set(0, 0);
    }

    public static Vector2 getDelta() {
        return pos.cpy().sub(Graphics.getInputX(), Graphics.getInputY()).scl(-1);
    }

    public static Vector2 getInit() {
        return pos;
    }
}
