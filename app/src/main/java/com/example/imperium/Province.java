package com.example.imperium;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import static com.example.imperium.MainActivity.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;
/**Province object for use on a map*/
public class Province extends Continent implements Serializable {
    /**Id os the province*/
    private int id;
    /**Id of the province's owner*/
    private int ownerId;
    //private String loadTag;
    /**Id of the continent the province is on*/
    private int continentId;
    /**X coord of the province*/
    private int x;
    /**X coord of the province*/
    private int y;
    /**Ids of the provinces that border this one*/
    private int[] borders;
    /**Helps the Ai determine the value of the province*/
    private double interest;
    /**Troops that a province contains*/
    private int troops;
    /**Weather the province has been selected of not*/
    private boolean selected;
    /**If the game is an imperium game*/
    private boolean imperium;
    /**Name of the province*/
    private String name;
    /**Instance of this province*/
    private Province self;
    /**Array of provinces this one is bordering*/
    private Province[] bordering;
    /**The map layout*/
    private RelativeLayout mapLayout;
    /**The text showing the amount of troops in a province*/
    private TextView status;
    /**The image overlay for the province*/
    private ImageView overlay;
    /**Shows the relation to the owner of a province*/
    private ImageView owner;
    /**Shows if the province is selected*/
    private ImageView showSelected;
    /**Application context*/
    private Context context;
    /**Bitmap of the provinces overlay*/
    private Bitmap overBit;
    /**Center point of the province*/
    private Point center;

    /**Constructor initializes fields and views*/
    public Province(Context context, int id, int continent, int[] borders, int x, int y, String name, double interest) { //classic Mode
        imperium = false;
        this.name = name;
        this.x = x;
        this.y = y;
        this.borders = borders;
        this.id = id;
        this.interest = interest;
        this.context = context;
        continentId = continent;
        resetValues();
        status = new TextView(context);
        status.setText("");
        overlay = new ImageView(context);
        //aimAttack = new ImageView(context);
        owner = new ImageView(context);
        showSelected = new ImageView(context);
        selected = false;
        mapLayout = getMapLayout();
        self = this;
    }

    /**Resets ownerId and troops to a default*/
    public void resetValues() {
        Log.i("ResetValuse", "");
        ownerId = -1;
        troops = 0;
    }
    /**@return id the id of the province*/
    public int getId() { return id; }
    /**@return x the x of the province*/
    public int getX() { return x; }
    /**@return y the y of the province*/
    public int getY() { return y; }
    /**@return name the name of the province*/
    public String getName() { return name; }

    /**@return ownerId the ownerId of the province*/
    public int getOwnerId() { return ownerId; }
    /**@return center the center of the province*/
    public Point getCenter() { return center; }
    /**Returns the continent that contains the province*/
    public Continent getContinent() {
        return getMap().getContinents()[continentId];
    }
    /**The array of provinces that border this one*/
    public Province[] getBordering() {
        return bordering;
    }

    /**@return owner the owner of the province*/
    public Player getOwner() {
        if (getGame() != null && ownerId >= 0) return getGame().getPlayerList()[ownerId];
        return null;
    }

    /**@return selected if the province is selected*/
    public boolean isSelected() { return selected; }
    /**@return overBit the overlay of the province*/
    public Bitmap getOverlay() { return overBit; }
    /**@return interest the interest of the province*/
    public double getInterest() { return interest + getContinent().getInterest(); }

    /**@param color sets the color of the overlay*/
    public void setColor(int color) {
        overlay.setColorFilter(color);
    }
    /**@param text sets the text on the province*/
    public void setText(String text) { status.setText(text); }

    /**@param set sets if the province is selected*/
    public void setSelected(boolean set) {
        selected = set;
        Log.i("provinceSelect", name+", isSelected: "+selected);
    }
    /**Sets the selection of the province and shows the selection image*/
    public void setAndShowSelection(boolean set) {
        selected = set;
        if (selected) showSelected.setVisibility(View.VISIBLE);
        else showSelected.setVisibility(View.INVISIBLE);
    }

