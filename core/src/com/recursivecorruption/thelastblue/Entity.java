package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.List;
import java.util.Random;

public abstract class Entity {
    protected Vector2 pos, vel;
    protected Color color;
    protected float radius;
    protected Random rand;
    protected List<Entity> entities;

    protected Entity(Color color, float radius, float x, float y, float vx, float vy, List<Entity> entities) {
        this.color = new Color(color);
        this.radius = radius;
        pos = new Vector2(x, y);
        vel = new Vector2(vx, vy);
        rand = new Random();
        this.entities = entities;
    }

    public boolean collides(Entity other) {
        return pos.x+radius>=other.pos.x&&pos.x<=other.pos.x+other.radius&&pos.y+radius>=other.pos.y&&pos.y<=other.pos.y+other.radius;
    }

    public void draw(Renderer renderer) {
        renderer.square(color, pos, radius);
    }

    public void createParticles()
    {
        createParticles(false);
    }

    public void createParticles(boolean noFade)
    {
        for (float x = pos.x; x <= pos.x+radius;x+= Particle.PARTICLE_SIZE)
            for (float y = pos.y; y <= pos.y+radius;y+= Particle.PARTICLE_SIZE)
                entities.add(new Particle(x,y,vel.x*0.3f+(rand.nextFloat()-0.5f), vel.y*0.3f+(rand.nextFloat()-0.5f), color, Particle.PARTICLE_SIZE, entities, noFade));
    }

    //Returns the entities to remove
    public abstract List<Entity> update();
}
