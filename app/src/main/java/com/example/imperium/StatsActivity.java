package com.example.imperium;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class StatsActivity extends MainActivity {
    private static GraphView statGraph;
    private ScaleGestureDetector mScaleGestureDetector;
    private static float dY;
    private static float dX;
    public static double initialY;
    public static double initialX;
    private float mScaleFactor = 0.9f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        statGraph = findViewById(R.id.statGraph);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        statGraph.setScaleX(mScaleFactor);
        statGraph.setScaleY(mScaleFactor);
        makeGraph(new double[]{1, 3, 2, 8, 6, 1, 3, 5, 9, 7, 5, 0});
        touched();
    }
    private void makeGraph(double[] stats){
        DataPoint[] data = new DataPoint[stats.length];
        for(int i=0; i<stats.length; i++) {
            data[i] = new DataPoint(i, stats[i]);
        }
        statGraph.addSeries(new LineGraphSeries<>(data));
        statGraph.getViewport().setMinX(1);
        statGraph.getViewport().setXAxisBoundsManual(true);
    }
    private void changeX(){

    }
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 3.0f));
            statGraph.setScaleX(mScaleFactor);
            statGraph.setScaleY(mScaleFactor);
            return true;
        }
    }
    public static void touched() {
        statGraph.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            initialX = statGraph.getViewport().getMinX(true);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            //view.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                            statGraph.getViewport().setXAxisBoundsManual(true);
                            statGraph.getViewport().setMinX(statGraph.getViewport().getMaxXAxisSize()*
                                    initialX+(2*(dX-(view.getX() - event.getRawX())))/view.getMeasuredWidth());

                            Log.i("xpos", ""+(2*(dX-(view.getX() - event.getRawX())))/view.getMeasuredWidth());
                            statGraph.getViewport().setYAxisBoundsManual(true);

                            break;
                        default:
                            return false;
                    }
                }
                else{
                    return false;
                }
                return true;
            }
        });
    }
}
