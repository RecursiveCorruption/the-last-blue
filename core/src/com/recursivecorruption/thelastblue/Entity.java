package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
    protected Vector2 pos,vel;
    protected final Color color;
    protected float radius;
    protected Entity(Color color, float radius, float x, float y, float vx, float vy) {
        this.color = new Color(color);
        this.radius = radius;
        pos = new Vector2(x, y);
        vel = new Vector2(vx, vy);
    }
}