    /**builds comps and assigns borders*/
    public void place() {
        bordering = new Province[borders.length];
        for (int i = 0; i < bordering.length; i++)
            bordering[i] = provinceList[borders[i] - 1];
        buildComps();
    }
    /**Updates the owner of the province to @param ownerId as well as the overlay cover*/
    public void updatePress(final int ownerId) {
        int prevId = this.ownerId;
        this.ownerId = ownerId;
        if(ownerId == -1) Log.i("Negg", "");
        if (ownerId != prevId)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ownerId != -1 && getOwner() != null)
                        overlay.setColorFilter(getOwner().getColor());
                    else overlay.setColorFilter(PLAYER_NONE);
                    updateOwner();
                }
            });
    }

    /**Sets the text on the province to reflect the number of troops it has*/
    public void updateOwner() {
        if (getOwner() != null) {
            if (getTroops() > 0)
                setText("" + (int) getTroops());
            else{
                setText("");
            }
        }else if (ownerId == -1) {
            Log.i("onverNull", "");
            owner.setBackgroundResource(R.drawable.blank);
        }
    }
    /**Returns if the province is bordering the parameter*/
    public boolean bordering(Province province) {
        if(province == null) return false;
        for (Province border : bordering) {
            if (border.getId() == province.getId())
                return true;
        }
        return false;
    }
    /**Updates the tint on the overlay depending on the province's owner*/
    public void updateOverlays() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                overlay.setVisibility(View.VISIBLE);
                if(ownerId != -1 && ownerId < getPlayerList().length && getOwner() != null) overlay.setColorFilter(getOwner().getColor());
                else overlay.setColorFilter(PLAYER_NONE);
            }
        });

    }
    /**Modifies and returns the number of troops a province has*/
    public int modTroops(double mod) {
        troops += mod;
        if(mod != 0) {
            Log.i("ProvinceModded", "" + name + ": " + (troops - mod) + " modded by " + mod + ", troops: " + troops + ", blocked? false");
        }
        return troops;
    }
    /**Returns the number of troops a province has*/
    public double getTroops(){ return troops; }

    /**Shows or hides the text on a province, called at a particular map zoom*/
    public void ownerVis(boolean vis) {
        if (vis) {
            owner.setVisibility(View.VISIBLE);
            status.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (float) (2*getMap().getStatusScale()));

        } else {
            owner.setVisibility(View.INVISIBLE);
            status.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 0);
        }
    }
    /**Adds a province's components to the mapLayout*/
    public void addCapComps(){
        mapLayout.addView(showSelected);
        mapLayout.addView(owner);
        mapLayout.addView(status);
    }
    /**Logic for when a province is clicked, ie placing troops or selecting hte province based on the stage of the current player*/
    public void doClick() {
        long startClick = System.currentTimeMillis();
        Log.i("ClickDelay5", ""+(System.currentTimeMillis()-startClick));
        Log.i("ClickDelay4", ""+(System.currentTimeMillis()-startClick));
            Log.i("doClick", "did, Stage:" + getCurrentPlayer().getStage() + "PLayerId: "+getCurrentPlayer().getId() + "OwnerId: "+ownerId);
            if (getCurrentPlayer().getStage() == -1 && (ownerId == -1 || ownerId == getCurrentPlayer().getId())) {
                boolean did = false;
                //claims a new province while map is not full
                if (!imperium && getCurrentPlayer().getTroops() > 0) {
                    if (getMap().allTaken()) {
                        modTroops(1);
                        updateOwner();
                        getCurrentPlayer().modTroops(-1);
                        did = true;
                    }
                    if (ownerId == -1) {
                        ownerId = getCurrentPlayer().getId();
                        if(ownerId == -1) Log.i("Neggt", "");
                        modTroops(1);
                        updateOwner();
                        getCurrentPlayer().modTroops(-1);
                        did = true;
                    }
                    //ends setup phase of the game
                }else{
                    getCurrentPlayer().setStage(0);
                }
                if (did) {
                    Log.i("Skip", "did");
                    getCurrentPlayer().calcAllOwned();
                    changePlayer(false);
                }
            }
            //increments troops on province by 1 and removes reinforcement
            if (getCurrentPlayer().getStage() == 0 && ownerId == getCurrentPlayer().getId()) {
                Log.i("ClickDelay1", ""+(System.currentTimeMillis()-startClick));
                if (!imperium && getCurrentPlayer().getTroops() > 0) {
                    modTroops(1);
                    getCurrentPlayer().modTroops(-1);
                    getCurrentPlayer().setTempProvince(getMap().getList()[id - 1]);
                }
                //selects province for either attack or transport
            } else if (getCurrentPlayer().getStage() == 1 || getCurrentPlayer().getStage() == 2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (selected) {
                            setAndShowSelection(false);
                            getCurrentPlayer().select(1);
                        } else if (!selected && getCurrentPlayer().select(0) > 0) {
                            setAndShowSelection(true);
                            getCurrentPlayer().select(-1);
                        }
                    }
                });
            }
        Log.i("ClickDelayFinasl", ""+(System.currentTimeMillis()-startClick));
    }
    /**Opens the info*/
    public void doLongClick(){showInfo(self);}

    /**Initializes the views of the province, notably extracting the bitmap file form the assets folder*/
    private void buildComps(){
        //loads bitmap and assigns it to the overlay
        try{
            InputStream in = context.getAssets().open("overlays/"+getMap().getMapFilePath()+id+".png");
            byte[] inBytes = new byte[in.available()];
            in.read(inBytes);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            try {
                overBit = BitmapFactory.decodeByteArray(inBytes, 0, inBytes.length, options);
            }catch(OutOfMemoryError e){e.printStackTrace();}
            overlay.setImageBitmap(overBit);
            //Log.i("provId", ""+id+", Bitwidth:"+overBit.getWidth()+", Bitheight"+overBit.getHeight());
        }catch(IOException e){ e.printStackTrace();}
        //positions all views and adds them to the layout
        x += 3; y += 1;
        owner.setLayoutParams(new RelativeLayout.LayoutParams((int)(10*getMap().getStatusScale()), (int)(5*getMap().getStatusScale())));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int)(overBit.getWidth()*getMap().overScale), (int)(overBit.getHeight()*getMap().overScale));
        params.leftMargin = x; params.topMargin = y;
        overlay.setLayoutParams(params);
        showSelected.setLayoutParams(new RelativeLayout.LayoutParams(20, 20));
        status.setLayoutParams(new RelativeLayout.LayoutParams(20, 20));
        mapLayout.addView(overlay);

        status.bringToFront();
        center = new Point(x+(int)(overBit.getWidth()/2*getMap().overScale), y+(int)(overBit.getHeight()/2*getMap().overScale));
        owner.setX(center.x-5);
        owner.setY(center.y-3);
        showSelected.setX(center.x-10);
        showSelected.setY(center.y-10);
        status.setX(center.x-3);
        status.setY(center.y-3);
        overlay.setVisibility(View.INVISIBLE);
        showSelected.setVisibility(View.INVISIBLE);
        owner.setVisibility(View.INVISIBLE);
        showSelected.setBackgroundResource(R.drawable.selected);
        status.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 0);
        status.setTextColor(BLACK);
    }
}
