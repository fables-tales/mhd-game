package com.me.mygdxgame;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class PsyEntity implements Drawable, Updatable {

    private Body mBody;
    private Sprite mSprite;
    private float mVelocityEstimate = 0;
    private float mLastPosition = 0;
    private ShapeRenderer mShapeRenderer;
    private GameWrapper mGameWrapper;
    private ExplosionManager em;
    private float mTrail[] = new float[30];
    private Random r = new Random();

    public PsyEntity(GameWrapper gw) {
        mGameWrapper = gw;
        em = new ExplosionManager("bananarama");
        mSprite = ResourceManager.spriteFromFilename("assets/psy-rolling.png");
        mShapeRenderer = new ShapeRenderer();
        BodyDef bd = new BodyDef();
        bd.type = BodyType.DynamicBody;
        bd.fixedRotation = false;
        bd.allowSleep = false;
        bd.position.set(new Vector2(200 / GameWrapper.PIXELS_PER_METER,
                200 / GameWrapper.PIXELS_PER_METER));
        FixtureDef fd = new FixtureDef();
        CircleShape cs = new CircleShape();
        float radius = mSprite.getHeight() / (2 * GameWrapper.PIXELS_PER_METER);
        cs.setPosition(new Vector2(0, 0));
        cs.setRadius(radius);
        fd.shape = cs;
        fd.friction = 0;
        mBody = gw.mWorld.createBody(bd);
        mBody.createFixture(fd);
        mBody.setAngularDamping(0.0f);
        mBody.setFixedRotation(false);

        for (int i = 1; i < mTrail.length; i++) {
            mTrail[i] = mTrail[i - 1] + r.nextFloat() * 15 - 15 / 2;
        }
    }

    public void update() {
        em.update();
        SegmentLoader sl = mGameWrapper.mSegmentLoader;
        mSprite.setScale((sl.getCurrentFans() / (sl.getMaxFans())) + 0.5f);
        mVelocityEstimate = getXPosition() - mLastPosition;
        mLastPosition = getXPosition();
        mSprite.setPosition(
                mBody.getPosition().x * GameWrapper.PIXELS_PER_METER
                        - mSprite.getWidth() / 2,
                mBody.getPosition().y * GameWrapper.PIXELS_PER_METER
                        - mSprite.getWidth() / 2);
        mSprite.setRotation((float) (mBody.getAngle() * (180 / Math.PI)));

        mBody.setAngularVelocity(-10);
        float x = mBody.getPosition().x;
        float y = (600 - Gdx.input.getY()) / GameWrapper.PIXELS_PER_METER;
        mBody.setTransform(new Vector2(x, y), mBody.getAngle());
        pushBackTrail();
    }

    private void pushBackTrail() {
        float k = mGameWrapper.mSegmentLoader.getCurrentLoudness() / 10;
        k = k * k;
        for (int i = mTrail.length - 1; i > 0; i--) {
            mTrail[i] = mTrail[i - 1] + r.nextFloat() * k - k / 2;
        }

        mTrail[0] = 0;

    }

    public void draw(SpriteBatch sb) {
        if (mGameWrapper.mSegmentLoader.getNextBeatTime()-mGameWrapper.mGangnamPlayer.getCurrentSeconds() < 0.02) {
            for (int i = 0; i < mGameWrapper.mSegmentLoader.getCurrentFans()*10/mGameWrapper.mSegmentLoader.getMaxFans()+1; i++) {
            em.pew(getXPosition(), getYPosition());
            }
        }
        em.draw(sb);
        mSprite.draw(sb);
        
    }

    public void drawTrail() {
        mShapeRenderer.setProjectionMatrix(mGameWrapper.getProjectionMatrix());
        mShapeRenderer.begin(ShapeType.Line);
        mShapeRenderer.setColor(0, 1.0f,
                1.0f - mGameWrapper.mSegmentLoader.getPitch(1), 1.0f);
        float baseY = mSprite.getY() + mSprite.getHeight() / 2;
        for (int i = 1; i < mTrail.length; i++) {
            for (int j = 0; j < 6; j++) {
                mShapeRenderer.line(mSprite.getX() + mSprite.getWidth() / 2 - 8
                        * (i - 1), baseY + j - 3 + mTrail[i - 1],
                        mSprite.getX() + mSprite.getWidth() / 2 - 8 * i, baseY
                                + j - 3 + mTrail[i]);
            }

        }
        mShapeRenderer.end();
    }

    public float getXPosition() {
        return mBody.getPosition().x * GameWrapper.PIXELS_PER_METER;
    }

    public float getYPosition() {
        return mBody.getPosition().y * GameWrapper.PIXELS_PER_METER;
    }

    public float getVelocityEstimate() {
        return mVelocityEstimate;
    }

    public void setPosition(float i, float j) {
        mBody.setTransform(i / GameWrapper.PIXELS_PER_METER, j
                / GameWrapper.PIXELS_PER_METER, mBody.getAngle());
    }

    public float getSize() {
        return mSprite.getHeight() * mSprite.getScaleX() / 2;
    }

    public Rectangle getSprite() {
        return mSprite.getBoundingRectangle();
    }
}
