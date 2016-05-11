package com.recursivecorruption.thelastblue.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

public class Renderer {
    private BitmapFont largeFont, smallFont;
    private SpriteBatch batch;
    private Sprite pxSpr;

    public Renderer(OrthographicCamera cam) {
        Texture tex = new Texture(Gdx.files.internal("px.png"));
        pxSpr = new Sprite(tex);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        largeFont = new BitmapFont();
        smallFont = new BitmapFont();
        updateFont(Graphics.getSX(), Graphics.getSY());
    }

    private void updateFont(int width, int height) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int) Math.min(width / 10f, height / 6f);
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?: ";
        parameter.flip = true;
        largeFont.setColor(Color.WHITE);
        smallFont.setColor(Color.WHITE);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("MontereyFLF.ttf"));
        largeFont.dispose();
        smallFont.dispose();
        largeFont = generator.generateFont(parameter);
        parameter.size /= 2;
        smallFont = generator.generateFont(parameter);
        generator.dispose();
    }

    public void begin() {
        batch.begin();
    }

    public void end() {
        batch.end();
    }

    public void resize(int width, int height, OrthographicCamera cam) {
        updateFont(width, height);
        batch.setProjectionMatrix(cam.combined);
    }

    public void square(Color color, Vector2 pos, float radius) {
        pxSpr.setScale(radius);
        pxSpr.setCenter(pos.x + radius / 2, pos.y + radius / 2);
        pxSpr.setColor(color);
        pxSpr.draw(batch);
    }

    public void circle(Color color, Vector2 pos, float radius) {
        //TODO: Add circle rendering
    }

    public void dispose() {
        batch.dispose();
        largeFont.dispose();
        smallFont.dispose();
    }

    public void printCentered(int y, String message) {
        printCentered(y, message, false);
    }

    public void printCentered(int y, String message, boolean small) {
        BitmapFont font = getFont(small);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);
        font.draw(batch, glyphLayout, Graphics.getSX() / 2f - glyphLayout.width / 2f, y - glyphLayout.height / 2f);
    }

    private BitmapFont getFont(boolean small) {
        return small ? smallFont : largeFont;
    }

    public void printLeftOf(int x, int y, String message) {
        printLeftOf(x, y, message, false);
    }

    public void printLeftOf(int x, int y, String message, boolean small) {
        BitmapFont font = getFont(small);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);
        font.draw(batch, glyphLayout, x - glyphLayout.width, y - glyphLayout.height);
    }

    public void printRightOf(int x, int y, String message) {
        printLeftOf(x, y, message, false);
    }

    public void printRightOf(int x, int y, String message, boolean small) {
        BitmapFont font = getFont(small);
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);
        font.draw(batch, glyphLayout, x, y - glyphLayout.height / 2f);
    }
}