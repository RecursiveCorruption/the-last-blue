package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TheLastBlueGame implements ApplicationListener {
    private OrthographicCamera cam;
    private List<Entity> entities;
    private Random rand;
    private Player player;
    private int maxRad;
    private static State state = State.BEGIN;
    private Renderer renderer;
    private int numEnemies;
    private static int score = 0;
    private Music playMusic, beginMusic;
    private int highScore = 0;
    Preferences prefs;

    public static void addScore(int amount)
    {
        if (state==State.PLAY)
            score += amount;
    }

    private static void increaseVolume(Music music)
    {
        if (music.getVolume() < 0.999f) {
            if (!music.isPlaying())
                music.play();
            music.setVolume(music.getVolume() + 0.01f);
        }
    }

    private static void decreaseVolume(Music music)
    {
        if (music.getVolume() > 0.001f)
            music.setVolume(music.getVolume() - 0.01f);
        else if (music.isPlaying())
            music.stop();
    }

    private enum State {
        BEGIN,
        PLAY
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
        highScore = prefs.getInteger("highScore",0);
        reset(false);
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());
        renderer.resize(width, height);
    }

    private void reset()
    {
        reset(true);
    }
    private void reset(boolean addPlayer) {
        entities = new ArrayList<Entity>();
        player = new Player(Graphics.getSX() / 2f, Graphics.getSY() / 2f);
        if (addPlayer)
            entities.add(player);
        if (score>highScore)
            highScore = score;
        score = 0;
    }

    public void update() {
        InputProcessor.update();
        maxRad = 15;
        numEnemies = 0;
        boolean justDied = false;
        List<Entity> remove = new ArrayList<Entity>();
        List<Entity> create = new ArrayList<Entity>();
        for (Entity i : entities) {
            if (i instanceof Enemy)
                maxRad = Math.max(maxRad, (int) i.radius);
            Entity j = i.update(entities);
            if (j instanceof Enemy) {
                ++numEnemies;
            } else if (j instanceof Player) {
                state = State.BEGIN;
                justDied = true;
            }
            if (j!=null) {
                create.addAll(j.createParticles());
                remove.add(j);
            }
        }
        entities.removeAll(remove);
        entities.addAll(create);
        if (justDied)
            score += (int) Math.pow((double) (maxRad - 15f), 2f);

        if (numEnemies < 50 && rand.nextInt(2 + ((100 * 1000) / (1000 + score + (int) Math.pow((double) maxRad, 2f)))) == 1) {
            int width = rand.nextInt(Graphics.getSX());
            int height = rand.nextInt(Graphics.getSY());
            if (rand.nextInt(2) == 1)
                width = Graphics.getSX() * rand.nextInt(2);
            else
                height = Graphics.getSY() * rand.nextInt(2);
            entities.add(new Enemy(width, height, player));
        }

        if (state == State.BEGIN) {
            decreaseVolume(playMusic);
            increaseVolume(beginMusic);
            if (Gdx.input.justTouched()) {
                state = State.PLAY;
                reset();
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
        //if (Gdx.input.isTouched())
        //    renderer.circle(Color.GRAY, InputProcessor.getInit(), 20f);
        for (Entity i : entities)
            i.draw(renderer);
        renderer.generateSquares();
        renderer.render();
        renderer.beginText();
        renderer.printCentered((int) (0.8f * Graphics.getSY()), Integer.toString(score + (state == State.PLAY ? (int) Math.pow((double) (maxRad - 15f), 2f) : 0)));
        if (state != State.PLAY) {
            renderer.printCentered((int) (0.4f * Graphics.getSY()), "Avoid the blue boxes");
            renderer.printCentered((int) (0.6f * Graphics.getSY()), "Tap to begin");
            if (score>highScore)
            {
                renderer.printLeftOf((int)(0.9f*Graphics.getSX()), (int)(0.1f*Graphics.getSY()), "New High Score!", true);
                renderer.printLeftOf((int) (0.9f * Graphics.getSX()), (int) (0.2f * Graphics.getSY()), "Old:" + highScore, true);
            }
            else
                renderer.printLeftOf((int) (0.9f * Graphics.getSX()), (int) (0.1f * Graphics.getSY()), "High Score:" + highScore, true);
        }
        renderer.endText();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        prefs.putInteger("highScore", Math.max(score,highScore));
        prefs.flush();
        renderer.dispose();
        playMusic.dispose();
    }
}