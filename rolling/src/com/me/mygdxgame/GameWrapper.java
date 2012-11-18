package com.me.mygdxgame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class GameWrapper implements ApplicationListener {

    public static float PIXELS_PER_METER = 16;
    public PsyEntity mPsyEntity;
    public SegmentLoader mSegmentLoader;

    public World mWorld;
    private Camera mCamera;

    private Box2DDebugRenderer mDebugRenderer;
    private List<Drawable> mDrawables = new ArrayList<Drawable>();
    private ExplosionManager mExplosionManager;
    private boolean mGameOver = false;

    private Sprite mGameOverSprite;
    private int mHeight = 600;
    private SpriteBatch mSpriteBatch;
    private ShapeRenderer mBackgroundRenderer;

    private Color[] mColors = new Color[4];

    private ShapeRenderer mStarRenderer;
    private ArrayList<Vector2> mStars;
    private List<Updatable> mUpdateables = new ArrayList<Updatable>();
    private int mWidth = 800;
    private Random rng = new Random();
    private List<MermaidEntity> mMermaidEntities;
    GangnamPlayer mGangnamPlayer;

    @Override
    public void create() {
        mStars = new ArrayList<Vector2>();
        mSpriteBatch = new SpriteBatch();
        mStarRenderer = new ShapeRenderer();
        mBackgroundRenderer = new ShapeRenderer();
        mGangnamPlayer = GangnamPlayer.getInstance();
        mSegmentLoader = new SegmentLoader("assets/gangnam.json");
        mWorld = new World(new Vector2(0, 0), false);
        mDebugRenderer = new Box2DDebugRenderer();
        mDebugRenderer.setDrawBodies(true);
        mDebugRenderer.setDrawAABBs(true);
        mMermaidEntities = new ArrayList<MermaidEntity>();

        PsyEntity pse = new PsyEntity(this);
        mPsyEntity = pse;
        mDrawables.add(pse);
        mUpdateables.add(pse);
        mExplosionManager = new ExplosionManager("rainbow_burst");

        mGameOverSprite = ResourceManager
                .spriteFromFilename("assets/gameover.png");

        generateStars();

        Obstacle o = new Obstacle(this);
        mUpdateables.add(o);
        mDrawables.add(o);

        for (int i = 0; i < mColors.length; i++) {
            mColors[i] = new Color(0, 0, 0, 1);
        }

        for (int i = 0; i < 800; i += 100) {
            MermaidEntity me = new MermaidEntity((i / 100) % 2 * 2, i);
            mMermaidEntities.add(me);
        }
    }

    @Override
    public void dispose() {
    }

    public void gameOver() {
        mGameOver = true;
    }

    public Matrix4 getProjectionMatrix() {
        Matrix4 m = new Matrix4(mCamera.combined);
        m.translate(-mWidth / 2, -mHeight / 2, 0);
        return m;
    }

    @Override
    public void pause() {
    }

    @Override
    public void render() {
        update();
        draw();
    }

    @Override
    public void resize(int width, int height) {
        mCamera = new OrthographicCamera(width, height);
        mWidth = width;
        mHeight = height;
    }

    @Override
    public void resume() {
    }

    private void draw() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        drawBackground();

        if (!mGameOver) {
            mPsyEntity.drawTrail();
            drawStarField();
            drawSpriteBatch();
        } else {
            mPsyEntity.setPosition(mWidth / 2, mHeight / 2);
            mSpriteBatch.begin();
            drawGameOverText();
            mExplosionManager.draw(mSpriteBatch);
            mPsyEntity.draw(mSpriteBatch);
            mSpriteBatch.end();
        }

    }

    private void drawBackground() {
        mBackgroundRenderer.setProjectionMatrix(getProjectionMatrix());
        mBackgroundRenderer.begin(ShapeType.FilledRectangle);
        mColors[0] = new Color(1, 1, 1, 1);

        mColors[0].r = mSegmentLoader.getPitch(9) * 0.3f;
        mColors[0].g = mSegmentLoader.getPitch(4) * 0.3f;
        mColors[0].b = mSegmentLoader.getPitch(2) * 0.3f;

        mBackgroundRenderer.setColor(mColors[0]);
        mBackgroundRenderer.filledRect(0, 0, 800, 600);

        mBackgroundRenderer.end();
    }

    private void drawGameOverText() {
        mGameOverSprite.draw(mSpriteBatch);
    }

    private void drawSpriteBatch() {
        Matrix4 m = getProjectionMatrix();

        mSpriteBatch.setProjectionMatrix(m);
        mSpriteBatch.begin();
        drawMermaids();
        mExplosionManager.draw(mSpriteBatch);
        drawEntities();
        mSpriteBatch.end();
    }

    private void drawEntities() {
        for (Drawable d : mDrawables) {
            d.draw(mSpriteBatch);
        }
    }

    private void drawMermaids() {
        for (MermaidEntity me : mMermaidEntities) {
            me.draw(mSpriteBatch);
        }
    }

    private void drawStarField() {
        mStarRenderer.setProjectionMatrix(getProjectionMatrix());
        mStarRenderer.begin(ShapeType.FilledRectangle);

        Color color1 = new Color(
                mSegmentLoader.getTimbre(3) + 79.366f + 2.8950043f / 100.0f,
                mSegmentLoader.getPitch(8), 1, 1);

        Color color2 = new Color(mSegmentLoader.getPitch(1),
                mSegmentLoader.getPitch(7), mSegmentLoader.getPitch(2), 1);

        Random r = new Random();
        for (Vector2 pos : mStars) {
            float width = r.nextFloat()
                    * (mSegmentLoader.getCurrentLoudness() - 40) + 4;
            if (width < 0) {
                width = 2;
            }

            if (rng.nextBoolean()) {
                mStarRenderer.setColor(color1);
            } else {
                mStarRenderer.setColor(color2);
            }

            mStarRenderer.filledRect(pos.x - width / 2, pos.y - width / 2,
                    width, width);
        }
        mStarRenderer.end();
    }

    private void generateStars() {

        Random r = new Random();

        for (int i = 0; i < 40; i++) {
            mStars.add(new Vector2(r.nextFloat() * mWidth * 3, r.nextFloat()
                    * mHeight + 20));
        }
    }

    private void killOldObstacles() {
        Set<Obstacle> toRemove = new HashSet<Obstacle>();

        for (Updatable u : mUpdateables) {
            if (u instanceof Obstacle) {
                Obstacle o = (Obstacle) u;
                if (o.getPositionX() < -200) {
                    toRemove.add(o);
                }
            }
        }

        for (Obstacle o : toRemove) {
            mDrawables.remove(o);
            mUpdateables.remove(o);
        }
    }

    private void update() {
        mExplosionManager.update();
        updateStars();

        stepPhysics();
        for (Updatable u : mUpdateables) {
            u.update();
        }

        mGangnamPlayer.dispatchFrame();
        handleIncomingBeats();

        killOldObstacles();
    }

    private void stepPhysics() {
        mWorld.step(1.0f / 60f, 1, 1);
    }

    private void handleIncomingBeats() {
        if (mSegmentLoader.timeToNextBeat() < 0.02) {
            updateMermaids();
            Obstacle o = new Obstacle(this);
            mUpdateables.add(o);
            mDrawables.add(o);
            explode();
        }
    }

    private void updateMermaids() {
        for (MermaidEntity me : mMermaidEntities) {
            me.update();
        }
    }

    private void explode() {
        for (int i = 0; i < 2; i++) {
            Vector2 pewpos = new Vector2(rng.nextFloat() * 800,
                    rng.nextFloat() * 600);
            for (int j = 0; j < mSegmentLoader.getCurrentFans() * 19.0
                    / mSegmentLoader.getMaxFans(); j++) {
                mExplosionManager.pew(pewpos);
            }

        }
    }

    private void updateStars() {
        for (Vector2 pos : mStars) {

            float newX = pos.x - (3 * mGangnamPlayer.lastPeak * 10) - 1;
            if (newX < -200) {
                newX = mWidth - (-200 - newX);
            }
            pos.set(newX, pos.y);
        }
    }

}
