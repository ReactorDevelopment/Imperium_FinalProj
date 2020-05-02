package com.example.imperium;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;
/**Class for displaying saves*/
public class ListTextView extends OpenSaveActivity{
    /**Id if listTextView*/
    private int id;
    /**If the view is selected*/
    private Boolean selected;
    /**The primary text*/
    private TextView text;
    /**The background text*/
    private TextView backText;
    /**The portion of the scroll bar associated with it*/
    private ImageView scroll;
    /**The size of the text*/
    private int textSize;

    /**Initializes all fields and views*/
    public ListTextView(Context context, int id){
        this.id = id;
        selected = false;
        text = new TextView(context);
        backText = new TextView(context);
        scroll = new ImageView(context);
    }
    /**@return backText the backText view*/
    public TextView getBackText(){return backText;}
    /**@return text the text view*/
    public TextView getTextView(){return text;}
    /**@return scroll the scroll view*/
    public ImageView getImageView(){return scroll;}
    /**@return selected the selection status*/
    public Boolean getSelected(){return selected;}
    /**@param set sets the selection*/
    public void setSelected(Boolean set){selected = set;}
    /**@return id the id*/
    public int getId(){return id;}

    /**Sets the text of the view*/
    public void setText(String rename){
        setTextSize(35-rename.length()/2);
        text.setText(rename);
        backText.setText(rename);
    }
    /**Sets the textsize and adjusts the position of the backText*/
    public void setTextSize(int size){
        if(size > 20) size = 20;
        if(size < 10) size = 10;
        textSize = size;
        text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        backText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        backText.animate().xBy((float)(.1*textSize)).setDuration(0);
    }
    /**Removes all views*/
    public void deletusFrom(){
        saveLister.removeView(text);
        saveLister.removeView(backText);
        scrollList.removeView(scroll);
    }
    /**Animates all views to a given spot*/
    public void animateTo(float x, float y, long duration){
        text.animate().x(x).y(y).setDuration(duration);
        backText.animate().x((float)(x+.1*textSize)).y(y).setDuration(duration);
    }
    /**Animates all views by a given value*/
    public void animateBy(float dx, float dy, long duration){
        text.animate().xBy(dx).yBy(dy).setDuration(duration);
        backText.animate().xBy(dx).yBy(dy).setDuration(duration);
        scroll.animate().yBy(dy).setDuration(duration);
    }
}
