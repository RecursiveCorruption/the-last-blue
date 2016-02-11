package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy
{
    private float speed;
    private Vector2 pos;
    public static float EXPLODE_SIZE = 65f;
    private static float EXPLODE_AREA = (float)Math.pow(EXPLODE_SIZE,2);
    public float radius = 15f;
    public Color color;
    private double angle = -200.25f;
    private Random rand;

    public Enemy(float x, float y)
    {
        pos = new Vector2(x,y);
        color = Color.BLUE;
        recalcColor();
        recalcSpeed();
        rand = new Random();
    }

    public void createParticles(List<Particle> particles)
    {
        createParticles(particles,false);
    }

    public void createParticles(List<Particle> particles, boolean noFade)
    {
        float vx = (float)(speed *Math.cos(angle));
        float vy = (float)(speed *Math.sin(angle));

        for (float x = pos.x; x <= pos.x+radius;x+= Particle.PARTICLE_SIZE)
            for (float y = pos.y; y <= pos.y+radius;y+= Particle.PARTICLE_SIZE)
                particles.add(new Particle(x,y,vx*0.3f+(rand.nextFloat()-0.5f), vy*0.3f+(rand.nextFloat()-0.5f), color, Particle.PARTICLE_SIZE, noFade));
    }

    private boolean collides(Enemy enemy)
    {
        return collides(enemy.pos,enemy.radius);
    }

    public boolean collides(Vector2 pos, float radius)
    {
        return this.pos.x + this.radius >= pos.x && this.pos.x <= pos.x + radius && this.pos.y + this.radius >= pos.y && this.pos.y <= pos.y + radius;
    }

    public void draw(Renderer renderer)
    {
        renderer.square(color, pos.x, pos.y, radius);
    }

    private void recalcColor()
    {
        color = new Color(radius/EXPLODE_SIZE, 0.1f,1-radius/EXPLODE_SIZE,1f);
    }

    private void recalcSpeed()
    {
        speed = 5f;//(float)(50.0 - 50.0*EXPLODE_AREA/((EXPLODE_AREA-Math.min(Math.pow((double)radius,2.0),EXPLODE_AREA-1.0))));
    }

    public boolean update(Vector2 playerPos, List<Enemy> peers, List<Particle> particles)
    {
        List<Enemy> remove = new ArrayList<Enemy>();
        for (Enemy i:peers)
        {
            if (this==i)
                continue;
            if (collides(i))
            {
                if (true || i.radius==radius) {
                    if (radius < i.radius) {
                        createParticles(particles);
                        pos = i.pos;
                    } else
                        i.createParticles(particles);
                    radius = (int) Math.sqrt(Math.pow((double) radius, 2) + Math.pow((double) i.radius, 2));
                    recalcColor();
                    recalcSpeed();
                    remove.add(i);
                }
                else {
                    float dX = (pos.x+radius)/2f-(radius+i.pos.x)/2;
                    if (dX>0)
                        pos.x += i.pos.x+radius;
                }
            }
        }
        Vector2 delta = new Vector2(playerPos);
        delta.sub(pos);
        double newAngle = delta.angleRad();
        if (newAngle>Math.PI)
            newAngle -= 2*Math.PI ;
        else if (newAngle<-Math.PI)
            newAngle += 2*Math.PI ;
        if (angle == -200.25f)
            angle = newAngle;
        else {
            double dA = newAngle - angle;
            if (dA>Math.PI)
                dA = -(2*Math.PI-dA);
            else if(dA<-Math.PI)
                dA = (2*Math.PI+dA);
            angle += 0.03f * (dA);
            if (angle>Math.PI)
                angle -= 2*Math.PI ;
            else if (angle<-Math.PI)
                angle += 2*Math.PI ;
        }
        pos.add((float)(speed *Math.cos(angle)), (float)(speed *Math.sin(angle)));
        if (remove.size()>0) {
            peers.removeAll(remove);
            return true;
        }
        return false;
    }
}
