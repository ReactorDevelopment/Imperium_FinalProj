package com.example.imperium;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class BuildActivity extends MainActivity {
    /**Image for the classic map choice*/
    private ImageView classic;
    /**Flag of the blue player*/
    private ImageView player0;
    /**Flag of the red player*/
    private ImageView player1;
    /**Flag of the green player*/
    private ImageView player2;
    /**Indicator of the computer status of the blue player*/
    private ImageView ai0;
    /**Indicator of the computer status of the red player*/
    private ImageView ai1;
    /**Indicator of the computer status of the green player*/
    private ImageView ai2;
    /**Slider to determine the length of the players*/
    private SeekBar playerSelect;
    /**Indicator of how many players are selected*/
    private TextView playersTitle;
    /**Launches gameActivity with player and map information*/
    private Button create;
    /**Layout containing the player flags*/
    private AbsoluteLayout absoluteLayout;
    /**Application contents*/
    private Context context;
    /**The id of the selected map*/
    private static int mapId;
    /**Save number of players*/
    private static int players = 2;
    /**The progress of the slider*/
    private int progressNum = 0;
    /**Array containing which players are computers*/
    private boolean[] ais;

    /**Initializes the views and fields*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build);
        absoluteLayout = findViewById(R.id.flagCanvas);
        Log.i("initial", "added " + players + " players");
        context = this;
        mapId = 0;
        playerSelect();
        createButton();
    }

    /**Assigns the create button which launches gameActivity and passes in the game information*/
    private void createButton(){
        create = findViewById(R.id.makeGame);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GameActivity.class);
                intent.putExtra("players", players);
                Log.i("build", "added " + players + " players");
                intent.putExtra("mapId", mapId);
                intent.putExtra("ais", ais);
                intent.putExtra("tag", "new");
                startActivity(intent);
            }
        });
    }
    /**Assigns the ai buttons which toggle which players are computers*/
    private void createAiSelect(){
        ai0 = new ImageView(context);
        ai0.setBackgroundResource(R.drawable.helmet);
        ai1 = new ImageView(context);
        ai1.setBackgroundResource(R.drawable.helmet);
        ai2 = new ImageView(context);
        ai2.setBackgroundResource(R.drawable.helmet);
        //toggles if player is computer
        ai0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ais[0] = !ais[0];
                if(ais[0])
                    ai0.setBackgroundResource(R.drawable.halus);
                else
                    ai0.setBackgroundResource(R.drawable.helmet);
                Log.i("ais", "" + ais[0]);
            }
        });
        ai1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ais[1] = !ais[1];
                    if(ais[1])
                        ai1.setBackgroundResource(R.drawable.halus);
                    else
                        ai1.setBackgroundResource(R.drawable.helmet);
                Log.i("ais", "" + ais[1]);
            }
        });
        ai2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ais.length >= 3){
                    ais[2] = !ais[2];
                    if(ais[2])
                        ai2.setBackgroundResource(R.drawable.halus);
                    else
                        ai2.setBackgroundResource(R.drawable.helmet);
                    Log.i("ais", "" + ais[2]);
                }
            }
        });
    }
    /**Creates all the logic for deciding howe many players a game should have based on the position of the slider*/
    private void playerSelect(){
        ais = new boolean[players];
        playersTitle = findViewById(R.id.playersTitle);
        playerSelect = findViewById(R.id.playerSelect);
        playerSelect.setProgress(0);
        player0 = new ImageView(context);
        player0.setBackgroundResource(R.drawable.blue);
        player1 = new ImageView(context);
        player1.setBackgroundResource(R.drawable.red);
        player2 = new ImageView(context);
        player2.setBackgroundResource(R.drawable.green);

        createAiSelect();
        //manages how to place flags based on progressBar progress, also snapping the bar either to full or to zero
        int masterWidth = (int)(screenWidth*.5*.12);
        int masterHeight = (int)(screenHeight*.07*.5);
        final int layoutW = (int)(screenWidth*.6);
        int layoutH = (int)(screenHeight*.07);
        absoluteLayout.addView(player0, new LinearLayout.LayoutParams(masterWidth, masterHeight));
        absoluteLayout.addView(player1, new LinearLayout.LayoutParams(masterWidth, masterHeight));
        absoluteLayout.addView(player2, new LinearLayout.LayoutParams(masterWidth, masterHeight));
        absoluteLayout.addView(ai0, new LinearLayout.LayoutParams(masterWidth, masterHeight));
        absoluteLayout.addView(ai1, new LinearLayout.LayoutParams(masterWidth, masterHeight));
        absoluteLayout.addView(ai2, new LinearLayout.LayoutParams(masterWidth, masterHeight));
        player0.setX((float)(layoutW*.1));
        player1.setX((float)(layoutW*.7));
        player2.setX((float)(layoutW*1.3));

        ai0.setX((float)(layoutW*.1));
        ai1.setX((float)(layoutW*.7));
        ai2.setX((float)(layoutW*1.3));
        ai0.setY((float)(layoutH*.5));
        ai1.setY((float)(layoutH*.5));
        ai2.setY((float)(layoutH*.5));
        playerSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressNum = progress;
                players = (int) (progress/80) + 2;
                Log.i("build", "set " + players + " players");
                float one = (float)(.75*layoutW) - (float) (.22*layoutW*progress/50);
                float two = (float)(layoutW) - (float) (.25*layoutW*progress/50);
                player1.setX(one);
                player2.setX(two);
                ai1.setX(one);
                ai2.setX(two);
                ais = new boolean[players];
                ai0.setBackgroundResource(R.drawable.helmet);
                ai1.setBackgroundResource(R.drawable.helmet);
                ai2.setBackgroundResource(R.drawable.helmet);
                playersTitle.setText(players + " players");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playerSelect.setProgress(100 * (int) (progressNum/80.0));
                playersTitle.setText("" + players + " players");
            }
        });
    }
}
