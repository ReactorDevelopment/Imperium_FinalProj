package com.example.imperium;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

//7/30/19
public class MainActivity extends AppCompatActivity {
    /**Default save file name*/
    public static final String SAVED_GAME_ID = "game.txt";
    /**Location on the device where the save is stored*/
    public static final String SAVE_PATH = Environment.getExternalStorageDirectory().getPath()+"/Imperium/Saves";
    private SharedPreferences prefs;
    /**Code for allowing access to files*/
    private int STORAGE_PERMISSION_CODE = 1;
    /**Transparency of province overlays*/
    private static final int ALPHA = 200;
    /**Province color for no player*/
    public static final int PLAYER_NONE = Color.argb(ALPHA, 188, 216, 157);
    /**Province color for the blue player*/
    public static final int PLAYER_BLUE = Color.argb(ALPHA, 36, 177, 201);
    /**Province color for the red player*/
    public static final int PLAYER_RED = Color.argb(ALPHA, 255, 0, 0);
    /**Province color for the green player*/
    public static final int PLAYER_GREEN = Color.argb(ALPHA, 0, 255, 0);
    /**Text scaling factor*/
    public static final float BASE_TEXT_SCALE =  .05f;
    /**Opens buildActivity to create a new game*/
    private Button newGame;
    /**Opens openSaveActivity to load a game*/
    private Button loadGame;
    /**Closes the app*/
    private Button quit;
    /**Aplication context*/
    private Context context;
    /**Stores the width of the screen in dip units*/
    public static int screenWidth = -1;
    /**Stores the height of the screen in dip units*/
    public static int screenHeight = -1;
    /**Stores the width of the screen in inch units*/
    public static int inchWidth;
    /**Stores the height of the screen in inch units*/
    public static int inchHeight;
    /**An instance of mainActivity for use in permissions*/
    private static Activity mainActivity;

    /**Assigns the dimensions of the screen as well as initializing the buttons*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        context = this;
        prefs = context.getSharedPreferences(SAVED_GAME_ID, MODE_PRIVATE);
        permissions();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        if(screenWidth == -1) screenWidth = metrics.widthPixels;
        if(screenHeight == -1) screenHeight = metrics.heightPixels;
        inchHeight = (int) (screenHeight/metrics.xdpi);
        inchWidth = (int) (screenWidth/metrics.ydpi);
        Log.i("dimensions", "W: "+screenWidth+", H: "+screenHeight);
        makerButton();
        quitterButton();
        loadButton();
    }
    /**Called to check the sates of the storage permissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                Log.i("Perm", ""+(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED));
            } else
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }
    /**Brings up permissions dialogue*/
    private void permissions(){
        ActivityCompat.requestPermissions(mainActivity,
                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

        }
        else {
            makeDirs();
        }
    }
    /**Makes the directories for the game on the device*/
    private void makeDirs(){
        String path = Environment.getExternalStorageDirectory().getPath()+"/Imperium";
        Log.i("path", path);
        File dir = new File(path);
        if(!dir.exists()) if(!dir.mkdir()) Log.i("No", "nomake");;
        dir = new File(path+"/Saves");
        if(!dir.exists()) if(!dir.mkdir()) Log.i("No2", "nomake");
        dir = new File(path+"/Music");
        if(!dir.exists()) if(!dir.mkdir()) Log.i("No3", "nomake");
        dir = new File(path+"/Logs");
        if(!dir.exists()) if(!dir.mkdir()) Log.i("No4", "nomake");
        FileOutputStream fos;
        File save = new File(path+"/Music", "Readme.txt");
        String readme = "This folder stores Imperium's music. Paste any music files that you'd like in the game here!";
        try {
            fos = new FileOutputStream(save);
            fos.write(readme.getBytes());
            fos.close();
        } catch (Exception e) { e.printStackTrace(); }

    }
    /**Tints the status bar on the device to a matching color*/
    protected void tinter(){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            try {
                // to work on old SDKs
                int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = 0x80000000;
                window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

                Class<?> cls = window.getClass();
                Method method = cls.getDeclaredMethod("setStatusBarColor",
                        new Class<?>[] { Integer.TYPE });
                method.invoke(window, Color.parseColor("#854705"));
            } catch (Exception e) {/* upgrade your SDK and ADT :D*/}

        }
    }
    /**Logic for loading buildActivity*/
    private void makerButton(){
        newGame = findViewById(R.id.newGame);
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BuildActivity.class);
                startActivity(intent);
            }
        });
    }
    /**Logic for quitting the app*/
    private void quitterButton(){
        quit = findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
    }
    /**Logic for loading openSaveActivity*/
    private void loadButton(){
        loadGame = findViewById(R.id.loadGame);
        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OpenSaveActivity.class);
                startActivity(intent);
            }
        });
    }

}