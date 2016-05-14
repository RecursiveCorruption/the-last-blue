package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

public enum GameState {

    PLAY {
        @Override
        GameState update(World world) {
            return world.update(this);
        }

        @Override
        void render(World world, Renderer renderer) {
            world.render(renderer);
            if (Gdx.input.isTouched())
                renderer.square(new Color(0.4f, 0.4f, 0.8f, 0.2f), InputProcessor.getInit(), 30f);
            int currentScore = world.getScore()+(int) Math.pow(Enemy.getMaxRad() - 15.0, 2.0);
            renderer.printCentered((int) (0.8f * Graphics.getSY()), Integer.toString(currentScore));
        }
    },
    BEGIN {
        @Override
        GameState update(World world) {
            if (Gdx.input.justTouched()) {
                world.reset(true);
                return PLAY;
            }
            return world.update(this);
        }

        @Override
        void render(World world, Renderer renderer) {
            world.render(renderer);
            int score = world.getScore();
            renderer.printCentered((int) (0.8f * Graphics.getSY()), Integer.toString(score));
            renderer.printCentered((int) (0.4f * Graphics.getSY()), "Avoid the blue boxes");
            renderer.printCentered((int) (0.6f * Graphics.getSY()), "Tap to begin");
            int printX = (int) (0.9f * Graphics.getSX());
            int printY = (int) (0.1f * Graphics.getSX());
            if (world.getScore() > World.getHighScore())
                renderer.printLeftOf(printX, printY, "New High Score!\nOld:" + World.getHighScore(), true);
            else
                renderer.printLeftOf(printX, printY, "High Score:" + World.getHighScore(), true);
        }
    };

    abstract GameState update(World world);

    abstract void render(World world, Renderer renderer);
}