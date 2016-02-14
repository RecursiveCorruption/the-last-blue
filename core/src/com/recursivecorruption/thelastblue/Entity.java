package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.recursivecorruption.thelastblue.graphics.Renderer;

public abstract class Entity {
    protected Vector2 pos, vel;
    protected Color color;
    protected float radius;

    protected Entity(Color color, float radius, float x, float y, float vx, float vy) {
        this.color = new Color(color);
        this.radius = radius;
        pos = new Vector2(x, y);
        vel = new Vector2(vx, vy);
    }

    public boolean collides(Entity other) {
        return pos.x+radius>=other.pos.x&&pos.x<=other.pos.x+other.radius&&pos.y+radius>=other.pos.y&&pos.y<=other.pos.y+other.radius;
    }

    public void draw(Renderer renderer) {
        renderer.square(color, pos, radius);
    }
}
