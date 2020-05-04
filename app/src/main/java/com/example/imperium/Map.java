package com.example.imperium;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
/**Class that contains helper methods for maps*/
public class Map extends Game implements Serializable {
    /**Array of provinces in a map*/
    protected static Province[] provinceList;
    /**Array of continents*/
    protected static Continent[] continents = new Continent[0];
    /**Temporary list to set the borders of a given province*/
    protected ArrayList<Integer> borders;
    /**If the map is classic or imperium*/
    protected int imperiumMap;
    /**Path for the provinces to find their overlays*/
    protected static String mapFilePath;
    /**The id of the map*/
    protected int id;
    /**How large the text appears on provinces*/
    protected double statusScale;
    /**Scale of the overlays of a province*/
    protected double overScale;

    /**Creates blank map*/
    public Map(){}
    /**@return id the maps id*/
    public int getId(){return id;}
    /**@return overScale the maps overScale value*/
    public double getOverScale(){return overScale;}
    /**@return mapFilePath the maps mapFilePath*/
    public static String getMapFilePath(){return mapFilePath;}
    /**@return provinceList the maps provinceList*/
    public Province[] getList(){ return provinceList; }
    /**@return continents the maps continents*/
    public Continent[] getContinents(){ return continents; }
    /**@return statusScale the maps statusScale*/
    public double getStatusScale() {return statusScale;}

    /**Calls the finalization methods for each province and continent*/
    public void place(){
        for(Province prov : provinceList)
            prov.place();
        for(Province prov : provinceList)
            prov.addCapComps();

        if(imperiumMap == 0)
            for(int i=0; i<continents.length; i++)
                continents[i].makeList();
    }
    /**Updates the overlays of every province*/
    public void updateAllOverlays(){
        for(Province p :getList())
            p.updateOverlays();
    }
    /**Returns true if every province on the map has an owner*/
    public boolean allTaken(){
        for(int i=0; i<provinceList.length; i++)
            if(provinceList[i].getOwnerId() == -1)
                return false;
        return true;
    }
    /**Checks if the given player owns the entire map*/
    public boolean allOwned(Player player){
        for(int i=0; i<provinceList.length; i++)
            if(provinceList[i].getOwnerId() != player.getId())
                return false;

        return true;
    }
    /**Calculates the bonuses of each player based on how many continents they completely own*/
    public int bonuses(Player player){
        Province[] provinces = player.calcAllOwned();
        int totalBonuses = 0;
        for(int i=0; i<continents.length; i++){
            if(continents[i].hasComplete(provinces))
                totalBonuses += continents[i].getBonus();
        }
        return totalBonuses;
    }

    /**Converts the borders ArrayList to the out array which is returned,
     * used for assigning provinces their borders*/
    protected int[] bordering(){
        int[] out = new int[borders.size()];
        for(int i=0; i<out.length; i++)
            out[i] = borders.get(i);
        return out;
    }


    /**Uses to randomize the intrest value of a province, making every game slightly different for the Ai*/
    protected double vary(){return Math.random()*.2-.1;}

}
