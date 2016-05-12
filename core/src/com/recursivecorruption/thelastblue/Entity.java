package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Entity {
    protected Vector2 pos, vel;
    protected Color color;
    protected float radius;
    protected Random rand;

    protected Entity(Color color, float radius, float x, float y, float vx, float vy) {
        this.color = new Color(color);
        this.radius = radius;
        pos = new Vector2(x, y);
        vel = new Vector2(vx, vy);
        rand = new Random();
    }

    public boolean collides(Entity other) {
        return pos.x + radius >= other.pos.x && pos.x <= other.pos.x + other.radius && pos.y + radius >= other.pos.y && pos.y <= other.pos.y + other.radius;
    }

    public void draw(Renderer renderer) {
        renderer.square(color, pos, radius);
    }

    public List<Entity> createParticles() {
        return createParticles(false);
    }

    public List<Entity> createParticles(boolean noFade) {
        List<Entity> particles = new ArrayList<Entity>();
        for (float x = pos.x; x <= pos.x + radius; x += Particle.PARTICLE_SIZE)
            for (float y = pos.y; y <= pos.y + radius; y += Particle.PARTICLE_SIZE)
                particles.add(new Particle(x, y, vel.x * 0.3f + 1.4f * radius * (rand.nextFloat() - 0.5f), vel.y * 0.3f + 1.4f * radius * (rand.nextFloat() - 0.5f), color, Particle.PARTICLE_SIZE, noFade));
        return particles;
    }

    //Returns the entities to remove
    public abstract Entity update(World world);
}
