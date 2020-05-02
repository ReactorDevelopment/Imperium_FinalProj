package com.example.imperium;

import android.util.Log;

import java.io.Serializable;

/**Continent object that contains provinces*/
public class Continent extends Map implements Serializable {
    /**Name of the continent*/
    private String name;
    /**Id of the continent*/
    private int id;
    /**Troop bonus given by owning the continent*/
    private int bonus;
    /**Intests of the continent*/
    private double interest;
    /**Provinces that the continent contains*/
    private Province[] provinces;
    /**Ids of the provinces the continent contains*/
    private int[] provinceIds;
    /**Blank initializer*/
    public Continent(){}
    /**Initializes values in the continent*/
    public Continent(int id, int color, String name, int bonus, double interest){
        this.name = name;
        this.id = id;
        this.bonus = bonus;
        this.interest = interest;
    }
    /**@return interest the interest of the continent*/
    public double getInterest(){return interest;}
    /**@return bonus the bonus of the continent*/
    public int getBonus(){return bonus;}
    /**@return id the id of the continent*/
    public int getId(){return id;}
    /**@return name the name of the continent*/
    public String getName(){return name;}
    /**@return provinces the provinces of the continent*/
    public Province[] getList(){return provinces;}

    /**Fills the provinceIds array with the values between the parameters*/
    public void fill(int from, int to){
        provinceIds = new int[to-from+1];//9
        for(int i=from; i<to+1; i++) {
            provinceIds[i-from] = i+1;
        }
    }
    /**Creates the provinces array from the provinceIds array*/
    public void makeList(){
        provinces = new Province[provinceIds.length];
        for(int i=provinceIds[0]-1; i<provinceIds[provinceIds.length-1]; i++) {
            provinces[i-(provinceIds[0]-1)] = getMap().getList()[i];
        }
    }
    /**The number of provinces in list that are also in provinces*/
    public int hasIn(Province[] list){
        int count = 0;
        for(int i=0; i<list.length; i++) {
            for (int j = 0; j < provinces.length; j++) {
                if (list[i].getId() == provinceIds[j]) {
                    count++;
                    //Log.i("continent", "contains " + provinces[j].getName() + " in continent " + name);
                }
            }
        }
        //Log.i("continent", "" + count + " provinces out of " + provinces.length + " in " + name);
        return count;
    }
    /**returns true if all the provinces in a continent are in the list parameter*/
    public boolean hasComplete(Province[] list){
        return hasIn(list) == provinces.length;
    }

}
