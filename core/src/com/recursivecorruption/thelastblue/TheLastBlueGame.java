package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TheLastBlueGame implements ApplicationListener
{
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera cam;
    private List<Enemy> enemies;
    private List<Particle> particles;
    private BitmapFont font;
    private Random rand;
    private Player player;
    private int maxRad;
    private State state = State.BEGIN;

    private Renderer renderer;

    private enum State
    {
        BEGIN,
        PLAY
    }

    @Override
	public void create()
	{
        cam = new OrthographicCamera(Graphics.getSX(), Graphics.getSY());
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        rand = new Random();
        renderer = new Renderer(cam);
        reset();
	}

	@Override
	public void resize(int width, int height)
	{
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());
        renderer.resize(cam);
        shapeRenderer.setProjectionMatrix(cam.combined);
	}

    private void reset()
    {
        player = new Player(Graphics.getSX()/2f, Graphics.getSY()/2f);
        enemies = new ArrayList<Enemy>();
        particles = new ArrayList<Particle>();
    }

    private void updateParticles()
    {
        boolean repeat = true;
        do {
            List<Particle> remove = new ArrayList<Particle>();
            for (Particle i : particles) {
                if (i.update())
                    remove.add(i);
            }
            particles.removeAll(remove);
            if(remove.size()<=0)
                repeat = false;
        } while(repeat);
    }

    public void update()
    {
        if (state == State.BEGIN)
        {
            updateParticles();
            if (Gdx.input.justTouched()) {
                state = State.PLAY;
                reset();
            }
            return;
        }

        if (enemies.size()<50 && rand.nextInt(2+((100*1000)/(1000+player.score+(int)Math.pow((double)maxRad,2f))))==1) {
            int width = rand.nextInt(Graphics.getSX());
            int height = rand.nextInt(Graphics.getSY());
            if (rand.nextInt(2)==1)
                width =  Graphics.getSX()*rand.nextInt(2);
            else
                height =  Graphics.getSY()*rand.nextInt(2);
            enemies.add(new Enemy(width,height));
        }
        boolean restart;

        do {
            restart = false;
            maxRad = 15;
            List<Enemy> remove = new ArrayList<Enemy>();
            for (Enemy i : enemies) {
                maxRad = Math.max(maxRad,(int)i.radius);
                if (i.update(player.getPos(), enemies, particles))
                {
                    if (i.radius>Enemy.EXPLODE_SIZE) {
                        player.score += Math.pow(i.radius,2);
                        remove.add(i);
                        i.createParticles(particles);
                    }
                    restart = true;
                    break;
                }
            }
            enemies.removeAll(remove);
        } while(restart);
        updateParticles();
        if(player.update(enemies)) {
            player.createParticles(particles, true);
            for (Enemy i:enemies)
                i.createParticles(particles, true);
            state = State.BEGIN;
        }
    }

	@Override
	public void render()
    {
        update();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(0f, 0.2f, 0.3f, 1f);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        cam.update();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle i:particles)
            i.draw(shapeRenderer);
        if (state==State.PLAY) {
            for (Enemy i:enemies)
                i.draw(shapeRenderer);
            player.draw(shapeRenderer);
        }
        shapeRenderer.end();
        renderer.begin();
        renderer.printCentered((int)(0.8f * Graphics.getSY()),Integer.toString(player.score+(int)Math.pow((double)(maxRad-15f),2f)));
        if (state!=State.PLAY)
        {
            renderer.printCentered((int)(0.4f * Graphics.getSY()),"Avoid the blue boxes");
            renderer.printCentered((int)(0.6f * Graphics.getSY()),"Tap to begin");
        }
        renderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	@Override
	public void pause()
	{

	}

	@Override
	public void resume()
	{

	}

	@Override
	public void dispose()
	{
	}
}