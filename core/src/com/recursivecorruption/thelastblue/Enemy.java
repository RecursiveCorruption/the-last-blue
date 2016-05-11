package com.recursivecorruption.thelastblue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.Random;

public class Enemy extends Entity {
    public static float EXPLODE_SIZE = 65f;
    private static float EXPLODE_AREA = (float) Math.pow(EXPLODE_SIZE, 2);
    Player player;
    private float speed;
    private double angle = -200.25f;
    private static float maxRad = 15f;

    public Enemy(float x, float y, Player player) {
        super(new Color(0.1f, 1f, 1f, 1f), 15f, x, y, 0, 0);
        this.player = player;
        recalcColor();
        recalcSpeed();
        rand = new Random();
    }

    public static void refresh()
    {
        maxRad = 15f;
    }

    public static float getMaxRad()
    {
        return maxRad;
    }

    private void recalcColor() {
        color = new Color(radius / EXPLODE_SIZE, 0.2f, 1 - radius / EXPLODE_SIZE, 1f);
    }

    private void recalcSpeed() {
        speed = 300f;//(float)(50.0 - 50.0*EXPLODE_AREA/((EXPLODE_AREA-Math.min(Math.pow((double)radius,2.0),EXPLODE_AREA-1.0))));
    }

    @Override
    public Entity update(List<Entity> entities) {
        maxRad = Math.max(maxRad, radius);
        vel.set((float) (speed * Math.cos(angle)), (float) (speed * Math.sin(angle)));
        if (radius > Enemy.EXPLODE_SIZE) {
            TheLastBlueGame.addScore((int) Math.pow(radius, 2));
            return this;
        }
        for (Entity i : entities) {
            if (this == i || !(i instanceof Enemy))
                continue;
            if (collides(i)) {
                if (true || i.radius == radius) {
                    Entity toDie, toLive;
                    if (radius < i.radius) {
                        toDie = this;
                        toLive = i;
                    } else {
                        toDie = i;
                        toLive = this;
                    }
                    toLive.radius = (int) Math.sqrt(Math.pow((double) toLive.radius, 2) + Math.pow((double) toDie.radius, 2));
                    ((Enemy) toLive).recalcColor();
                    ((Enemy) toLive).recalcSpeed();
                    return toDie;
                } else {
                    float dX = (pos.x + radius) / 2f - (radius + i.pos.x) / 2;
                    if (dX > 0)
                        pos.x += i.pos.x + radius;
                }
            }
        }
        Vector2 delta = new Vector2(player.pos);
        delta.sub(pos);
        double newAngle = delta.angleRad();
        if (newAngle > Math.PI)
            newAngle -= 2 * Math.PI;
        else if (newAngle < -Math.PI)
            newAngle += 2 * Math.PI;
        if (angle == -200.25f)
            angle = newAngle;
        else {
            double dA = newAngle - angle;
            if (dA > Math.PI)
                dA = -(2 * Math.PI - dA);
            else if (dA < -Math.PI)
                dA = (2 * Math.PI + dA);
            angle += 0.03f * (dA);
            if (angle > Math.PI)
                angle -= 2 * Math.PI;
            else if (angle < -Math.PI)
                angle += 2 * Math.PI;
        }
        pos.add(Gdx.graphics.getDeltaTime() * (float) (speed * Math.cos(angle)), Gdx.graphics.getDeltaTime() * (float) (speed * Math.sin(angle)));
        return null;
    }
}
