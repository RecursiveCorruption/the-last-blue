package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    private BitmapFont font;
    private Random rand;
    private Player player;
    private int maxRad;
    private State state = State.BEGIN;
    private Renderer renderer;
    private int numEnemies;

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
        reset();
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());
        renderer.resize(cam);
    }

    private void reset() {
        entities = new ArrayList<Entity>();
        player = new Player(Graphics.getSX() / 2f, Graphics.getSY() / 2f, entities);
        entities.add(player);
    }

    public void update() {
        InputProcessor.update();
        if (state == State.BEGIN) {
            if (Gdx.input.justTouched()) {
                state = State.PLAY;
                reset();
            }
            return;
        }

        maxRad = 15;
        numEnemies = 0;
        List<Entity> remove = new ArrayList<Entity>();
        for (Entity i : entities) {
            maxRad = Math.max(maxRad, (int) i.radius);
            List<Entity> toBeRemoved = i.update();
            for (Entity j : toBeRemoved) {
                if (j instanceof Enemy) {
                    ++numEnemies;
                    if (j.radius > Enemy.EXPLODE_SIZE)
                        player.score += Math.pow(j.radius, 2);
                } else if (j instanceof Player) {
                    for (Entity k : entities)
                        i.createParticles(true);
                    state = State.BEGIN;
                    break;
                }
                j.createParticles();
            }
            remove.addAll(toBeRemoved);
        }
        entities.removeAll(remove);

        if (numEnemies < 50 && rand.nextInt(2 + ((100 * 1000) / (1000 + player.score + (int) Math.pow((double) maxRad, 2f)))) == 1) {
            int width = rand.nextInt(Graphics.getSX());
            int height = rand.nextInt(Graphics.getSY());
            if (rand.nextInt(2) == 1)
                width = Graphics.getSX() * rand.nextInt(2);
            else
                height = Graphics.getSY() * rand.nextInt(2);
            entities.add(new Enemy(width, height, player, entities));
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
        renderer.begin(true);
        if (Gdx.input.isTouched())
            renderer.circle(Color.GRAY, InputProcessor.getInit(), 20f);
        for (Entity i : entities)
            i.draw(renderer);
        renderer.end();
        renderer.begin(false);
        renderer.printCentered((int) (0.8f * Graphics.getSY()), Integer.toString(player.score + (int) Math.pow((double) (maxRad - 15f), 2f)));
        if (state != State.PLAY) {
            renderer.printCentered((int) (0.4f * Graphics.getSY()), "Avoid the blue boxes");
            renderer.printCentered((int) (0.6f * Graphics.getSY()), "Tap to begin");
        }
        renderer.end();
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
    }
}