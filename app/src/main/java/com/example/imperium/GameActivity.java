package com.example.imperium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
/**Activity that manages all game related things*/
public class GameActivity extends BuildActivity {
    private SharedPreferences prefs;
    /**Changes the stage of the turn the player is on or changes the player*/
    private static ImageButton change;
    /**Activates an attack or commits a transport*/
    private static ImageButton again;
    /**Ends an attack or transport*/
    private static ImageButton retreat;
    /**Automatically contunues attacking until enemy is annihilated*/
    private static ImageButton annihilate;
    /**Shows the current winner*/
    private static ImageView winCover;
    /**Background for the dice rolls*/
    private static ImageView rollsCover;
    /**Displays the result of the first attack die*/
    private static TextView aDie1;
    /**Displays the result of the second attack die*/
    private static TextView aDie2;
    /**Displays the result of the third attack die*/
    private static TextView aDie3;
    /**Displays the result of the first defense die*/
    private static TextView dDie1;
    /**Displays the result of the second defense die*/
    private static TextView dDie2;
    /**Shows the winner between the first attack and defense dies*/
    private static ImageView defeated;
    /**Shows the winner between the second attack and defense dies*/
    private static ImageView defeated2;
    /**Background to show flag of attacker*/
    private static ImageView attackerBackround;
    /**Background to show flag of defender*/
    private static ImageView defenderBackround;
    /**Displays the flag of the attacker*/
    private static ImageView attacker;
    /**Displays the flag of the defender*/
    private static ImageView defender;
    /**Displays the number of troops attacking*/
    private static TextView attackerTroops;
    /**Displays the number of troops defending*/
    private static TextView defenderTroops;
    /**Background for troop transporting*/
    private static ImageView slideCover;
    /**Seekbar used to determine how many troops to transport*/
    private static SeekBar slider;
    /**Image used as the thumb of the slider*/
    private static ImageView sliderImage;
    /**Displays the number of troops being transported*/
    private static TextView slideTroops;
    /**Background for the status text*/
    private static ImageView statusCover;
    /**Displays information about the status of the current player*/
    private static TextView status;
    /**Opens the nabBar*/
    public static ImageButton handle;
    /**Layout for the navBar*/
    private static ConstraintLayout navBar;
    /**Activates the save popup*/
    private static ImageButton saver;
    /**Quits ot the main menu*/
    private static ImageButton quitter;
    /**Contains the info text*/
    private static ConstraintLayout info;
    /**Shows information about the current province*/
    private static TextView infoText;
    /**Closes the info*/
    private static ImageButton closeInfo;
    /**Layout that holds the save components*/
    private static ConstraintLayout saveMaker;
    /**Part of save popup that saves current state of game*/
    private static ImageButton saveOK;
    /**Part of save popup that exits the popup*/
    private static ImageButton saveCancel;
    /**Allows user to input the name of a save*/
    private static EditText saveInput;
    /**Layout that contains the map*/
    private static RelativeLayout mapLayout;
    /**Current game*/
    private static Game game;
    /**Map of current game*/
    protected static Map map;
    /**Stores weather navBar is open*/
    private static boolean openNav;
    /**Stores weather info is open*/
    private static boolean openInfo;
    /**Stores weather save popup is open*/
    private static boolean openSave;
    /**Stores the scale detector for the map*/
    private ScaleGestureDetector scaleGestureDetector;
    /**Stores weather a player can interract with provinces*/
    private static boolean provEnabled;
    /**Current zoom of the map*/
    private float zoomFactor = 0.9f;
    /**Stores the y change of a user drag on the map*/
    private static float dY;
    /**Stores the x change of a user drag on the map*/
    private static float dX;
    /**Stores where the user first touched the map*/
    private static Point down;
    /**Saves the time the user first touched them map*/
    private static long downtime;
    /**The string representation of the game to be saved*/
    private String saveString;
    /**The string representation of the game to be loaded*/
    private String loadString;
    /**Android context of application*/
    private static Context context;

