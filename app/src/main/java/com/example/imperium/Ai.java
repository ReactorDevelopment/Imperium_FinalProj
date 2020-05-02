package com.example.imperium;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
/**Computer object to play against the player*/
public class Ai extends Player {
    /**Ai stops function when the turn of the game reaches this*/
    private static final int MAX_TURNS = 115;
    /**Shows weather the player is human or not*/
    private boolean human;
    /**Weather the Ai is executing its logic*/
    private boolean executing;
    /**Array containing every porvince that obrders an owned province*/
    private Province[] bordering;

    /**Initializes fields, starts the logic thread*/
    public Ai(Context cont, int ident){
        super(cont, ident);
        human = false;
        executing = false;
        bordering = new Province[0];
        absoluteControl();
        scanBordering();
    }
    /**Sets all provinces to be disabled when the logic was running*/
    private void absoluteControl(){
        Thread control = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(100);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(executing)
                                    setProvEnabled(false);
                            }
                        });
                    }
                } catch (InterruptedException e) { }
            }
        };
        control.start();
    }
    /**Mimics a press to a given province*/
    private void pressProvince(Province province){ province.doClick(); }
    /**Mimics a press to change*/
    private void pressChange(){ getChange().performClick(); }
    /**Mimics a press to again*/
    private void pressAgain(){ getAgain().performClick(); }
    /**Refreshes bordering to the current bordering provinces*/
    private void scanBordering(){
        ArrayList<Province> borderingArray = new ArrayList<>(0);
        for (int i = 0; i< calcAllOwned().length; i++){
            for (int j = 0; j< calcAllOwned()[i].getBordering().length; j++){
                if(calcAllOwned()[i].getBordering()[j].getOwnerId() != getId())
                    borderingArray.add(calcAllOwned()[i].getBordering()[j]);
            }
        }
        bordering = new Province[borderingArray.size()];
        for (int i=0; i<borderingArray.size(); i++)
            bordering[i] = borderingArray.get(i);
    }
    /**Detects the number of enemy troops surrounding a province*/
    private int howSurrounded(Province province){
        int count = 0;
        for(int i=0; i<province.getBordering().length; i++){
            if(province.getBordering()[i].getOwnerId() != getId())
                count += province.getBordering()[i].modTroops(0);
        }
        return  count;
    }
    /**Executes an attack from a given province to another province, stopping when the ratio of attacking troops to
     * defending troops is less than perDiff or when the enemy has been defeated*/
    private void aiAttack(Province attacker, Province defender, double perDiff /*ratio of attacking troops to defending troops*/){
        Log.i("aiAttack", "ran");
        changeAllSelection(false);
        pressProvince(attacker);
        pressProvince(defender);
        Log.i("aiAttack", " Attacker selected: " + attacker.isSelected() + ", Id: " + attacker.getId() + ", Troops: " + attacker.modTroops(0));
        Log.i("aiAttack", " Devender selected: " + defender.isSelected() + ", Id: " + defender.getId() + ", Troops: " + defender.modTroops(0));
        while(attacker.modTroops(0)/(double)defender.modTroops(0) > perDiff){
            Log.i("aiAttack", "inLoop");
            pressAgain();
            if(transportScan()) {
                getSlider().setProgress(100);
                break;
            }
            if(!attackScan()) {
                Log.i("aiAttack", "forceBroke");
                break;
            }
        }
    }
    /**Refreshes the bordering provinces and executes logic based on the current stage of the turn, ending once it is done*/
    public void turn(){
        executing = true;
        Log.i("AiTurn", "aiturn: " + getId());
        scanBordering();
        if (getTurnNum() < MAX_TURNS) {
            if (getStage() == -1)
                setupStage();
            if (getStage() == 0)
                placeStage();
            if (getStage() == 1)
                attackStage();
            if (getStage() == 2)
                transportStage();
        }
        executing = false;
        setProvEnabled(true);
    }
    /**Presses the province with the highest interest value to place a troop*/
    private void setupStage(){
        Log.i("AiStaging", "setup");
        double max = 0;
        Continent bestC = null;
        Province bestP = null;
        if(!getMap().allTaken()) {
            Log.i("Setup", "not all taken");
            for(int i=0; i<getMap().getList().length; i++){
                if(getMap().getList()[i].getInterest() + getMap().getList()[i].getContinent().getInterest() > max && getMap().getList()[i].getOwnerId() == -1){
                    max = getMap().getList()[i].getInterest() + getMap().getList()[i].getContinent().getInterest();
                    bestP = getMap().getList()[i];
                }
            }
        }
        max = 0;
        if(getMap().allTaken()) {
            Log.i("taken", "allTaken");
            for (int i = 0; i < getMap().getList().length; i++) {
                Province test = getMap().getList()[i];
                if((test.getInterest() + test.getContinent().getInterest() - test.modTroops(0)/20)> max && test.getOwnerId() == getId()) {
                    max = getMap().getList()[i].getInterest() + getMap().getList()[i].getContinent().getInterest();
                    bestP = getMap().getList()[i];
                }
            }
        }
        Log.i("Setup", "pressed: " + bestP.getName());
        pressProvince(bestP);
    }
    /**Presses a province based on its interest but also factoring in how threatened it is be enemies*/
    private void placeStage(){
        Log.i("AiStaging", "place");
        double max = 0;
        while (modTroops(0) > 0) {
            Continent bestC = null;
            for (int i = 0; i < getMap().getContinents().length; i++) {
                Continent test = getMap().getContinents()[i];
                if (test.hasIn(calcAllOwned()) >= test.getList().length/2 && test.getInterest() > max) {
                    max = test.getInterest();
                    bestC = test;
                }
            }
            Province bestP = null;
            if (bestC != null) {
                max = 0;
                for (int i = 0; i < bestC.getList().length; i++) {
                    for (int j = 0; j < bestC.getList()[i].getBordering().length; j++) {
                        for (int k = 0; k < bordering.length; k++) {
                            if (bestC.getList()[i].getBordering()[j].getId() == bordering[k].getId() && bestC.getList()[i].getInterest()
                                    - bestC.getList()[i].modTroops(0) / bordering[k].modTroops(0) > max) {
                                bestP = bestC.getList()[i];
                                max = bestC.getList()[i].getInterest()
                                        - bestC.getList()[i].modTroops(0) / bordering[k].modTroops(0);
                            }
                        }
                    }
                }
            }
            max = 0;
            if (bestC == null || bestP == null) {
                for (int i = 0; i < getMap().getList().length; i++) {
                    Province test = getMap().getList()[i];
                    if ((test.getInterest() + test.getContinent().getInterest() - test.modTroops(0) / 25)
                            + howSurrounded(test) / test.modTroops(0) > max && test.getOwnerId() == getId()) {
                        max = test.getInterest() + test.getContinent().getInterest() - test.modTroops(0) / 20
                                + howSurrounded(test) / test.modTroops(0);
                        bestP = getMap().getList()[i];
                    }
                }
            }
            if(bestP == null)
                bestP = calcAllOwned()[0];
            pressProvince(bestP);
        }
        pressChange();
    }
    /**Attempts to gain control of the most interesting province, to break into an enemy controlled continent,
     * or to decrease the surrounding amount of troops*/
    private void attackStage(){
        Log.i("AiStaging", "attack");
        double max = 0;
        Continent bestC = null;
        Province target = null;
        for (int i = 0; i < getMap().getContinents().length; i++) {
            Continent test = getMap().getContinents()[i];
            if (test.hasIn(calcAllOwned()) >= test.getList().length/2.0 && test.getInterest() > max) {
                max = test.getInterest();
                bestC = test;
            }
        }
        max = 0;
        if(bestC != null){
            Log.i("bestC", "the best arouound: " + bestC.getName());
            for (int i = 0; i < bestC.getList().length; i++) {
                if(bestC.getList()[i].getOwnerId() != getId()) {
                    target = bestC.getList()[i];
                    Log.i("aiAttack", "foe: " + target.getName());
                    for (int j = 0; j < target.getBordering().length; j++) {
                        Log.i("aiAttack", "foeBorder: " + target.getBordering()[j].getName());
                        if(target.getBordering()[j].getOwnerId() == getId() && target.getBordering()[j].modTroops(0) > 1)
                            aiAttack(target.getBordering()[j], target, .8);
                    }
                }
            }
        }
        else{
            Log.i("aiAttack", "inElse");
            target = calcAllOwned()[(int)(Math.random()* calcAllOwned().length)];
            for (int i = 0; i < getMap().getContinents().length; i++) {
                Continent test = getMap().getContinents()[i];
                if (test.getInterest() > max) {
                    max = test.getInterest();
                    bestC = test;
                }
            }
            max = 0;
            for (int i = 0; i < bestC.getList().length; i++) {
                if (bestC.getList()[i].getInterest() > max && bestC.getList()[i].modTroops(0) > 15 && bestC.getList()[i].getOwnerId() == getId()) {
                    max = target.getInterest();
                    target = bestC.getList()[i];
                    Log.i("aiAttack", "target: " + target.getName());
                }
            }
            for(int i=0; i < target.getBordering().length; i++){
                if(target.getBordering()[i].getOwnerId() != getId()) {
                    Log.i("aiAttack", "border: " + target.getBordering()[i].getName());
                    aiAttack(target, target.getBordering()[i], .8);
                    break;
                }
            }
        }
        pressChange();
    }
    /**Ends the current turn*/
    private void transportStage(){
        Log.i("AiStaging", "transport");
        if(getTurnNum() < MAX_TURNS)
            pressChange();
    }
}
