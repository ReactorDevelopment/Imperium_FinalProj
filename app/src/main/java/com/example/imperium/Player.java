package com.example.imperium;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
/**A player object for the game*/
public class Player extends Game{
    /**Stores weather the player is human or not*/
    private boolean human = true;
    /**The player's id*/
    private int id;
    /**The current stage in a turn a player is at*/
    private int stage;
    /**Number of troops available for placement*/
    private int reinforcements;
    /**Ammount of provinces a player is allowed to select*/
    private int selections;
    /**How active a players has been with attacking*/
    private double infamy;
    /**Number of provinces a player has conquered*/
    private int conquers;
    /**Application context*/
    private Context context;
    /**Array of the current provinces that are selected*/
    private Province[] selected;
    /**ArrayList of all the provinces the player owns*/
    private ArrayList<Province> provinces;
    /**Temporary province storage*/
    private Province tempProvince;
    /**Blank constructor*/
    public Player(){}

    /**Constructor for a new player*/
    public Player(Context cont, int ident){
        context = cont;
        id = ident;
        stage = -1;
        reinforcements = 30 + 42 / getPlayerList().length;
        selections = 0;
        infamy = 0;
        conquers = 0;
        tempProvince = getMap().getList()[0];
    }
    /**@return human if the player is a computer or not*/
    public boolean isHuman(){ return human; }
    /**Overloaded in Ai.java, executes at hte beginning of a turn*/
    public void turn(){}
    /**@return selected provinces that have been selected*/
    public Province[] getSelected(){ return selected; }
    /**Sets and returns the infamy vlaue*/
    public double setInfamy(int set){
        infamy = set;
        return infamy;
    }
    /**Get color of player to color provinces with*/
    public int getColor(){
        if(id == 0) return PLAYER_BLUE;
        if(id == 1) return PLAYER_RED;
        if(id == 2) return PLAYER_GREEN;
        else return PLAYER_NONE;
    }
    /**Gets the tag of the player*/
    public String getTag(){
        if(id == 0) return "#00";
        if(id == 1) return "#01";
        if(id == 2) return "#02";
        else return "";
    }
    /**@return id the player's id*/
    public int getId(){ return id; }
    /**@return infamy the player's infamy*/
    public double getInfamy(){return infamy;}
    /**@return stage the player's stage*/
    public int getStage(){ return stage; }
    /**@param set the stage of the player*/
    public void setStage(int set){
        stage = set;
        if(stage != 0)
            selections = 2;
    }
    //public boolean isOwned(Province province){ return province.getOwnerId() == id; }
    /**@param change the new number of selections*/
    public int select(int change){
        selections += change;
        return  selections;
    }
    /**Changes and returns the reinforcement value*/
    public int modTroops(int num){
        reinforcements += num;
        return reinforcements;
    }
    /**@return reinforcements*/
    public int getTroops(){
        return reinforcements;
    }
    /**@param set the reinforcements*/
    public int setTroops(int set){
        reinforcements = set;
        return reinforcements;
    }
    /**Determines it two selected provinces are eligible for an attack
     * returns true if one province if owned and the other is not, the two border eachother*/
    public boolean attackScan(){
        selected = new Province[2];
        boolean friend = false;
        boolean foe = false;
        Province[] list = getMap().getList();
        for(int i=0; i<list.length; i++){
            if(list[i].getOwnerId() == id && list[i].isSelected() && list[i].modTroops(0) > 1) {
                friend = true;
                //Log.i("attackScan", "Friend: " + friend);
                selected[0] = list[i];
                //selected[0].setAttacker(true);
            }
            if(list[i].getOwnerId() != id && list[i].isSelected()) {
                foe = true;
                //Log.i("attackScan", "Foe: " + foe);
                selected[1] = list[i];
                //selected[1].setAttacker(false);
            }
        }
        //Log.i("attackScan", "worthy: " + (friend && foe && selected[0].bordering(selected[1])));
        return friend && foe && selected[0].bordering(selected[1]);
    }
    /**Determines it two selected provinces are eligible for a transport
     * returns true if both are owned, the two border eachother*/
    public boolean transportScan(){
        selected = new Province[2];
        int friends = 0;
        Province[] list = getMap().getList();
        for(int i=0; i<list.length; i++) {
            if (list[i].getOwnerId() == id && list[i].isSelected()) {
                if(friends == 0) {
                    friends++;
                    selected[0] = list[i];
                }
                if(friends == 1 && selected[0] != list[i]) {
                    friends++;
                    selected[1] = list[i];
                    //Log.i("Fren", "dos");
                }
            }
        }
        return friends == 2  && selected[0].bordering(selected[1]);
    }
    /**Preforms an attack between the two selected provinces*/
    public int[] attack(){
        Log.i("attack", "activated");
        int[] losses;
        int[] rolls = new int[5];
        stage = 1;
        //if two provinces are valid for attack
        if(attackScan()) {
            Log.i("attack", "scanned");
            losses = roll(selected[0].modTroops(0), selected[1].modTroops(0));
            //modifies troops based on calculated dice rolls
            selected[0].modTroops(losses[0]);
            selected[1].modTroops(losses[1]);
            selected[0].setText("" + selected[0].modTroops(0));
            selected[1].setText("" + selected[1].modTroops(0));
            Log.i("attack", "troops" + selected[1].modTroops(0));
            for (int i = 0; i < 5; i++)
                rolls[i] = losses[i + 2];
            //if the defending province looses
            if(selected[1].modTroops(0) == 0) {
                conquers++;
                calcInfamy();
                Log.i("defeated", "" + selected[1].getName() + "defeated");
                //transfer ownership
                selected[1].updatePress(selected[0].getOwnerId());
                if(selected[0].modTroops(0) > 3) {
                    selected[0].modTroops(-3);
                    selected[1].modTroops(3);
                    startTransport();
                }
                else if(selected[0].modTroops(0) == 3){
                    selected[0].modTroops(-2);
                    selected[1].modTroops(2);
                }
                else if(selected[0].modTroops(0) == 2){
                    selected[0].modTroops(-1);
                    selected[1].modTroops(1);
                }
            }
        }
        return rolls;
    }
    /**transports @param num troops from one province to another*/
    public void transport(int num){
        Log.i("transporting", "troops: " + num);
        Log.i("transporting", "prohgress: " + getSlideProgress());
        if(transportScan()) {
            if(getSlideProgress() < 50) {
                selected[0].modTroops(num);
                selected[1].modTroops(-num);
            }
            else {
                selected[0].modTroops(-num);
                selected[1].modTroops(num);
            }
            //getCurrentPlayer().getSelected()[0].setAttacker(false);
            //getCurrentPlayer().getSelected()[1].setAttacker(false);
        }
        Log.i("transporting", "inPlayerEnded");
        endTransport();
        endAttack();
    }
    /**Adds provinces to the provinces arraylist of their owner matches this player*/
    public Province[] calcAllOwned(){
        provinces = new ArrayList<>(0);
        for(int i=0; i<getMap().getList().length; i++){
            if(getMap().getList()[i].getOwnerId() == id) {
                provinces.add(getMap().getList()[i]);
            }
        }
        Province[] owned = new Province[provinces.size()];
        for(int i=0; i<provinces.size(); i++){
            owned[i] = provinces.get(i);
            //Log.i("PlayerOwned", "player " + id + " owns " + getMap().getList()[i].getName());
        }
        return owned;
    }
    //public Province getTempProvince(){ return tempProvince; }
    /**Stes the temp province*/
    public void setTempProvince(Province temp){ tempProvince = temp; }

