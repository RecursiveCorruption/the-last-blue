package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.recursivecorruption.thelastblue.graphics.Graphics;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private Player player;
    private List<Entity> entities;
    private int score = 0;
    private static int highScore;

    public static void init(Preferences prefs)
    {
        highScore = prefs.getInteger("highScore", 0);
    }

    public static void pause(Preferences prefs)
    {
        prefs.putInteger("highScore", highScore);
    }

    public static int getHighScore()
    {
        return highScore;
    }

    private void reset() {
        reset(true);
    }

    private void reset(boolean addPlayer) {
        entities = new ArrayList<Entity>();
        player = new Player(Graphics.getSX() / 2f, Graphics.getSY() / 2f);
        if (addPlayer)
            entities.add(player);
        if (score > highScore)
            highScore = score;
        score = 0;
    }

    public TheLastBlueGame.State update(Random rand, TheLastBlueGame.State state) {
        Enemy.refresh();
        List<Entity> remove = new ArrayList<Entity>();
        List<Entity> create = new ArrayList<Entity>();
        for (Entity i : entities) {
            Entity j = i.update(entities);
            if (j instanceof Player) {
                score += (int) Math.pow((double) (Enemy.getMaxRad() - 15f), 2f);
                return TheLastBlueGame.State.BEGIN;
            }
            if (j != null) {
                create.addAll(j.createParticles());
                remove.add(j);
            }
        }
        entities.removeAll(remove);
        entities.addAll(create);

        if (Enemy.getCount() < 50 && rand.nextInt(2 + ((100 * 1000) / (1000 + score + (int) Math.pow((double) Enemy.getMaxRad(), 2f)))) == 1) {
            int width = rand.nextInt(Graphics.getSX());
            int height = rand.nextInt(Graphics.getSY());
            if (rand.nextInt(2) == 1)
                width = Graphics.getSX() * rand.nextInt(2);
            else
                height = Graphics.getSY() * rand.nextInt(2);
            entities.add(new Enemy(width, height, player));
        }
        return state;
    }

    public void render(Renderer renderer)
    {
        for (Entity i : entities)
            i.draw(renderer);
    }
}
