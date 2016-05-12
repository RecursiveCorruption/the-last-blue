package com.recursivecorruption.thelastblue.graphics;

import com.badlogic.gdx.Gdx;

public class Graphics {
    private static final float SCREEN_SCALE = 1.5f;
    private static float scaleConstant;

    public static float getScaleConstant() {
        return scaleConstant;
    }

    public static void updateScaleConstant() {
        scaleConstant = SCREEN_SCALE / ((Gdx.graphics.getDensity() + (Gdx.graphics.getHeight() / 630f)) / 2f);
    }

    public static int getInputX() {
        return (int) (scaleConstant * Gdx.input.getX());
    }

    public static int getInputY() {
        return (int) (scaleConstant * Gdx.input.getY());
    }

    public static int getSX() {
        return (int) (scaleConstant * Gdx.graphics.getWidth());
    }

    public static int getSY() {
        return (int) (getScaleConstant() * Gdx.graphics.getHeight());
    }
}
