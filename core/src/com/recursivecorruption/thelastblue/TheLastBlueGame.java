package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
    private SpriteBatch batch;
    private State state = State.BEGIN;

    private static float SCREEN_SCALE = 1.5f;

    private enum State
    {
        BEGIN,
        PLAY
    }

    public static float getScaleConstant()
    {
        return SCREEN_SCALE/((Gdx.graphics.getDensity()+(Gdx.graphics.getHeight()/630f))/2f);
    }

    public static int getX()
    {
        return (int)(getScaleConstant()*Gdx.input.getX());
    }

    public static int getY()
    {
        return (int)(getScaleConstant()*Gdx.input.getY());
    }

    public static int getSX()
    {
        return (int)(getScaleConstant()*Gdx.graphics.getWidth());
    }

    public static int getSY()
    {
        return (int)(getScaleConstant()*Gdx.graphics.getHeight());
    }

    private void printCentered(int y, String message)
    {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);
        font.draw(batch,glyphLayout, getSX()/2f-glyphLayout.width / 2f, y - glyphLayout.height / 2f);
    }

	@Override
	public void create()
	{
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("MontereyFLF.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(70f/Gdx.graphics.getDensity());
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?: ";
        parameter.flip = true;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font = generator.generateFont(parameter);
        generator.dispose();
        cam = new OrthographicCamera(getSX(),getSY());
        cam.setToOrtho(true, getSX(), getSY());
        batch.setProjectionMatrix(cam.combined);

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        rand = new Random();
        reset();
	}

	@Override
	public void resize(int width, int height)
	{
        cam.setToOrtho(true, getSX(), getSY());
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer.setProjectionMatrix(cam.combined);
	}

    private void reset()
    {
        player = new Player(getSX()/2f,getSY()/2f);
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
            int width = rand.nextInt(getSX());
            int height = rand.nextInt(getSY());
            if (rand.nextInt(2)==1)
                width =  getSX()*rand.nextInt(2);
            else
                height =  getSY()*rand.nextInt(2);
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
        batch.begin();
        printCentered((int)(0.8f * getSY()),Integer.toString(player.score+(int)Math.pow((double)(maxRad-15f),2f)));
        if (state!=State.PLAY)
        {
            printCentered((int)(0.4f * getSY()),"Avoid the blue boxes");
            printCentered((int)(0.6f * getSY()),"Tap to begin");
        }
        batch.end();
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