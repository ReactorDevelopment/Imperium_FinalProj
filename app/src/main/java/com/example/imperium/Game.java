package com.example.imperium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import static android.graphics.Color.*;

public class Game extends GameActivity implements java.io.Serializable {
    private Player wiener;
    private static int currentPlayer = 0;
    private int slideValue;
    private static Player[] players;
    private static int slideProgress;
    private static int turnNum;
    private static boolean ran;
    private static boolean transporting;
    private static boolean attacking;
    private static Map map;
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
    /**Displays the number of troops being transported*/
    private static TextView slideTroops;
    /**Image used as the thumb of the slider*/
    private static ImageView sliderImage;
    /**Background for the status text*/
    private static ImageView statusCover;
    /**Displays information about the status of the current player*/
    private static TextView status;
    /**Array contialing which players are controlled by computers*/
    private static boolean[] ais;
    /**The number of players in a game*/
    private static int numPlayers;
    /**Application context*/
    private transient Context context;

    /**Constructor for a blank game*/
    public Game(){}
    /**Constructor used for loading a game*/
    public Game(Context context, int numPlayers, int mapId){
        this.context = context;
        turnNum = 1;
        transporting = false;
        attacking = false;
        ran = false;
        slideValue = 0;
        map = new Classic(context);
        map.place();
        players = new Player[numPlayers];
        Log.i("playersSize", "players: " + numPlayers);
    }
    /**Constructor used for creating a new game*/
    public Game(Context context, int numPlayers, int mapId, boolean[] ais){
        this(context, numPlayers, mapId);
        this.numPlayers = numPlayers;
        this.ais = ais;
        touched();
        rollOut(new int[5]);
        changer();
        again();
        retreat();
        annihilate();
        slide();
        update();
        endAttack();
        endTransport();
        addPlayers(numPlayers, ais);
        players[0].turn();
    }
    /**Called after a loaded game is created to initialize its components*/
    public void maker(){
        this.ais =reverseAis();
        touched();
        rollOut(new int[5]);
        changer();
        again();
        retreat();
        annihilate();
        slide();
        update();
        endAttack();
        endTransport();
        players[0].turn();
    }
    /**Thread that updates the status text, checks for an attack, a transport, or a winner*/
    private void update(){
        status = getStatus();
        status.setTextSize(TypedValue.COMPLEX_UNIT_IN,BASE_TEXT_SCALE*inchWidth);
        statusCover = getStatusCover();
        statusCover.setBackgroundResource(R.drawable.statusblue);
        Thread lookingThread = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(getCurrentPlayer().attackScan() && getCurrentPlayer().getStage() == 1 && !attacking)
                                    startAttack();
                                else if(!getCurrentPlayer().attackScan() && getCurrentPlayer().getStage() == 1 && attacking)
                                    endAttack();
                                if(getCurrentPlayer().getStage() == 2){
                                    if (getCurrentPlayer().transportScan() && !transporting)
                                    startTransport();
                                    else if(!getCurrentPlayer().transportScan() && transporting) {
                                        Log.i("transport", "lookEnd");
                                        endTransport();
                                    }
                                }
                                if(getCurrentPlayer().attackScan()){
                                    attackerTroops.setText("" + getCurrentPlayer().getSelected()[0].modTroops(0));
                                    defenderTroops.setText("" + getCurrentPlayer().getSelected()[1].modTroops(0));
                                }
                                if(getCurrentPlayer().getStage() == 1)
                                    change.setBackgroundResource(R.drawable.endattack);
                                if(getCurrentPlayer().select(0) > 2){
                                    getCurrentPlayer().select(-getCurrentPlayer().select(0) + 2);
                                }
                                status.setText("Player: " + currentPlayer + ", Stage: " + getCurrentPlayer().getStage()
                                        + ", Reinforcements: " +  getCurrentPlayer().modTroops(0)
                                        + ", Infamy: " + (int)(getCurrentPlayer().getInfamy()*100)/100.0);
                                inBounds();
                                //status.bringToFront();
                                slider.bringToFront();
                                map.updateAllOverlays();
                                chickenDinner();
                                updateProvinces();
                            }
                        });
                    }
                } catch (InterruptedException e) { }
            }
        };
        lookingThread.start();
    }
    /**Checks if the game has been won then displays the winCover as a banner*/
    private void chickenDinner(){
        winCover = getWinCover();
        winCover.setVisibility(View.INVISIBLE);
        if(map.allOwned(players[0])) {
            wiener = players[0];
            winCover.setBackgroundResource(R.drawable.winnerblue);
        }
        else if(map.allOwned(players[1])) {
            wiener = players[1];
            winCover.setBackgroundResource(R.drawable.winnerred);
        }
        if(players.length >= 3){
            if(map.allOwned(players[2])) {
                wiener = players[2];
                winCover.setBackgroundResource(R.drawable.winnergreen);
            }
        }
        if(wiener != null) {
            setProvEnabled(false);
            changeAllSelection(false);
            endAttack();
            endTransport();
            getCurrentPlayer().setStage(69);
            winCover.setVisibility(View.VISIBLE);
        }
    }
    /**Components for switching from one player to another*/
    private void toStageZero(){
        Log.i("switchinh", "tostagezero");
        endTransport();
        slideValue = 0;
        changePlayer(true);
        change.setBackgroundResource(R.drawable.endplacement);
        again.setBackgroundColor(TRANSPARENT);
        again.setVisibility(View.INVISIBLE);
        changeAllSelection(false);

    }
    /**Refreshes the text on each province*/
    private void updateProvinces(){
        for(int i=0; i<map.getList().length; i++) {
            if(map.getList()[i].modTroops(0) > 0)
                map.getList()[i].setText("" + map.getList()[i].modTroops(0));
        }
    }
    /**Handles the logic for changer, mostly what to do at each stage to move to the next one*/
    private void changer(){
        change = getChange();
        change.setBackgroundResource(R.drawable.endplacement);
        change.setId(0);
        change.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(getCurrentPlayer().getStage() == 0 && getCurrentPlayer().modTroops(0) == 0) {
                    getCurrentPlayer().setStage(1);
                }
                else if(getCurrentPlayer().getStage() == 1) {
                    getCurrentPlayer().setStage(2);
                    change.setBackgroundResource(R.drawable.endtransport);
                    changeAllSelection(false);
                }
                else if(getCurrentPlayer().getStage() == 2) {
                    toStageZero();
                }
            }
        });
    }
    /**Handles the logic for again, mostly executing attacks and transports*/
    @SuppressLint("ResourceType")
    private void again(){
        again = getAgain();
        again.setBackgroundColor(TRANSPARENT);
        again.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("again", "presses");
                rollOut(new int[5]);
                if(getCurrentPlayer().getStage() == 1)
                    rollOut(getCurrentPlayer().attack());
                if(getCurrentPlayer().getStage() == 2) {
                    getCurrentPlayer().transport(slideValue);
                    toStageZero();
                }
                if(getCurrentPlayer().getStage() == 1 && transporting && ran) {
                    Log.i("transporting", "againEnded, Ran? " + ran);
                    getCurrentPlayer().transport(slideValue);
                }
                if(getCurrentPlayer().getStage() == 1 && transporting)
                    ran = true;
            }
        });
    }
    /**Handles the logic for retreating, closes the transport or attack screens*/
    @SuppressLint("ResourceType")
    private void retreat(){
        retreat = getRetreat();
        retreat.setBackgroundColor(TRANSPARENT);
        retreat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                changeAllSelection(false);
                getCurrentPlayer().select(2);
                if(getCurrentPlayer().getStage() == 1 && transporting) {
                    Log.i("transporting", "retreatEnd");
                    endTransport();
                }
                else if(getCurrentPlayer().getStage() == 1)
                    endAttack();
                Log.i("Retrear", "Retreated");
            }
        });
    }
    /**Handles the logic for annihilate, loops through attacking until the other province is invaded or the attacker is exhausted*/
    @SuppressLint("ResourceType")
    private void annihilate(){
        annihilate = getAnnihilate();
        annihilate.setBackgroundResource(R.drawable.annihilate);
        annihilate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                while(getCurrentPlayer().getSelected()[0].modTroops(0) > 1 && getCurrentPlayer().getSelected()[1].modTroops(0) > 1)
                    rollOut(getCurrentPlayer().attack());
            }
        });
    }
    /**Logic for determining how do distribute troops to move depending on the progress of the slider,
     * giving the player a readout of how many troops will be transported where*/
    private void slide(){
        slider = getSlider();
        slideTroops = getSlideTroops();
        slideCover = getSlideCover();
        sliderImage = getSliderImage();
        slideProgress = 0;
        sliderImage.setX(50);
        slider.setProgress(50);
        sliderImage.setVisibility(View.INVISIBLE);
        Log.i("slide", "created");
        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                getCurrentPlayer().transportScan();
                slideProgress = progress;
                sliderImage.setX(progress);
                sliderImage.bringToFront();
                int add0 = 0;
                int add1 = 0;
                if(getCurrentPlayer().getStage() == 2) {
                    if (progress < 50) {
                        slideValue = (int) ((getCurrentPlayer().getSelected()[1].modTroops(0) - 1) * (1 - progress / 50.0));
                        slideTroops.setText("" + slideValue + " troops to " + getCurrentPlayer().getSelected()[0].getName());
                    }
                    if (progress >= 50) {
                        slideValue = (int) ((getCurrentPlayer().getSelected()[0].modTroops(0) - 1) * (progress - 50) / 50.0);
                        slideTroops.setText("" + slideValue + " troops to " + getCurrentPlayer().getSelected()[1].getName());
                    }
                }
                if(getCurrentPlayer().getStage() == 1) {
                    if(getCurrentPlayer().getSelected()[1].modTroops(0) == 3){
                        add1 = 2;
                    }
                    else if(getCurrentPlayer().getSelected()[0].modTroops(0) == 3) {
                        add0 = 2;
                    }
                    if(progress < 50) {
                        slideValue = (int) ((getCurrentPlayer().getSelected()[1].modTroops(0) - (1 + add1)) * (1 - progress / 50.0));
                        slideTroops.setText("" + slideValue + " troops to " + getCurrentPlayer().getSelected()[0].getName());
                    }
                    if(progress >= 50) {
                        slideValue = (int) ((getCurrentPlayer().getSelected()[0].modTroops(0) - (1 + add0)) * (progress - 50) / 50.0);
                        slideTroops.setText("" + slideValue + " troops to " + getCurrentPlayer().getSelected()[1].getName());
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    /**Refreshes the die textViews depending on the inputted rolls, as well as initializing related views*/
    private void rollOut(int[] rolls){
        attackerBackround = getAttackerBackround();
        defenderBackround = getDefenderBackround();
        attacker = getAttacker();
        defender = getDefender();
        attackerTroops = getAttackerTroops();
        defenderTroops = getDefenderTroops();
        rollsCover = getRollsCover();
        aDie1 = getaDie1();
        aDie2 = getaDie2();
        aDie3 = getaDie3();
        dDie1 = getdDie1();
        dDie2 = getdDie2();
        defeated = getDefeated();
        defeated2 = getDefeated2();
        defeated.setBackgroundResource(R.drawable.defeated);
        defeated2.setBackgroundResource(R.drawable.defeated);
        aDie1.setText("" + rolls[0]);
        aDie2.setText("" + rolls[1]);
        aDie3.setText("" + rolls[2]);
        dDie1.setText("" + rolls[3]);
        dDie2.setText("" + rolls[4]);
        if(rolls[0] > rolls[3])
            defeated.setRotation(0);
        else if(rolls[0] < rolls[3])
            defeated.setRotation(180);
        else
            defeated.setBackgroundResource(R.drawable.tie);
        if(rolls[1] > rolls[4])
            defeated2.setRotation(0);
        else if(rolls[1] < rolls[4])
            defeated2.setRotation(180);
        else
            defeated2.setBackgroundResource(R.drawable.tie);
    }
    /**Animates the map back onscreen if it ever goes off*/
    private void inBounds(){
        double vector = Math.sqrt(Math.pow(getMapLayout().getX(), 2) + Math.pow(getMapLayout().getY(), 2));
        if(getMapLayout().getScaleX() < 1.3f)
            getMapLayout().animate().x(-30).y(-22).setDuration((long) (2000 * Math.pow(getMapLayout().getScaleX(), 2) / Math.sqrt(vector)));
    }
    /**Saves the current state for the troops in a province*/
    /*private void saveAllTroops(){
        for (int i=0; i<map.getList().length; i++)
            getMap().getList()[i].saveTroops();
    }*/
    /**Used to convert the ais array to a string to be saved*/
    private String aisToString(){
        String list = "";
        for(int i=0; i<ais.length; i++){
            if(ais[i]) list += "1";
            else list += "0";
        }
        return list;
    }
    /**Creates the ais array from the current players array*/
    private boolean[] reverseAis(){
        boolean[] ais = new boolean[players.length];
        for(int i=0; i<ais.length; i++){
            if(players[i].isHuman()) ais[i] = false;
            else ais[i] = true;
        }
        return ais;
    }
    
    //public
    /**@return currentPlayer the player which is currently in a turn*/
    public Player getCurrentPlayer() { return players[currentPlayer]; }
    /**@return turnNum the current turn*/
    public int getTurnNum(){return turnNum;}
    /**@param set the current turn*/
    public void setTurnNum(int set){turnNum = set;}
    /**@return players the array of players in a game*/
    public Player[] getPlayerList(){return players;}
    /**@return map the map of the current game*/
    public static Map getMap() { return map; }
    /**@return slideProgress the current progress of slider*/
    public static int getSlideProgress(){ return slideProgress; }
    /**@param set the current player*/
    public void setCurrentPlayer(int set){currentPlayer = set;}
    /**Logic for switching from one players turn to another,  adding reinforcements to players if the turn is appropriate*/
    public void changePlayer(boolean adding){
        if(currentPlayer < players.length-1)
            currentPlayer++;
        else
            currentPlayer = 0;
        getCurrentPlayer().setStage(-1);
        if(adding) {
            getCurrentPlayer().modTroops(3 + map.bonuses(getCurrentPlayer()) + (int) getCurrentPlayer().getInfamy());
            getCurrentPlayer().setStage(0);
        }
        if(getCurrentPlayer().getId() == 0)
            statusCover.setBackgroundResource(R.drawable.statusblue);
        if(getCurrentPlayer().getId() == 1)
            statusCover.setBackgroundResource(R.drawable.statusred);
        if(getCurrentPlayer().getId() == 2)
            statusCover.setBackgroundResource(R.drawable.statusgreen);
        turnNum++;
        Log.i("Turn Num", "" + turnNum);
        getCurrentPlayer().turn();
    }
    /**@param set the selection of each province in the map*/
    public void changeAllSelection(boolean set){
        for(int i=0; i<map.getList().length; i++){
            map.getList()[i].setSelected(set);
        }
    }
    /**@return saveString the string representation of the game*/
    public String saveString(){
        String save = "";
        //Log.i("gameSave0", "saved: " + save);
        save += "" + map.getId();
        //Log.i("gameSave1", "saved: " + save);
        if(turnNum<=9) save += "00"+turnNum;
        else if(turnNum<=99) save += "0"+turnNum;
        else if(turnNum>99) save += ""+turnNum;
        //Log.i("gameSave2", "saved: " + save);
        for(int i=0; i<map.getList().length; i++){
            Province pAt = map.getList()[i];
            save += ""+pAt.getOwnerId();
            //Log.i("gameSave3", "saved: " + save);
            if(pAt.modTroops(0)<=9) save += "00"+pAt.modTroops(0);
            else if(pAt.modTroops(0)<=99) save += "0"+pAt.modTroops(0);
            else if(pAt.modTroops(0)>99) save += ""+pAt.modTroops(0);
            //Log.i("gameSave4", "saved: " + save);
        }
        save += players.length;
        for(int i=0; i<players.length; i++){
            Player plAt = players[i];
            save+=""+plAt.getStage();
            if(plAt.modTroops(0)<=9) save += "0"+plAt.modTroops(0);
            else if(plAt.modTroops(0)>9) save += ""+plAt.modTroops(0);
        }
        save += currentPlayer;
        save += aisToString();
        //Log.i("gameSave", "saved: " + save);
        for(int i=0; i<save.length()-1; i++){
            if(save.substring(i, i+2).equals("-1")) {
                save = save.substring(0,i) + "n" + save.substring(i+2);
            }
        }
        Log.i("gameSave", "saved: " + save);
        return save;
    }
    /**Creates the players array with numPlayers and computer players assigned based on @param ais*/
    public void addPlayers(int numPlayers, boolean[] ais){
        for(int i=0; i<numPlayers; i++) {
            players[i] = new Player(context, i);
            if(ais[i])
                players[i] = new Ai(context, i);
        }
    }
    /**Shows the transport components*/
    public static void startTransport(){
        ran = false;
        defeated.setVisibility(View.INVISIBLE);
        defeated2.setVisibility(View.INVISIBLE);
        rollsCover.setVisibility(View.INVISIBLE);
        again.setVisibility(View.VISIBLE);
        retreat.setVisibility(View.VISIBLE);
        slider.setVisibility(View.VISIBLE);
        slideCover.setVisibility(View.VISIBLE);
        sliderImage.setVisibility(View.VISIBLE);
        attackerBackround.setVisibility(View.INVISIBLE);
        defenderBackround.setVisibility(View.INVISIBLE);
        attacker.setVisibility(View.INVISIBLE);
        defender.setVisibility(View.INVISIBLE);
        attackerTroops.setVisibility(View.INVISIBLE);
        defenderTroops.setVisibility(View.INVISIBLE);
        change.setVisibility(View.INVISIBLE);
        annihilate.setVisibility(View.INVISIBLE);
        //slider.setEnabled(true);
        slider.setProgress(50);
        sliderImage.setX(50);
        //again.setEnabled(true);
        again.setBackgroundResource(R.drawable.transport);
        retreat.setEnabled(true);
        retreat.setBackgroundResource(R.drawable.done);
        setProvEnabled(false);
        transporting = true;
        attacking = false;
        Log.i("Transport", "Start");
    }
    /**Hides the transport components*/
    public void endTransport(){
        Log.i("transport", "ending");
        ran = false;
        slider.setVisibility(View.INVISIBLE);
        //slider.setEnabled(false);
        slideTroops.setText("");
        again.setVisibility(View.INVISIBLE);
        //again.setEnabled(false);
        retreat.setVisibility(View.INVISIBLE);
        //retreat.setEnabled(false);
        slideCover.setVisibility(View.INVISIBLE);
        setProvEnabled(true);
        sliderImage.setVisibility(View.INVISIBLE);
        aDie1.setVisibility(View.INVISIBLE);
        aDie2.setVisibility(View.INVISIBLE);
        aDie3.setVisibility(View.INVISIBLE);
        dDie1.setVisibility(View.INVISIBLE);
        dDie2.setVisibility(View.INVISIBLE);
        change.setVisibility(View.VISIBLE);
        transporting = false;
    }
    /**Shows the attack components*/
    public void startAttack(){
        again.setBackgroundColor(LTGRAY);
        //again.setEnabled(true);
        again.setBackgroundResource(R.drawable.attack);
        retreat.setBackgroundColor(LTGRAY);
        //retreat.setEnabled(true);
        retreat.setBackgroundResource(R.drawable.retreat);
        annihilate.setVisibility(View.VISIBLE);
        setProvEnabled(false);
        attackerBackround.setVisibility(View.VISIBLE);
        defenderBackround.setVisibility(View.VISIBLE);
        attacker.setVisibility(View.VISIBLE);
        defender.setVisibility(View.VISIBLE);
        attackerTroops.setVisibility(View.VISIBLE);
        defenderTroops.setVisibility(View.VISIBLE);
        defeated.setVisibility(View.VISIBLE);
        defeated2.setVisibility(View.VISIBLE);
        again.setVisibility(View.VISIBLE);
        retreat.setVisibility(View.VISIBLE);
        change.setVisibility(View.INVISIBLE);
        aDie1.setVisibility(View.VISIBLE);
        aDie2.setVisibility(View.VISIBLE);
        aDie3.setVisibility(View.VISIBLE);
        dDie1.setVisibility(View.VISIBLE);
        dDie2.setVisibility(View.VISIBLE);
        rollsCover.setVisibility(View.VISIBLE);
        annihilate.setVisibility(View.VISIBLE);
        attacking = true;
        if(getCurrentPlayer().getId() == 0)
            attacker.setBackgroundResource(R.drawable.blue);
        if(getCurrentPlayer().getId() == 1)
            attacker.setBackgroundResource(R.drawable.red);
        if(getCurrentPlayer().getId() == 2)
            attacker.setBackgroundResource(R.drawable.green);
        if(getCurrentPlayer().getSelected()[1].getOwnerId() == 0)
            defender.setBackgroundResource(R.drawable.blue);
        if(getCurrentPlayer().getSelected()[1].getOwnerId() == 1)
            defender.setBackgroundResource(R.drawable.red);
        if(getCurrentPlayer().getSelected()[1].getOwnerId() == 2)
            defender.setBackgroundResource(R.drawable.green);
    }
    /**Hides the attack components*/
    public static void endAttack(){
        again.setBackgroundColor(TRANSPARENT);
        //again.setEnabled(false);
        //again.setActivated(false);
        again.setVisibility(View.INVISIBLE);
        retreat.setBackgroundColor(TRANSPARENT);
        //retreat.setEnabled(false);
        retreat.setVisibility(View.INVISIBLE);
        annihilate.setVisibility(View.INVISIBLE);
        setProvEnabled(true);
        attackerBackround.setVisibility(View.INVISIBLE);
        defenderBackround.setVisibility(View.INVISIBLE);
        attacker.setVisibility(View.INVISIBLE);
        defender.setVisibility(View.INVISIBLE);
        attackerTroops.setVisibility(View.INVISIBLE);
        defenderTroops.setVisibility(View.INVISIBLE);
        defeated.setVisibility(View.INVISIBLE);
        defeated2.setVisibility(View.INVISIBLE);
        aDie1.setVisibility(View.INVISIBLE);
        aDie2.setVisibility(View.INVISIBLE);
        aDie3.setVisibility(View.INVISIBLE);
        dDie1.setVisibility(View.INVISIBLE);
        dDie2.setVisibility(View.INVISIBLE);
        rollsCover.setVisibility(View.INVISIBLE);
        change.setVisibility(View.VISIBLE);
        annihilate.setVisibility(View.INVISIBLE);
        attacking = false;
    }
}