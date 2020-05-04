package com.example.imperium;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import static com.example.imperium.MainActivity.*;

/**Class that lists the saves on a device*/
public class OpenSaveActivity extends AppCompatActivity {
    /**Layout containing the listTextViews*/
    protected static LinearLayout saveLister;
    /**Layout containing the scroll bars*/
    protected static LinearLayout scrollList;
    /**Application context*/
    private static Context context;
    /**List of saves in ListTextView form*/
    private static ListTextView[] savesList;
    /**Allows the user to input a new name for a save*/
    private static EditText rename;
    /**Top cap of the scroll bar*/
    private ImageView top;
    /**Bottom cap of the scrollbar*/
    private ImageView bottom;
    /**Distance in the y direction that a drag covers*/
    private float dY;
    /**Place where the last drag began*/
    private float lastY;
    /**Width of every listTextView*/
    private int fileWidth = (int)(screenWidth*.6);
    /**Height of every listTextView*/
    private int fileHeight = (int)(screenHeight*.05);
    /**Distance between every listTextView*/
    private int scrollPadding = (int)(screenWidth*.1);

    /**Initalizes all views and displays all saves in listTextViews*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_save);
        ImageView activityRound = findViewById(R.id.loadRound);
        activityRound.setScaleType(ImageView.ScaleType.FIT_XY);
        context = this;
        Log.i("width", ""+ fileWidth);
        listSaves();
        lister();
        quitter();
        openSave();
        deleteSave();
        renameSave();
    }
    /**Removes then replaces all listTextViews*/
    private void refreshList(){
        scrollList.removeView(top);
        scrollList.removeView(bottom);
        for(ListTextView file : savesList) file.deletusFrom();
        listSaves();
    }
    /**Retrives the save files, creates a corresponding listTextView for each and calls makeText() to complete it*/
    private int listSaves(){
        saveLister = findViewById(R.id.savesList);
        scrollList = findViewById(R.id.scrollList);
        //adds and sets height of the endcaps of the scroll
        top = new ImageView(context);
        bottom = new ImageView(context);

        top.setBackgroundResource(R.drawable.scrolltop);
        top.setLayoutParams(new LinearLayout.LayoutParams((int)(.1 * fileWidth), (int)(.3 * fileHeight)));
        top.setX(scrollPadding);
        bottom.setBackgroundResource(R.drawable.scrollbottom);
        bottom.setLayoutParams(new LinearLayout.LayoutParams((int)(.1 * fileWidth), (int)(.3 * fileHeight)));
        bottom.setX(scrollPadding);
        //sorts the files based on their last date of modification
        Log.d("Files", "Path: " + SAVE_PATH);
        File directory = new File(SAVE_PATH);
        File[] files = directory.listFiles();
        boolean sorted = false;
        File temp;
        while(!sorted) {
            sorted = true;
            for (int i = 0; i < files.length - 1; i++) {
                if (files[i].lastModified() < files[i+1].lastModified()) {
                    temp = files[i+1];
                    files[i+1] = files[i];
                    files[i] = temp;
                    sorted = false;
                }
            }
        }
        Log.d("Files", "Size: "+ files.length);
        scrollList.addView(top);
        //initalizes every listTextView
        savesList = new ListTextView[files.length];
        for (int i = 0; i < files.length; i++) {
            makeText(files[i].getName(), i);
            Log.d("Files", "FileName:" + files[i].getName());
        }
        scrollList.addView(bottom);
        return files.length;
    }
    /**Controls how far each listTextView moves when the scroll bar is dragged up or down*/
    @SuppressLint("ClickableViewAccessibility")
    private void lister(){
        scrollList.bringToFront();
        scrollList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getPointerCount() == 1) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            lastY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            dY = event.getRawY() - lastY;
                            lastY = event.getRawY();
                            if(bottom.getY() > 50 && top.getY() < screenHeight*.35) animateAll(dY);
                            else if(bottom.getY() <= 50) animateAll(70, 500);
                            else if(top.getY() >= screenHeight*.35) animateAll(-70, 500);
                            Log.i("dY", ""+dY);
                            Log.i("bounds", "Top"+top.getY()+", Bottom"+bottom.getY());
                            break;
                        default:
                            return false;
                    }
                }
                else return false;

                return true;
            }
        });
    }
    /**Animates all views by a certain value*/
    private void animateAll(float dy){
        top.animate().yBy(dy).setDuration(0);
        bottom.animate().yBy(dy).setDuration(0);
        for(ListTextView v : savesList) v.animateBy(0, dy, 0);
    }
    /**Animates all views with a specified duration*/
    private void animateAll(float dy, int duration){
        top.animate().yBy(dy).setDuration(duration);
        bottom.animate().yBy(dy).setDuration(duration);
        for(ListTextView v : savesList) v.animateBy(0, dy, duration);
    }
    /**Creates a litsTextView in the savesList, sets the text and adds the click listener,
     * that toggles the selection and highlight of the view and adds it to the saves layout*/
    private void makeText(final String NAME, final int id){
        savesList[id] = new ListTextView(context, id);
        //initalizes colors and positions
        savesList[id].getTextView().setLayoutParams(new LinearLayout.LayoutParams(fileWidth, fileHeight));
        savesList[id].getBackText().setLayoutParams(new LinearLayout.LayoutParams(fileWidth, fileHeight));
        savesList[id].animateTo(0, id * fileHeight, 0);

        Log.i("textY", ""+savesList[id].getTextView().getY());
        savesList[id].getImageView().setLayoutParams(new LinearLayout.LayoutParams((int)(.1*fileWidth), fileHeight));
        savesList[id].getImageView().setBackgroundResource(R.drawable.scrollbodylong);
        savesList[id].getImageView().setX(scrollPadding);
        savesList[id].getTextView().setTextColor(Color.parseColor("#c7ccd4"));
        savesList[id].getBackText().setTextColor(Color.parseColor("#000000"));
        savesList[id].setText(NAME);
        //initalizes toggle to select and deselect
        savesList[id].getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deselectFiles(savesList[id].getId());
                if(!savesList[id].getSelected()) savesList[id].getTextView().setTextColor(Color.parseColor("#FFD700"));

                else savesList[id].getTextView().setTextColor(Color.parseColor("#c7ccd4"));
                Log.i("openSave", "clicked: "+NAME+" Selected: "+savesList[id].getSelected());
                savesList[id].setSelected(!savesList[id].getSelected());
            }
        });
        //adds all components to view
        scrollList.addView(savesList[id].getImageView());
        saveLister.addView(savesList[id].getBackText());
        saveLister.addView(savesList[id].getTextView());
    }
    /**Deselects all listTextViews*/
    private void deselectFiles(int skipId) {
        for (int i = 0; i < savesList.length; i++)
            if (savesList[i].getId() != skipId){
                savesList[i].getTextView().setTextColor(Color.parseColor("#c7ccd4"));
                savesList[i].setSelected(false);
            }
    }
    /**Exits the activity*/
    private void quitter(){
        ImageButton quitter = findViewById(R.id.quitter);
        quitter.setBackgroundResource(R.drawable.navquit);
        quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    /**Renames the selected file to the text in the editable text box*/
    private void renameSave(){
        rename = findViewById(R.id.rename);
        ImageButton renameSave = findViewById(R.id.renameSave);
        renameSave.setBackgroundResource(R.drawable.rename);
        renameSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < savesList.length; i++) {
                    if (savesList[i].getSelected()) {
                        if(rename.getText().toString().equals("") || rename.getText().toString().equals("Rename Saves"))
                            Toast.makeText(context, "Enter a new game name" , Toast.LENGTH_SHORT).show();
                        else {
                            renameGame(savesList[i].getTextView().getText().toString(), rename.getText().toString());
                            savesList[i].setText(rename.getText().toString()+".txt");
                        }
                        return;
                    }
                }
                Toast.makeText(context, "Select a file to rename" , Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**Opens GameActivity and passes in the contents of the save file to be loaded*/
    private void openSave(){
        ImageButton openSave = findViewById(R.id.openSave);
        openSave.setBackgroundResource(R.drawable.open);
        openSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < savesList.length; i++) {
                    if(savesList[i].getSelected()) {
                        Intent intent = new Intent(context, GameActivity.class);
                        intent.putExtra("loadedGame", loadGame(SAVE_PATH+"/"+savesList[i].getTextView().getText().toString()));
                        intent.putExtra("tag", "loaded");
                        intent.putExtra("loadName", ""+savesList[i].getTextView().getText().toString());
                        startActivity(intent);
                        return;
                    }
                }
                Toast.makeText(context, "Select a file to load" , Toast.LENGTH_SHORT).show();
            }
        });
    }
    /**Deletes the save from the file system, activation a popup to ask for confirmation*/
    private void deleteSave(){
        ImageButton deleteSave = findViewById(R.id.deleteSave);
        deleteSave.setBackgroundResource(R.drawable.destroy);
        deleteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < savesList.length; i++) {
                    if (savesList[i].getSelected()) {
                        //findViewById(R.id.deleteCheck).animate().y(screenHeight/5).setDuration(500);
                        Log.i("delete", "found"+", "+findViewById(R.id.deleteCheck).getY());
                        //if(!fixClick) ref
                        //fixClick = true;
                        findViewById(R.id.deleteCheck).setVisibility(View.VISIBLE);
                        return;
                    }
                }
                Toast.makeText(context, "Select a file to delete" , Toast.LENGTH_SHORT).show();
            }
        });
        //components for 'are you sure' popup
        ImageButton yes = findViewById(R.id.yes);
        yes.setBackgroundResource(R.drawable.savesdel);
        //deletes and hides
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < savesList.length; i++) {
                    if (savesList[i].getSelected()) {
                        deleteGame(savesList[i].getTextView().getText().toString());
                        refreshList();
                        //findViewById(R.id.deleteCheck).animate().y(2000).setDuration(500);
                        findViewById(R.id.deleteCheck).setVisibility(View.INVISIBLE);
                        return;
                    }
                }

            }
        });
        //hides
        ImageButton no = findViewById(R.id.no);
        no.setBackgroundResource(R.drawable.cancel);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //findViewById(R.id.deleteCheck).animate().y(2000).setDuration(500).start();
                findViewById(R.id.deleteCheck).setVisibility(View.INVISIBLE);
            }
        });
    }
    /**Retrieves the contents of a save file*/
    private String loadGame(String saveId){
        FileInputStream fileIn = null;
        StringBuilder sb = new StringBuilder();
        try {
            fileIn = new FileInputStream(saveId);
            InputStreamReader inputStreamReader = new InputStreamReader(fileIn);
            BufferedReader br = new BufferedReader(inputStreamReader);
            String text;
            while ((text = br.readLine()) != null)
                sb.append(text).append("\n");

        } catch (IOException e) { e.printStackTrace(); }
        finally {
            if (fileIn != null)
                try { fileIn.close(); } catch (IOException e) { e.printStackTrace(); }
        }
        return sb.toString();
    }
    /**Deletes a game from the device*/
    private void deleteGame(String saveId){
        File victim = new File(SAVE_PATH + "/" + saveId);
        if(victim.exists())
            victim.delete();
        if(!victim.exists())
            Toast.makeText(this, "" + SAVE_PATH + "/" + saveId + " is gone, reduced to atoms", Toast.LENGTH_LONG).show();
    }
    /**Renames a given file to a given name*/
    private void renameGame(String saveId, String newName){
        File nohbdy = new File(SAVE_PATH + "/" + saveId);
        if(nohbdy.exists())
            nohbdy.renameTo(new File(SAVE_PATH + "/", newName+".txt"));
        if(!nohbdy.exists())
            Toast.makeText(this, ""+saveId+" renamed to "+newName+".txt", Toast.LENGTH_LONG).show();
    }
}
