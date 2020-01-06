package com.leewilson.infinishapes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

public class ShapesView extends View {

    /*

    TODO: Tweak move touch event handling to make sure shapes can be moved by their edges instead of being
        shunted instantly to the touch event coords
     */

    private LinkedList<Rect> mShapes;
    private LinkedList<Integer> mColors;
    private Paint mPaint;
    private Display mDisplay;
    private boolean mIsShapeMoved = false;

    private static final int SQUARE_RADIUS = 300;
    private static final int NO_SHAPE_FOUND = -1;


    public ShapesView(Context context) {
        super(context);
        init(null);
    }

    public ShapesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ShapesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ShapesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attributeSet) {
        mPaint = new Paint();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        boolean handled = false;

        final int touchX = (int) event.getX();
        final int touchY = (int) event.getY();


        switch(event.getActionMasked()) {

            // In case some empty space shows (in theory there shouldn't be any).
            case MotionEvent.ACTION_DOWN:

                if(getTouchedRectIndex(touchX, touchY) == NO_SHAPE_FOUND) {
                    Rect newShape = new Rect(defineRectFromCoords(touchX, touchY));
                    mShapes.add(newShape);
                    mColors.add(getRandomColor());
                }
                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_MOVE:

                final int newX = (int) event.getX();
                final int newY = (int) event.getY();
                int touchedShapeIndex = getTouchedRectIndex(touchX, touchY);
                boolean isMoved = false;


                if(touchedShapeIndex != NO_SHAPE_FOUND) {

                    // Generate shape at the bottom of the pile where this one was removed
                    if(!mIsShapeMoved) {
                        // Increment index of touched shape
                        touchedShapeIndex++;

                        mShapes.addFirst(defineRectFromCoords(touchX, touchY));
                        mColors.addFirst(getRandomColor());
                        mIsShapeMoved = true;
                    }

                    mShapes.remove(touchedShapeIndex);
                    int colorOfMovedShape = mColors.get(touchedShapeIndex);
                    mColors.remove(touchedShapeIndex);
                    final Rect movedRectangle = defineRectFromCoords(newX, newY);
                    mShapes.add(movedRectangle);
                    mColors.add(colorOfMovedShape);
                }

                invalidate();
                handled = true;
                break;

            case MotionEvent.ACTION_UP:

                // Reset flag for next move
                mIsShapeMoved = false;

                handled = true;
                break;
        }

        return super.onTouchEvent(event) || handled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < mShapes.size(); i++) {
            mPaint.setColor(mColors.get(i));

            canvas.drawRect(mShapes.get(i), mPaint);
        }
    }

    /**
     * Randomly scatters shapes across the screen, such that no uncovered space exists.
     */
    private void scatterShapes() {

        // Get screen size properties
        final Point size = new Point();
        mDisplay.getSize(size);
        final int numXAxisSectors = size.x / (SQUARE_RADIUS / 2);
        final int numYAxisSectors = size.y / (SQUARE_RADIUS / 2);
        final int sectorRadiusX = (size.x / numXAxisSectors) / 2;
        final int sectorRadiusY = (size.y / numYAxisSectors) / 2;

        // Algorithm loop
        for (int y = sectorRadiusY; y <= size.y; y += (sectorRadiusY * 2)) {
            for (int x = sectorRadiusX; x <= size.x; x += (sectorRadiusX * 2)) {
                final int newRectX = x + randomScatterOffset();
                final int newRectY = y + randomScatterOffset();
                mShapes.add(defineRectFromCoords(newRectX, newRectY));
                mColors.add(getRandomColor());
            }
        }

        // Shuffles collection to make it look more random
        Collections.shuffle(mShapes);

        // Reload view
        invalidate();
    }

    /**
     * Helper method for scatterShapes() to provide an offset value to simulate random scattering.
     * @return Random offset number with limitations applied
     */
    private int randomScatterOffset() {
        final int randomOffsetLimit = SQUARE_RADIUS / 2;
        final Random random = new Random();
        final int randomOffset = random.nextInt(randomOffsetLimit);
        if(random.nextInt(10) % 2 == 0) {
            return randomOffset;
        } else {
            return 0 - randomOffset;
        }
    }

    /**
     *Defines a new rectangle based on the coordinates provided.
     *
     * @param x
     * @param y
     * @return new Rect object with defined properties
     */
    private Rect defineRectFromCoords(float x, float y) {
        return new Rect(/*left*/    (int) (x - SQUARE_RADIUS),
                /*top*/     (int) (y - SQUARE_RADIUS),
                /*right*/   (int) (x + SQUARE_RADIUS),
                /*bottom*/  (int) (y + SQUARE_RADIUS));
    }

    /**
     * Finds the index of the touched rectangle. Searches through list of shapes starting from the last
     * one drawn (therefore visible on the canvas). Returns -1 if no matching Rect is found.
     *
     * @param touchX
     * @param touchY
     * @return index
     */
    private int getTouchedRectIndex(final int touchX, final int touchY) {
        for (int i = mShapes.size() - 1; i >= 0; i--) {
            if(mShapes.get(i).contains(touchX, touchY)) {
                return i;
            }
        }
        return NO_SHAPE_FOUND;
    }

    private int getRandomColor() {
        final Random r = new Random();
        return Color.argb(255, r.nextInt(256), r.nextInt(256), r.nextInt(256));
    }

    public void setDisplay(Display display) {
        mDisplay = display;
        if(mShapes.isEmpty())
            scatterShapes();
    }

    public ArrayList<Rect> getShapes() {
        return new ArrayList<>(mShapes);
    }

    public ArrayList<Integer> getColors() {
        return new ArrayList<>(mColors);
    }

    public void setShapes(ArrayList<Rect> shapes) {
        mShapes = new LinkedList<>(shapes);
    }

    public void setColors(ArrayList<Integer> colors) {
        mColors = new LinkedList<>(colors);
    }
}
