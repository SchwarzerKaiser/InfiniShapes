package com.leewilson.infinishapes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;

import com.leewilson.infinishapes.views.ShapesView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ShapesView mView;
    private Display mDisplay;

    public static final String EXTRA_SHAPES = "com.leewilson.infiniteshapes.MainActivity.EXTRA_SHAPES";
    public static final String EXTRA_COLORS = "com.leewilson.infiniteshapes.MainActivity.EXTRA_COLORS";
    public static final String LOG_TAG = "LWILSON";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = findViewById(R.id.shapes_view);
        mDisplay = getWindowManager().getDefaultDisplay();

        if(savedInstanceState != null) {
            mView.setColors(savedInstanceState.getIntegerArrayList(EXTRA_COLORS));
            mView.setShapes(savedInstanceState.<Rect>getParcelableArrayList(EXTRA_SHAPES));
        } else {
            mView.setColors(new ArrayList<Integer>());
            mView.setShapes(new ArrayList<Rect>());
        }

        mView.setDisplay(mDisplay);

        // Temp. For getting the screen size.
        Display screen = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screen.getSize(size);
        Log.d(LOG_TAG, String.format("Screen: %s wide and %s high.", size.x, size.y));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(EXTRA_SHAPES, mView.getShapes());
        outState.putIntegerArrayList(EXTRA_COLORS, mView.getColors());
        super.onSaveInstanceState(outState);
    }
}