    //potected
    /**Calculates a player's infamy value*/
    protected void calcInfamy(){
        if(infamy < 2)
            infamy = Math.sqrt(conquers) / getTurnNum();
        else
            infamy = 2;
    }
    /**Bubble sort algorithm used to sort dice rolls*/
    protected void bubbleSort(int[] a) {
        boolean sorted = false;
        int temp;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < a.length - 1; i++) {
                if (a[i] > a[i+1]) {
                    temp = a[i];
                    a[i] = a[i+1];
                    a[i+1] = temp;
                    sorted = false;
                }
            }
        }
    }
    /**Randomly generates dice rolls and sorts them*/
    protected int[] roll(int men1, int men2) {
        int [] aftermath = new int[7];
        int [] aRolls = new int[3];
        int [] dRolls = new int[2];
        int aLoss = 0;
        int dLoss = 0;
        int temp;
        if(men1 > 0 && men2 > 0) {
            aRolls[0] = (int)(Math.random()*6+1);
            dRolls[0] = (int)(Math.random()*6+1);
            if (men1 >= 3)
                aRolls[1] = (int) (Math.random() * 6 + 1);
            if (men2 > 2)
                dRolls[1] = (int) (Math.random() * 6 + 1);
            if (men1 > 3)
                aRolls[2] = (int) (Math.random() * 6 + 1);
        }
        Log.i("rolls", "aRolls2: " + aRolls[2]);
        Log.i("rolls", "aRolls1: " + aRolls[1]);
        Log.i("rolls", "aRolls0: " + aRolls[0]);
        //sorts roll
        bubbleSort(aRolls);
        bubbleSort(dRolls);
        temp = aRolls[0];
        aRolls[0] = aRolls[2] + (int) infamy;
        aRolls[2] = temp;
        temp = dRolls[0];
        dRolls[0] = dRolls[1] + (int) selected[1].getOwner().getInfamy();
        dRolls[1] = temp;
        if(aRolls[0] > dRolls[0])
            dLoss--;
        else if(aRolls[0] < dRolls[0])
            aLoss--;
        else
        if(men1 > 0 && men2 > 0)
            aLoss--;
        if(aRolls[1] != 0 && dRolls[1] != 0){
            if(aRolls[1] > dRolls[1])
                dLoss--;
            else if(aRolls[1] < dRolls[1])
                aLoss--;
            else
                aLoss--;
        }
        //outputs results of battle
        aftermath[0] = aLoss;
        aftermath[1] = dLoss;
        aftermath[2] = aRolls[0];
        aftermath[3] = aRolls[1];
        aftermath[4] = aRolls[2];
        aftermath[5] = dRolls[0];
        aftermath[6] = dRolls[1];
        return aftermath;
    }
}
