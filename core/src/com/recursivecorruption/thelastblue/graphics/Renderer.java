package com.recursivecorruption.thelastblue.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class Renderer
{
    class Rect
    {
        Color color;
        Vector2 pos;
        int sideLength;

        Rect(Color color, Vector2 pos, int sideLength) {
            this.color = color;
            this.pos = pos;
            this.sideLength = sideLength;
        }
    }

    public static final String VERT_SHADER =
            "attribute vec4 a_position;    \n" +
                    "attribute vec4 a_color;\n" +
                    "varying vec4 v_color;" +
                    "uniform mat4 u_projTrans;\n" +
                    "void main()                  \n" +
                    "{                            \n" +
                    "   v_color = a_color; \n" +
                    "   gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);  \n"      +
                    "}                            \n" ;

    public static final String FRAG_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 v_color;\n" +
                    "void main()                                  \n" +
                    "{                                            \n" +
                    "  gl_FragColor = v_color;\n" +
                    "}";

    private BitmapFont font;
    private Batch batch;
    private List<Rect> rectangles;

    private Mesh mesh;
    private OrthographicCamera cam;
    private ShaderProgram shader;

    private final int VERT_POS_VALS = 3; //x,y,z
    private final int VERT_COLOR_VALS = 4; //r,b,g,a
    private final int VALS_PER_VERT = VERT_POS_VALS + VERT_COLOR_VALS;
    private final int VALS_PER_INDICE = 6, VERTS_PER_RECT = 4;

    private boolean useShapeRenderer = false;
    private ShapeRenderer shapeRenderer;

    public Renderer(OrthographicCamera cam)
    {
        this.cam = cam;
        if (useShapeRenderer)
            shapeRenderer = new ShapeRenderer();
        else
            shader = createMeshShader();
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        rectangles = new ArrayList<Rect>();
        font = new BitmapFont();
        updateFont(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void updateFont(int width, int height)
    {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(70f*Gdx.graphics.getDensity());
        parameter.size = (int)Math.min(Graphics.getSX() / 10f, Graphics.getSY()/6f);//(Gdx.graphics.getDensity()*160f);
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?: ";
        parameter.flip = true;
        font.setColor(Color.WHITE);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("MontereyFLF.ttf"));
        font.dispose();
        font = generator.generateFont(parameter);
        generator.dispose();
    }

    private void createFromList() {
        int numRects = rectangles.size();

        int numVerts = VERTS_PER_RECT * numRects;
        mesh = new Mesh(true, numVerts, numRects*VALS_PER_INDICE, VertexAttribute.Position(), VertexAttribute.ColorUnpacked());

        short[] indices = new short[numRects*VALS_PER_INDICE];
        float[] verts = new float[numVerts * VALS_PER_VERT];
        if (useShapeRenderer) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setProjectionMatrix(cam.combined);
        }

        for (int i = 0; i < numRects; i++) {

            Rect rect = rectangles.get(i);
            if (useShapeRenderer) {
                shapeRenderer.setColor(rect.color);
                shapeRenderer.rect(rect.pos.x, rect.pos.y, rect.sideLength, rect.sideLength);
            } else {
                int offset = i * VALS_PER_VERT * VERTS_PER_RECT;
                setValues(offset, 0, verts, rect.color, rect.pos.x, rect.pos.y);
                setValues(offset, 1, verts, rect.color, rect.pos.x + rect.sideLength, rect.pos.y);
                setValues(offset, 2, verts, rect.color, rect.pos.x + rect.sideLength, rect.pos.y + rect.sideLength);
                setValues(offset, 3, verts, rect.color, rect.pos.x, rect.pos.y + rect.sideLength);

                indices[i * 6] = (short) (i * 4);
                indices[i * 6 + 1] = (short) (i * 4 + 1);
                indices[i * 6 + 2] = (short) (i * 4 + 2);
                indices[i * 6 + 3] = (short) (i * 4 + 2);
                indices[i * 6 + 4] = (short) (i * 4 + 3);
                indices[i * 6 + 5] = (short) (i * 4);
            }
        }
        if (useShapeRenderer) {
            shapeRenderer.end();
        } else {
            mesh.setVertices(verts);
            mesh.setIndices(indices);
        }
    }

    private void setValues(int rectOffset, int vertNum, float[] verts, Color color, float x, float y) {
        int offset = rectOffset + vertNum * VALS_PER_VERT;
        verts[offset] = x;
        verts[offset + 1] = y;
        verts[offset + 2] = 0; //z-pos
        verts[offset + 3] = color.r;
        verts[offset + 4] = color.g;
        verts[offset + 5] = color.b;
        verts[offset + 6] = color.a;
    }

    private static ShaderProgram createMeshShader()
    {
        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(VERT_SHADER, FRAG_SHADER);
        String log = shader.getLog();
        if (!shader.isCompiled())
            throw new GdxRuntimeException(log);
        if (log != null && log.length() != 0)
            System.out.println("Shader Log: " + log);
        return shader;
    }

    public void beginText()
    {
       batch.begin();
    }

    public void endText()
    {
        batch.end();
    }

    public void resize(int width, int height)
    {
        updateFont(width, height);
    }

    public void square(Color color, Vector2 pos, float radius)
    {
        rectangles.add(new Rect(color, pos, (int) (radius)));
    }

    public void circle(Color color, Vector2 pos, float radius)
    {
        //TODO: Add circle rendering
    }

    public void generateSquares()
    {
        createFromList();
        rectangles.clear();
    }

    public void render()
    {
        batch.setProjectionMatrix(cam.combined);
        if (useShapeRenderer)
            return;
        shader.begin();
        shader.setUniformMatrix("u_projTrans", cam.combined);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();
    }

    public void dispose()
    {
        mesh.dispose();
        shader.dispose();
        batch.dispose();
        font.dispose();
    }

    public void printCentered(int y, String message)
    {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);
        font.draw(batch,glyphLayout, Graphics.getSX()/2f-glyphLayout.width / 2f, y - glyphLayout.height / 2f);
    }
}