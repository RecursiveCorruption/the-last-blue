package com.recursivecorruption.thelastblue.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Renderer
{

    public static final String VERT_SHADER =
            "attribute vec2 a_position;\n" +
                    "attribute vec4 a_color;\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	vColor = a_color;\n" +
                    "	gl_Position =  u_projTrans * vec4(a_position.xy, 0.0, 1.0);\n" +
                    "}";

    public static final String FRAG_SHADER =
            "#ifdef GL_ES\n" +
                    "precision mediump float;\n" +
                    "#endif\n" +
                    "varying vec4 vColor;\n" +
                    "void main() {\n" +
                    "	gl_FragColor = vColor;\n" +
                    "}";

    private BitmapFont font;
    private Batch batch;

    protected static ShaderProgram createMeshShader()
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

    Mesh mesh;
    OrthographicCamera cam;
    ShaderProgram shader;

    //Position attribute - (x, y)
    public static final int POSITION_COMPONENTS = 2;

    //Color attribute - (r, g, b, a)
    public static final int COLOR_COMPONENTS = 4;

    //Total number of components for all attributes
    public static final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;

    //The maximum number of triangles our mesh will hold
    public static final int MAX_TRIS = 1;

    //The maximum number of vertices our mesh will hold
    public static final int MAX_VERTS = MAX_TRIS * 3;

    //The array which holds all the data, interleaved like so:
    //    x, y, r, g, b, a
    //    x, y, r, g, b, a,
    //    x, y, r, g, b, a,
    //    ... etc ...
    private float[] verts = new float[MAX_VERTS * NUM_COMPONENTS];

    //The index position
    private int idx = 0;
    private ShapeRenderer shapeRenderer;

    public Renderer(OrthographicCamera cam)
    {
        this.cam = cam;
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("MontereyFLF.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)(70f/Gdx.graphics.getDensity());
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?: ";
        parameter.flip = true;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font = generator.generateFont(parameter);
        generator.dispose();
        batch.setProjectionMatrix(cam.combined);
        mesh = new Mesh(true, MAX_VERTS, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, COLOR_COMPONENTS, "a_color"));

        shader = createMeshShader();
    }

    public void begin(boolean shape)
    {
       if (shape)
            return;//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        else
            batch.begin();
    }

    public void end()
    {
        if (batch.isDrawing())
            batch.end();
        else
            flush();
        //    shapeRenderer.end();
    }

    public void resize(OrthographicCamera cam)
    {
        batch.setProjectionMatrix(cam.combined);
        //shapeRenderer.setProjectionMatrix(cam.combined);
    }

    public void square(Color color, Vector2 pos, float radius)
    {
        //shapeRenderer.setColor(color);
        //shapeRenderer.rect(pos.x, pos.y, radius, radius);
        render(pos.x,pos.y,pos.x+radius,pos.y,pos.x+radius,pos.y+radius,pos.x,pos.y+radius,color);
    }

    public void circle(Color color, Vector2 pos, float radius)
    {
        //shapeRenderer.setColor(Color.GRAY);
        //shapeRenderer.circle(pos.x,pos.y, 20f);
    }

    /*1 --- 2
      |     |
      |     |
      4 --- 3
    */
    public void render(float p1x,float p1y, float p2x, float p2y, float p3x,float p3y, float p4x,float p4y, Color color)
    {
        //this will push the triangles into the batch
        drawTriangle(p1x,p1y,p2x,p2y,p3x,p3y,color);
        //drawTriangle(p1x,p1y,p3x,p3y,p4x,p4y,color);

        //this will render the triangles to GL
        //flush();
    }

    void flush()
    {
        //if we've already flushed
        if (idx == 0)
            return;

        //sends our vertex data to the mesh
        mesh.setVertices(verts);

        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //number of vertices we need to render
        int vertexCount = (idx / NUM_COMPONENTS);

        //update the camera with our Y-up coordiantes
        cam.setToOrtho(true, Graphics.getSX(), Graphics.getSY());

        //start the shader before setting any uniforms
        shader.begin();

        //update the projection matrix so our triangles are rendered in 2D
        shader.setUniformMatrix("u_projTrans", cam.combined);

        //render the mesh
        mesh.render(shader, GL20.GL_TRIANGLES, 0, vertexCount);

        shader.end();

        //re-enable depth to reset states to their default
        Gdx.gl.glDepthMask(true);

        //reset index to zero
        idx = 0;
    }

    void drawTriangle(float p1x,float p1y, float  p2x,float p2y, float p3x,float p3y, Color color)
    {
        //we don't want to hit any index out of bounds exception...
        //so we need to flush the batch if we can't store any more verts
        if (idx == verts.length)
            flush();

        //now we push the vertex data into our array
        //we are assuming (0, 0) is lower left, and Y is up

        //bottom left vertex
        verts[idx++] = p1x; 			//Position(x, y)
        verts[idx++] = p1y;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //top left vertex
        verts[idx++] = p2x; 			//Position(x, y)
        verts[idx++] = p2y;
        verts[idx++] = color.r; 	//Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;

        //bottom right vertex
        verts[idx++] = p3x;	 //Position(x, y)
        verts[idx++] = p3y;
        verts[idx++] = color.r;		 //Color(r, g, b, a)
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
    }

    public void dispose()
    {
        mesh.dispose();
        shader.dispose();
    }

    public void printCentered(int y, String message)
    {
        GlyphLayout glyphLayout = new GlyphLayout();
        glyphLayout.setText(font, message);
        font.draw(batch,glyphLayout, Graphics.getSX()/2f-glyphLayout.width / 2f, y - glyphLayout.height / 2f);
    }
}