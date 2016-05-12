package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TheLastBlueGame implements ApplicationListener {
    private static State state = State.BEGIN;

    Preferences prefs;
    private OrthographicCamera cam;
    private Random rand;
    private Renderer renderer;
    private Music playMusic, beginMusic;
    private World world;

    private static void increaseVolume(Music music) {
        if (music.getVolume() < 0.999f) {
            if (!music.isPlaying())
                music.play();
            music.setVolume(music.getVolume() + 0.01f);
        }
    }

    private static void decreaseVolume(Music music) {
        if (music.getVolume() > 0.001f)
            music.setVolume(music.getVolume() - 0.01f);
        else if (music.isPlaying())
            music.stop();
    }

    @Override
    public void create() {
        cam = new OrthographicCamera(Graphics.getSX(), Graphics.getSY());
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());
        rand = new Random();
        renderer = new Renderer(cam);
        playMusic = Gdx.audio.newMusic(Gdx.files.internal("Song.mp3"));
        playMusic.setLooping(true);
        playMusic.setVolume(0f);
        beginMusic = Gdx.audio.newMusic(Gdx.files.internal("SadBg.mp3"));
        beginMusic.setLooping(true);
        beginMusic.setVolume(0f);
        prefs = Gdx.app.getPreferences("Settings");
        World.init(prefs);
        world = new World();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(true, (int)(width*Graphics.getScaleConstant()), (int)(height*Graphics.getScaleConstant()));
        renderer.resize((int)(width*Graphics.getScaleConstant()), (int)(height*Graphics.getScaleConstant()), cam);
    }

    public void update() {
        InputProcessor.update();
        state = world.update(rand, state);

        if (state == State.BEGIN) {
            decreaseVolume(playMusic);
            increaseVolume(beginMusic);
            if (Gdx.input.justTouched()) {
                state = State.PLAY;
                world.reset(true);
            }
        } else {
            decreaseVolume(beginMusic);
            increaseVolume(playMusic);
        }
    }

    @Override
    public void render() {
        update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 1f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        cam.update();

        renderer.begin();
        world.render(renderer);
        if (Gdx.input.isTouched())
            renderer.square(new Color(0.4f, 0.4f, 0.8f, 0.2f), InputProcessor.getInit(), 30f);
        int score = world.getScore();
        renderer.printCentered((int) (0.8f * Graphics.getSY()), Integer.toString(score + (state == State.PLAY ? (int) Math.pow((double) (Enemy.getMaxRad() - 15f), 2f) : 0)));
        if (state != State.PLAY) {
            renderer.printCentered((int) (0.4f * Graphics.getSY()), "Avoid the blue boxes");
            renderer.printCentered((int) (0.6f * Graphics.getSY()), "Tap to begin");
            int printX = (int) (0.9f * Graphics.getSX());
            int printY = (int) (0.1f * Graphics.getSX());
            if (world.getScore() > World.getHighScore())
                renderer.printLeftOf(printX, printY, "New High Score!\nOld:" +  World.getHighScore(), true);
            else
                renderer.printLeftOf(printX, printY, "High Score:" +  World.getHighScore(), true);
        }
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void pause() {
        world.updateHighScore();
        World.pause(prefs);
        prefs.flush();
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        renderer.dispose();
        playMusic.dispose();
    }

    public enum State {
        BEGIN,
        PLAY
    }
}