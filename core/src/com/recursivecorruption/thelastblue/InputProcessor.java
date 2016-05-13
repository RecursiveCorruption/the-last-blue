package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Graphics;

public final class InputProcessor {
    private static Vector2 pos = new Vector2(0, 0);
    static class CustomKeys {

        public static final int NUM_KEYS = 4;
        public static final int DOWN = 0, UP = 1, RIGHT = 2, LEFT = 3, NONE = 4;
        private static boolean[] pressed = new boolean[4];

        private static final int[] ARROW_KEYS = {Input.Keys.DOWN, Input.Keys.UP, Input.Keys.RIGHT, Input.Keys.LEFT};

        static void update()
        {
            for (int i = 0; i < ARROW_KEYS.length; ++i)
                pressed[i] = Gdx.input.isKeyPressed(ARROW_KEYS[i]);
        }

        public static boolean[] getPressed()
        {
            return pressed;
        }
    }

    private InputProcessor() {
    }

    //Called once per frame somewhere else
    public static void update() {
        CustomKeys.update();
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
