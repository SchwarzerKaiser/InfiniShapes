package com.leewilson.infinishapes;

import android.graphics.RectF;
import android.os.Bundle;
import android.view.Display;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

        if (savedInstanceState != null) {
            mView.setColors(savedInstanceState.getIntegerArrayList(EXTRA_COLORS));
            mView.setShapes(savedInstanceState.<RectF>getParcelableArrayList(EXTRA_SHAPES));
        } else {
            mView.setColors(new ArrayList<Integer>());
            mView.setShapes(new ArrayList<RectF>());
        }

        mView.setDisplay(mDisplay);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelableArrayList(EXTRA_SHAPES, mView.getShapes());
        outState.putIntegerArrayList(EXTRA_COLORS, mView.getColors());
        super.onSaveInstanceState(outState);
    }
}