    /**Initializes views and variables*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        provEnabled = true;
        context = this;
        assignViews();
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        openInfo = false;
        openNav = false;
        openSave = false;
        prefs = context.getSharedPreferences(SAVED_GAME_ID, MODE_PRIVATE);
        createGame();
        autosave();
        drawer();
        info();
        tinter();
        saveMaker();
        Log.i("game", "Players: " + getIntent().getIntExtra("players", 1) + " Map: " + getIntent().getIntExtra("mapId", 0));
    }
    /**Saves game and quits activity*/
    @Override
    public void onDestroy() {
        Log.i("onDestron", "ded");
        saveGame(SAVED_GAME_ID);
        finish();
        super.onDestroy();
    }
    /**Goes directly to mainActivity*/
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }
    /**Either creates a new game or creates a game from a given string to load*/
    private void createGame(){
        if(getIntent().getStringExtra("tag").equals("loaded")) {
            loadString = getIntent().getStringExtra("loadedGame");
            Log.i("loadedString", "String: " + loadString);
            game = loadBuilder();
            game.maker();
            Log.i("loadedGame", "");
            Toast.makeText(context, "Loaded " + getFilesDir() + "/" + getIntent().getStringExtra("loadName"), Toast.LENGTH_SHORT).show();
        }
        else if(getIntent().getStringExtra("tag").equals("new")){
            game = new Game(this, getIntent().getIntExtra("players", 1), getIntent().getIntExtra("mapId", 0), getIntent().getBooleanArrayExtra("ais"));
            map = game.getMap();
            Log.i("newGame", "" + game);
        }
    }
    /**Initializes views from the layout xml file*/
    private void assignViews(){
        mapLayout = findViewById(R.id.map);
        mapLayout.setScaleX(zoomFactor);
        mapLayout.setScaleY(zoomFactor);
        navBar = findViewById(R.id.navBar);
        saveMaker = findViewById(R.id.savePopup);
        saveInput = findViewById(R.id.saveInput);
        saveOK = findViewById(R.id.saveOK);
        saveCancel = findViewById(R.id.saveCancel);
        info = findViewById(R.id.info);
        closeInfo = findViewById(R.id.closeInfo);
        handle = findViewById(R.id.drawerHandle);
        infoText = findViewById(R.id.infoText);
        infoText.setTextSize(TypedValue.COMPLEX_UNIT_IN,BASE_TEXT_SCALE*inchWidth);
        saver = findViewById(R.id.saver);
        quitter = findViewById(R.id.quitter);
        attackerBackround = findViewById(R.id.attackerBackround);
        defenderBackround = findViewById(R.id.defenderBackround);
        attacker = findViewById(R.id.attacker);
        defender = findViewById(R.id.defender);
        defenderTroops = findViewById(R.id.defenderTroops);
        attackerTroops = findViewById(R.id.attackerTroops);
        defeated = findViewById(R.id.defeated);
        defeated2 = findViewById(R.id.defeated2);
        winCover = findViewById(R.id.winCover);
        status = findViewById(R.id.status);
        rollsCover = findViewById(R.id.rollsCover);
        slideCover = findViewById(R.id.slideCover);
        statusCover = findViewById(R.id.statusCover);
        annihilate = findViewById(R.id.annihilate);
        again = findViewById(R.id.again);
        retreat = findViewById(R.id.retreat);
        slider = findViewById(R.id.slide);
        sliderImage = findViewById(R.id.sliderImage);
        change = findViewById(R.id.change);
        slideTroops = findViewById(R.id.slideTroops);
        aDie1 = findViewById(R.id.aDie1);
        aDie2 = findViewById(R.id.aDie2);
        aDie3 = findViewById(R.id.aDie3);
        dDie1 = findViewById(R.id.dDie1);
        dDie2 = findViewById(R.id.dDie2);
    }
    //Getters
    /**@return game the current game*/
    public static Game getGame(){return game;}
    /**@return provEnabled*/
    public static boolean getProvEnabled(){return provEnabled;}
    /**@return mapLayout the layout that contains them ap and provinces*/
    public static RelativeLayout getMapLayout(){return mapLayout;}
    /**@return winCover*/
    public static ImageView getWinCover(){return winCover;}
    /**@return statusCover the statusCover component*/
    public static ImageView getStatusCover(){return statusCover;}
    /**@return attackerBackround the attackerBackround component*/
    public static ImageView getAttackerBackround(){return attackerBackround;}
    /**@return defenderBackround the defenderBackround component*/
    public static ImageView getDefenderBackround(){return defenderBackround;}
    /**@return attacker the attacker component*/
    public static ImageView getAttacker(){return attacker;}
    /**@return defender the defender component*/
    public static ImageView getDefender(){return defender;}
    /**@return defeated the defeated component*/
    public static ImageView getDefeated(){return defeated;}
    /**@return defeated2 the defeated2 component*/
    public static ImageView getDefeated2(){return defeated2;}
    /**@return slideCover the slideCover component*/
    public static ImageView getSlideCover(){return slideCover;}
    /**@return change the change component*/
    public static ImageButton getChange(){return change;}
    /**@return again the again component*/
    public static ImageButton getAgain(){return again;}
    /**@return annihilate the annihilate component*/
    public static ImageButton getAnnihilate(){return annihilate;}
    /**@return retreat the retreat component*/
    public static ImageButton getRetreat(){return retreat;}
    /**@return rollsCover the rollsCover component*/
    public static ImageView getRollsCover(){return rollsCover;}
    /**@return defenderTroops the defenderTroops component*/
    public static TextView getDefenderTroops(){return defenderTroops;}
    /**@return attackerTroops the attackerTroops component*/
    public static TextView getAttackerTroops(){return attackerTroops;}
    /**@return aDie1 the aDie1 component*/
    public static TextView getaDie1(){return aDie1;}
    /**@return aDie2 the aDie2 component*/
    public static TextView getaDie2(){return aDie2;}
    /**@return aDie3 the aDie3 component*/
    public static TextView getaDie3(){return aDie3;}
    /**@return dDie1 the dDie1 component*/
    public static TextView getdDie1(){return dDie1;}
    /**@return dDie2 the dDie2 component*/
    public static TextView getdDie2(){return dDie2;}
    /**@return slider the slider component*/
    public static SeekBar getSlider(){return slider;}
    /**@return slider the slider thumb component*/
    public static ImageView getSliderImage(){return sliderImage;}
    /**@return slideTroops the slideTroops component*/
    public static TextView getSlideTroops(){return slideTroops;}
    /**@return status the status component*/
    public static TextView getStatus(){return status;}

    /**@param set Sets the provEnabled field*/
    public static void setProvEnabled(boolean set){provEnabled = set;}
    /**@param prov
     * Executes the opening animation of th info layout and updates the information to match that of prov*/
    public void showInfo(Province prov){
        Log.i("showInfo", "open: " + openInfo + "from " + prov.getName());
        String infoStr = "";
        if(!openInfo){
            info.setVisibility(View.VISIBLE);
            infoStr += "Province: "+prov.getName()+"\nTroops: "+prov.modTroops(0);
            infoStr += "\n\nContinent: " + prov.getContinent().getName() + "\nBonus: " + prov.getContinent().getBonus();
            infoText.setText(infoStr);
            info.animate().x(0).setDuration(500).start();
        }
        openInfo = true;
    }
    /**Overrides onTouchEvent to use scaleGestureDetector*/
    public boolean onTouchEvent(MotionEvent motionEvent) {
        scaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    /**Initializes the components for the save popup*/
    private void saveMaker(){
        saveOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.saveString();
                if(saveInput.getText().toString().equals("") || saveInput.getText().toString().equals("autosave"))
                    Toast.makeText(context, "Type a new save name", Toast.LENGTH_SHORT);
                else {
                    saveGame(saveInput.getText().toString() + ".txt");
                    saveInput.setText("autosave");
                    saveMaker.animate().y(screenWidth+300).setDuration(500).start();
                    openSave = false;
                }
            }
        });
        saveCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInput.setText("autosave");
                saveMaker.animate().y(screenWidth+300).setDuration(500).start();
                openSave = false;
            }
        });
    }
    /**Initializes the components for the info layout*/
    private void info() {
        closeInfo.setBackgroundResource(R.drawable.closenav);
        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info.animate().x(info.getX() - info.getMeasuredWidth()).setDuration(500).start();
                openInfo = false;
                Log.i("info", "open: " + openInfo);
            }
        });
    }
    /**Initializes the components for the navBar layout*/
    private void drawer(){
        handle.setBackgroundResource(R.drawable.opennav);
        handle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNav = !openNav;
                if(openNav){
                    handle.setBackgroundResource(R.drawable.opennav);
                    handle.animate().x(handle.getX()-navBar.getMeasuredWidth()).setDuration(500).start();
                    navBar.animate().x(navBar.getX()-navBar.getMeasuredWidth()).setDuration(500).start();
                }
                else{
                    handle.setBackgroundResource(R.drawable.closenav);
                    handle.animate().x(handle.getX()+navBar.getMeasuredWidth()).setDuration(500).start();
                    navBar.animate().x(navBar.getX()+navBar.getMeasuredWidth()).setDuration(500).start();
                }
            }
        });
        saver.setBackgroundResource(R.drawable.navsave);
        saver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("save", "open: " + openSave);
                if(!openInfo)
                    saveMaker.animate().y(screenWidth/2).setDuration(500).start();
                openSave = true;
            }
        });
        quitter.setBackgroundResource(R.drawable.navquit);
        quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }
    /**Converts loadString into a game object
     * @return builder the loaded game*/
    private Game loadBuilder(){
        if(get(0, 1) == 0) map = new Classic(context);
        else map = new Classic(context);
        Game building = new Game(context, get(4+4*map.getList().length, 5+4*map.getList().length), get(0, 1));
        building.setTurnNum(get(0, 3));
        for(int i=0; i<building.getMap().getList().length; i++){
            building.getMap().getList()[i].updatePress(get(4+4*i, 5+4*i));
            building.getMap().getList()[i].modTroops(get(5+4*i, 8+4*i));
        }
        Log.i("building", "player strt");
        Log.i("building", "difference: " + (loadString.length()-(7+4*map.getList().length+3*building.getPlayerList().length)));
        for(int i=0; i<get(4+4*map.getList().length, 5+4*map.getList().length); i++){
            Log.i("buiding", "\""+get(i+(6+4*map.getList().length+3*building.getPlayerList().length), i+(7+4*map.getList().length+3*building.getPlayerList().length))+"\"");
            if(get(i+(6+4*map.getList().length+3*building.getPlayerList().length), i+(7+4*map.getList().length+3*building.getPlayerList().length)) == 0) {
                building.getPlayerList()[i] = new Player(context, i);
            }
            else building.getPlayerList()[i] = new Ai(context, i);
        }
        Log.i("building", "playerTings");
        for(int i=0; i<building.getPlayerList().length; i++){
            building.getPlayerList()[i].setStage(get(5+4*map.getList().length+3*i, 6+4*map.getList().length+3*i));
            building.getPlayerList()[i].setTroops(get(6+4*map.getList().length+3*i, 8+4*map.getList().length+3*i));
            Log.i("building", "playerModded: " + get(6+4*map.getList().length+3*i, 8+4*map.getList().length+3*i));
        }
        building.setCurrentPlayer(get(5+4*map.getList().length+3*building.getPlayerList().length, 6+4*map.getList().length+3*building.getPlayerList().length));
        return building;
    }
    /**Converts a length of loadString into an integer*/
    private int get(int start, int end){
        if(loadString.substring(start, end).equals("n"))
            return -1;
        return Integer.parseInt(loadString.substring(start, end));
    }
    /**Periodically saves the game*/
    private void autosave(){
        saveString = game.saveString();
        Thread lookingThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                saveString = game.saveString();
                                //Log.i("autosave", "saved: " + saveString.toString());
                            }
                        });
                    }
                } catch (InterruptedException e) { }
            }
        };
        lookingThread.start();
    }
    /**Class that scales the map when gestured as well as showing the status of the provinces at a certain zoom level*/
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            zoomFactor *= scaleGestureDetector.getScaleFactor();
            zoomFactor = Math.max(0.1f, Math.min(zoomFactor, 3.0f));
            mapLayout.setScaleX(zoomFactor);
            mapLayout.setScaleY(zoomFactor);
            if(zoomFactor < 1)
                for(Province p : map.getList())
                    p.ownerVis(false);
            else
                for(Province p : map.getList())
                    p.ownerVis(true);
            return true;
        }
    }
    /**Writes the output of game.saveString() to a file then closes it*/
    private void saveGame(String saveId){
        FileOutputStream fos;
        File save = new File(SAVE_PATH, saveId);
        saveString = game.saveString();
        try {
            fos = new FileOutputStream(save);
            fos.write(saveString.getBytes());
            fos.close();
        } catch (IOException e) { e.printStackTrace(); }

        try{ Toast.makeText(context, "Saved to " + SAVE_PATH + "/" + saveId, Toast.LENGTH_LONG).show();
        }catch (RuntimeException e){e.printStackTrace();}
    }
    /**Logic to either drag the entire map of to decide which province the user has tapped*/
    @SuppressLint("ClickableViewAccessibility")
    protected static void touched() {
        mapLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            dX = view.getX() - event.getRawX();
                            dY = view.getY() - event.getRawY();
                            down = new Point((int)event.getRawX(), (int)event.getRawY());
                            downtime = System.currentTimeMillis();
                            break;
                        case MotionEvent.ACTION_UP:
                            try {
                                if (Math.abs(down.x - event.getRawX()) < 30 && Math.abs(down.y - event.getRawY()) < 30) {
                                    ArrayList<Province> choices = new ArrayList<>(0);
                                    for (int i = 0; i < map.getList().length; i++) {
                                        Province at = map.getList()[i];
                                        Bitmap overlay = at.getOverlay();
                                        if (event.getX() > at.getX() && event.getX() < at.getX() + overlay.getWidth() * map.getOverScale()
                                                && event.getY() > at.getY() && event.getY() < at.getY() + overlay.getHeight() * map.getOverScale()) {
                                            try {
                                                if (Color.alpha(overlay.getPixel((int) ((event.getX() - at.getX()) / map.getOverScale()), (int) ((event.getY() - at.getY()) / map.getOverScale()))) > 10)
                                                    choices.add(at);
                                            } catch (IllegalArgumentException e) { e.printStackTrace(); }
                                        }
                                    }
                                    int minDist = Integer.MAX_VALUE;
                                    Province touched = null;
                                    if (choices.size() == 1) touched = choices.get(0);
                                    else if (choices.size() > 1)
                                        for (int i = 0; i < choices.size(); i++)
                                            if (Math.abs(event.getX() - choices.get(i).getX()) < minDist) {
                                                minDist = (int) Math.abs(event.getX() - choices.get(i).getX());
                                                touched = choices.get(i);
                                            }
                                    if (System.currentTimeMillis() - downtime > 300 && touched != null)
                                        touched.doLongClick();
                                    else if (touched != null && provEnabled) touched.doClick();
                                }
                            }catch (NullPointerException e){e.printStackTrace();}
                            break;
                        case MotionEvent.ACTION_MOVE:
                            view.animate().x(event.getRawX() + dX).y(event.getRawY() + dY).setDuration(0).start();
                            break;
                        default: return false;
                    }
                } else return false;
                return true;
            }
        });
    }
}

