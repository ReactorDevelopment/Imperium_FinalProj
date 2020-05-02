package com.example.imperium;

import android.content.Context;

import java.util.ArrayList;

public class ClassicMap extends Map {
    private Province[] provinceList;
    private Continent[] continents;
    private ArrayList<Integer> borders;

    public ClassicMap(Context context){
        provinceList = new Province[44];
        continents = new Continent[6];
        //assemble(context);
    }
    /*public void assemble(Context context){
        int cShiftX = 40;
        int cShiftY = 30;
        borders = new ArrayList<>();
        //continent 0
        int cNum = 0;
        int num = 1;
        continents[cNum] = new Continent(cNum, 20+cShiftX, 60+cShiftY, "North America", 5, .5+vary());
        borders.add(0, 2); borders.add(1, 4); borders.add(2, 32);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY(), 70, 70,  "Alaska", .8+vary());
        borders = new ArrayList<>();
        num = 2;
        borders.add(0, 1); borders.add(1, 3); borders.add(2, 4); borders.add(3, 5);
        provinceList[num-1] = new Province(context, num, cNum, bordering(), continents[cNum].getX()+65, continents[cNum].getY()+10, 100, 40, "Northwest Territory", .3+vary());
        borders = new ArrayList<>();
        num = 3;
        borders.add(0, 2); borders.add(1, 5); borders.add(2, 6); borders.add(3, 14);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+240, continents[cNum].getY()-50, 100, 80, "Greenland", .8+vary());
        borders = new ArrayList<>();
        num = 4;
        borders.add(0, 1); borders.add(1, 2); borders.add(2, 5); borders.add(3, 7);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+73, continents[cNum].getY()+45, 70, 40, "Alberta", .4+vary());
        borders = new ArrayList<>();
        num = 5;
        borders.add(0, 4); borders.add(1, 2); borders.add(2, 3); borders.add(3, 6); borders.add(4, 8); borders.add(5, 7);
        provinceList[num-1] = new Province(context, num, cNum, bordering(), continents[cNum].getX()+145, continents[cNum].getY()+45, 60, 40, "Ontario", .3+vary());
        borders = new ArrayList<>();
        num = 6;
        borders.add(0, 8); borders.add(1, 5); borders.add(2, 3);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+200, continents[cNum].getY()+45, 55, 45, "Quebec", .5+vary());
        borders = new ArrayList<>();
        num = 7;
        borders.add(0, 4); borders.add(1, 5); borders.add(2, 8); borders.add(3, 9);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+85, continents[cNum].getY()+75, 65, 60, "Western United States", .6+vary());
        borders = new ArrayList<>();
        num = 8;
        borders.add(0, 7); borders.add(1, 5); borders.add(2, 6); borders.add(3, 9);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+160, continents[cNum].getY()+75, 60, 60, "Eastern United States", .7+vary());
        borders = new ArrayList<>();
        num = 9;
        borders.add(0, 7); borders.add(1, 8); borders.add(2, 10);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+115, continents[cNum].getY()+115, 40, 55, "Mexico", .8+vary());
        borders = new ArrayList<>();
        continents[cNum].fill(0, 8);
        //continent 1
        cNum = 1;
        continents[cNum] = new Continent(cNum, 160+cShiftX, 220+cShiftY, "South America", 2, .7+vary());
        num = 10;
        borders.add(0, 9); borders.add(1, 11); borders.add(2, 12);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY(), 55, 45, "Venezuela", .8+vary());
        borders = new ArrayList<>();
        num = 11;
        borders.add(0, 10); borders.add(1, 12); borders.add(2, 13);borders.add(3, 21);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+45, continents[cNum].getY()+40, 85, 60, "Brazil", .9+vary());
        borders = new ArrayList<>();
        num = 12;
        borders.add(0, 10); borders.add(1, 11); borders.add(2, 13);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()-10, continents[cNum].getY()+50, 55, 45, "Peru", .6+vary());
        borders = new ArrayList<>();
        num = 13;
        borders.add(0, 12); borders.add(1, 11);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+22, continents[cNum].getY()+85,60, 90, "Argentina", .3+vary());
        borders = new ArrayList<>();
        continents[cNum].fill(9, 12);
        //continent 2
        cNum = 2;
        continents[cNum] = new Continent(cNum, 313+cShiftX, 81+cShiftY, "Europe", 5, .3+vary());
        num = 14;
        borders.add(0, 3); borders.add(1, 15); borders.add(2, 16);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY(), 55, 45, "Iceland", .8+vary());
        borders = new ArrayList<>();
        num = 15;
        borders.add(0, 14); borders.add(1, 16); borders.add(2, 17);borders.add(3, 18);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+5, continents[cNum].getY()+35, 45, 45, "Great Britain", .4+vary());
        borders = new ArrayList<>();
        num = 16;
        borders.add(0, 14); borders.add(1, 15); borders.add(2, 18);borders.add(3, 20);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+60, continents[cNum].getY()-25, 55, 65, "Scandinavia", .5+vary());
        borders = new ArrayList<>();
        num = 17;
        borders.add(0, 15); borders.add(1, 18); borders.add(2, 19);borders.add(3, 21);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+10, continents[cNum].getY()+70, 55, 45, "Western Europe", .8+vary());
        borders = new ArrayList<>();
        num = 18;
        borders.add(0, 15); borders.add(1, 17); borders.add(2, 19);borders.add(3, 20);borders.add(4, 16);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+55, continents[cNum].getY()+45, 55, 45, "Northern Europe", .7+vary());
        borders = new ArrayList<>();
        num = 19;
        borders.add(0, 22); borders.add(1, 17); borders.add(2, 18);borders.add(3, 20);borders.add(4, 21);borders.add(5, 35);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+65, continents[cNum].getY()+75, 55, 45, "Southern Europe", .8+vary());
        borders = new ArrayList<>();
        num = 20;
        borders.add(0, 16); borders.add(1, 19); borders.add(2, 18);borders.add(3, 35);borders.add(4, 33);borders.add(5, 27);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+110, continents[cNum].getY()+10, 80, 80, "Russia", .9+vary());
        borders = new ArrayList<>();
        continents[cNum].fill(13, 19);
        //continent 3
        cNum = 3;
        continents[cNum] = new Continent(cNum, 320+cShiftX, 200+cShiftY, "Africa", 3, .6+vary());
        num = 21;
        borders.add(0, 11); borders.add(1, 19); borders.add(2, 17);borders.add(3, 22);borders.add(4, 23);borders.add(5, 24);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY(), 80, 90, "North Africa", .9+vary());
        borders = new ArrayList<>();
        num = 22;
        borders.add(0, 21); borders.add(1, 19); borders.add(2, 35);borders.add(3, 23);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+60, continents[cNum].getY()+5, 55, 45, "Egypt", .8+vary());
        borders = new ArrayList<>();
        num = 23;
        borders.add(0, 21); borders.add(1, 22); borders.add(2, 35);borders.add(3, 26);borders.add(4, 24);borders.add(5, 25);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+90, continents[cNum].getY()+45, 55, 70, "East Africa", .7+vary());
        borders = new ArrayList<>();
        num = 24;
        borders.add(0, 21); borders.add(1, 23); borders.add(2, 25);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+55, continents[cNum].getY()+85, 55, 45, "Congo", .3+vary());
        borders = new ArrayList<>();
        num = 25;
        borders.add(0, 24); borders.add(1, 23); borders.add(2, 26);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+50, continents[cNum].getY()+120, 55, 70, "South Africa", .2+vary());
        borders = new ArrayList<>();
        num = 26;
        borders.add(0, 23); borders.add(1, 25);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+120, continents[cNum].getY()+130, 45, 45, "Madagascar", .5+vary());
        borders = new ArrayList<>();
        continents[cNum].fill(20, 25);
        //continent 4
        cNum = 4;
        continents[cNum] = new Continent(cNum, 505+cShiftX, 90+cShiftY, "Asia", 7, .2+vary());
        num = 27;
        borders.add(0, 20); borders.add(1, 33); borders.add(2, 34);borders.add(3, 28);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY(), 70, 70, "Ural", .8+vary());
        borders = new ArrayList<>();
        num = 28;
        borders.add(0, 27); borders.add(1, 34); borders.add(2, 31);borders.add(3, 30);borders.add(4, 29);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+55, continents[cNum].getY()-25, 70, 90, "Siberia", .2+vary());
        borders = new ArrayList<>();
        num = 29;
        borders.add(0, 28); borders.add(1, 30); borders.add(2, 32);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+120, continents[cNum].getY()-30, 70, 55, "Yakutsk", .5+vary());
        borders = new ArrayList<>();
        num = 30;
        borders.add(0, 28); borders.add(1, 29); borders.add(2, 31);borders.add(3, 32);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+110, continents[cNum].getY()+10, 55, 45, "Irkutsk", .3+vary());
        borders = new ArrayList<>();
        num = 31;
        borders.add(0, 28); borders.add(1, 34); borders.add(2, 30);borders.add(3, 32);borders.add(4, 38);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+120, continents[cNum].getY()+43, 65, 45, "Mongolia", .6+vary());
        borders = new ArrayList<>();
        num = 32;
        borders.add(0, 29); borders.add(1, 30); borders.add(2, 31);borders.add(3, 38);borders.add(4, 1);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+205, continents[cNum].getY()-25, 65, 50, "Kamchatka", .9+vary());
        borders = new ArrayList<>();
        num = 33;
        borders.add(0, 27); borders.add(1, 34); borders.add(2, 35);borders.add(3, 20);borders.add(4, 36);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()-20, continents[cNum].getY()+50, 65, 60, "Afghanistan", .5+vary());
        borders = new ArrayList<>();
        num = 34;
        borders.add(0, 37); borders.add(1, 36); borders.add(2, 33);borders.add(3, 27);borders.add(4, 28);borders.add(5, 31);borders.add(6, 39);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+40, continents[cNum].getY()+75, 110, 70, "China", .6+vary());
        borders = new ArrayList<>();
        num = 35;
        borders.add(0, 23); borders.add(1, 22); borders.add(2, 19);borders.add(3, 20);borders.add(4, 33);borders.add(5, 36);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()-70, continents[cNum].getY()+80, 70, 90, "Middle East", .7+vary());
        borders = new ArrayList<>();
        num = 36;
        borders.add(0, 35); borders.add(1, 33); borders.add(2, 34);borders.add(3, 37);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+15, continents[cNum].getY()+105, 70, 100, "India", .4+vary());
        borders = new ArrayList<>();
        num = 37;
        borders.add(0, 36); borders.add(1, 34); borders.add(2, 40);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+80, continents[cNum].getY()+130, 55, 45, "Siam", .9+vary());
        borders = new ArrayList<>();
        num = 38;
        borders.add(0, 31); borders.add(1, 32);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+175, continents[cNum].getY()+43, 55, 65, "Japan", .6+vary());
        borders = new ArrayList<>();
        num = 39;
        borders.add(0, 34);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+135, continents[cNum].getY()+135, 35, 45, "Philipines", .7+vary());
        borders = new ArrayList<>();
        continents[cNum].fill(26, 38);
        //continent 5
        cNum = 5;
        continents[cNum] = new Continent(cNum, 590+cShiftX, 270+cShiftY, "Australia", 2, .8+vary());
        num = 40;
        borders.add(0, 37); borders.add(1, 41); borders.add(2, 42);borders.add(3, 43);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY(), 55, 45, "Indonesia", .8+vary());
        borders = new ArrayList<>();
        num = 41;
        borders.add(0, 40); borders.add(1, 42); borders.add(2, 43);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+87, continents[cNum].getY()+2, 55, 45, "New Guinea", .5+vary());
        borders = new ArrayList<>();
        num = 42;
        borders.add(0, 40); borders.add(1, 43);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX(), continents[cNum].getY()+50, 55, 45, "Western Australia", .3+vary());
        borders = new ArrayList<>();
        num = 43;
        borders.add(0, 44); borders.add(1, 42); borders.add(2, 41);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+80, continents[cNum].getY()+50, 55, 45, "Eastern Australia", .6+vary());
        borders = new ArrayList<>();
        num = 44;
        borders.add(0, 43);
        provinceList[num-1] = new Province(context,num, cNum, bordering(), continents[cNum].getX()+130, continents[cNum].getY()+60, 30, 45, "New Zealand", .7+vary());
        borders = new ArrayList<>();
        continents[cNum].fill(39, 43);
    }*/
    public int getId(){return 0;}
    public Province[] getList(){
        return provinceList;
    }
    public Continent[] getContinents(){
        return continents;
    }
    public void place(){
        for(int i=0; i<provinceList.length; i++)
            provinceList[i].place();
        for(int i=0; i<continents.length; i++)
            continents[i].makeList();
    }

    public boolean allTaken(){
        for(int i=0; i<provinceList.length; i++){
            if(provinceList[i].getOwnerId() == -1)
                return false;
        }
        return true;
    }
    public boolean allOwned(Player player){
        for(int i=0; i<provinceList.length; i++){
            if(provinceList[i].getOwnerId() != player.getId())
                return false;
        }
        return true;
    }
    public int bonuses(Player player){
        Province[] provinces = player.calcAllOwned();
        int totalBonuses = 0;
        for(int i=0; i<continents.length; i++){
            if(continents[i].hasComplete(provinces))
                totalBonuses+= continents[i].getBonus();
        }
        return totalBonuses;
    }
    /*private int[] bordering(){
        int[] out = new int[borders.size()];
        for(int i=0; i<out.length; i++){
            out[i] = borders.get(i);
        }
        return out;
    }*/
    //private int vary(){return (int)(Math.random()*.4-.2);}
}
