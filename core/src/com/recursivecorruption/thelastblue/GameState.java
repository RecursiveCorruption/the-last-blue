package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.recursivecorruption.thelastblue.graphics.GameMenu;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

public enum GameState {

    PLAY {
        @Override
        public GameState update(World world, GameMenu menu) {
            return world.update(this);
        }

        @Override
        public void onEnter(World world, SoundManager manager, GameMenu menu) {
            super.onEnter(world, manager, menu);
            world.reset(true);
        }

        @Override
        public void render(World world, Renderer renderer, GameMenu menu) {
            world.render(renderer);
            if (Gdx.input.isTouched())
                renderer.square(new Color(0.4f, 0.4f, 0.8f, 0.2f), InputProcessor.getInit(), 30f);
            int currentScore = world.getScore() + (int) Math.pow(Enemy.getMaxRad() - 15.0, 2.0);
            renderer.printCentered((int) (0.8f * Graphics.getSY()), Integer.toString(currentScore));
        }
    },
    MENU {
        @Override
        public GameState update(World world, GameMenu menu) {
            world.update(this);
            return menu.update();
        }

        @Override
        public void onEnter(World world, SoundManager manager, GameMenu menu)
        {
            super.onEnter(world, manager, menu);
            menu.reset();
        }

        @Override
        public void render(World world, Renderer renderer, GameMenu menu) {
            world.render(renderer);
            menu.draw(renderer);
            int score = world.getScore();
            int margin = Graphics.getMarginPx();
            int printX = margin/2;
            int printY = Graphics.getSY() - margin/2;
            if (score>=0)
                renderer.printRightOf(printX, printY, "Score: "+Integer.toString(score), true);
            printX = Graphics.getSX() - printX;
            if (world.getScore() > World.getHighScore())
                renderer.printLeftOf(printX, printY, "New High Score! Old:" + World.getHighScore(), true);
            else
                renderer.printLeftOf(printX, printY, "High Score:" + World.getHighScore(), true);
        }
    };

    public abstract GameState update(World world, GameMenu menu);

    public void onEnter(World world, SoundManager manager, GameMenu menu) {
        manager.onNewState(this);
    }

    public abstract void render(World world, Renderer renderer, GameMenu menu);
}