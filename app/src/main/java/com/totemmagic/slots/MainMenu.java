package com.totemmagic.slots;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    boolean isForeground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = getAssets().openFd("bg_music.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(),
                    descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.setLooping(true);
            if (isForeground) {
                mediaPlayer.setVolume(0.2f, 0.2f);
            } else {
                mediaPlayer.setVolume(0, 0);
            }
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        isForeground = false;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0, 0);
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0.2f, 0.2f);
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView13:
                startActivityForResult(new Intent(MainMenu.this,
                        GameActivity.class), 1);
                finish();
                break;
            case R.id.imageView14:
                exit();
                break;
        }
    }

    private void exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
