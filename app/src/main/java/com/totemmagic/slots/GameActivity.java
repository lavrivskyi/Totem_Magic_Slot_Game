package com.totemmagic.slots;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.InterstitialAd;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {
    Toast notify;
    MediaPlayer mediaPlayer;
    boolean isForeground;
    SoundPool sounds;
    int winSound, buttonSound, creditSound, spinSound;
    SharedPreferences sharedPreferences;
    Timer timer = new Timer();
    TimerTask rotationTask;
    int numRotations, item0, item1, item2;
    int bet = 0;
    int win = 0;
    AnimatorSet winAnimation = new AnimatorSet();
    AnimatorSet creditAnimation = new AnimatorSet();
    AnimatorSet betAnimation = new AnimatorSet();
    final int NUM_ROTATION = 15; // number of rotations
    final int WIN_PERCENT = 15; // percent to win besides normal chance to win
    int numItems = 7; // number of slot items
    int credit = 1000; // credit
    int[] bets = {10, 20, 30, 40, 50}; // bets
    int[][] wins = {{2500, 1000, 500, 200, 100, 50, 20},
            {5000, 2000, 1000, 400, 200, 100, 40},
            {7500, 3000, 1500, 600, 300, 150, 60},
            {10000, 4000, 2000, 800, 400, 200, 80},
            {12500, 5000, 2500, 1000, 500, 250, 100},
            {15000, 6000, 3000, 1200, 600, 300, 120}
    }; // probable wins array

    AdView adMobBanner;
    InterstitialAd adMobInterstitial;
    AdRequest adRequest;
    final int interstitialAdInterval = 1; // show admob Interstitial after each N wins

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        adMob();
        mediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor descriptor = getAssets().openFd("bg_music.mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();
            mediaPlayer.setLooping(true);
            if (isForeground)
                mediaPlayer.setVolume(0.2f, 0.2f);
            else
                mediaPlayer.setVolume(0, 0);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
        sounds = new SoundPool.Builder().setMaxStreams(5).build();
        try {
            winSound = sounds.load(getAssets().openFd("win.mp3"), 1);
            buttonSound = sounds.load(getAssets().openFd("bet.mp3"), 1);
            creditSound = sounds.load(getAssets().openFd("sndCredit.mp3"), 1);
            spinSound = sounds.load(getAssets().openFd("barabanyi.mp3"), 1);
        } catch (IOException e) {
            Log.e("ERROR", e.getMessage());
        }

        findViewById(R.id.root).setOnSystemUiVisibilityChangeListener(visibility -> hideNavigation());

        Typeface font = Typeface.createFromAsset(getAssets(), "sancreek.ttf");
        ((TextView) findViewById(R.id.textView)).setTypeface(font);
        ((TextView) findViewById(R.id.textView2)).setTypeface(font);
        ((TextView) findViewById(R.id.textView3)).setTypeface(font);

        updateText();

        ((ImageView) findViewById(R.id.imageView10)).setImageResource(getResources().getIdentifier("item" + Math.round(Math.random() * (numItems - 1)), "drawable", getPackageName()));
        ((ImageView) findViewById(R.id.imageView11)).setImageResource(getResources().getIdentifier("item" + Math.round(Math.random() * (numItems - 1)), "drawable", getPackageName()));
        ((ImageView) findViewById(R.id.imageView12)).setImageResource(getResources().getIdentifier("item" + Math.round(Math.random() * (numItems - 1)), "drawable", getPackageName()));

        List<Animator> anim_list = new ArrayList<>();
        ObjectAnimator anim;
        anim = ObjectAnimator.ofFloat(findViewById(R.id.mainLayout), "scaleY", 1.1f);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim_list.add(anim);
        AnimatorSet animStart = new AnimatorSet();
        animStart.playTogether(anim_list);
        animStart.setDuration(3000);
        animStart.start();

        anim_list = new ArrayList<>();
        anim = ObjectAnimator.ofFloat(findViewById(R.id.textView3), "scaleX", 0.7f);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim_list.add(anim);
        anim = ObjectAnimator.ofFloat(findViewById(R.id.textView3), "scaleY", 0.7f);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim_list.add(anim);
        winAnimation.playTogether(anim_list);
        winAnimation.setDuration(100);

        anim_list.clear();
        anim = ObjectAnimator.ofFloat(findViewById(R.id.textView2), "scaleX", 0.7f);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim_list.add(anim);
        anim = ObjectAnimator.ofFloat(findViewById(R.id.textView2), "scaleY", 0.7f);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim_list.add(anim);
        creditAnimation.playTogether(anim_list);
        creditAnimation.setDuration(100);

        anim_list.clear();
        anim = ObjectAnimator.ofFloat(findViewById(R.id.textView), "scaleX", 0.7f);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim_list.add(anim);
        anim = ObjectAnimator.ofFloat(findViewById(R.id.textView), "scaleY", 0.7f);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.setRepeatCount(1);
        anim_list.add(anim);
        betAnimation.playTogether(anim_list);
        betAnimation.setDuration(100);
    }

    @Override
    protected void onPause() {
        isForeground = false;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(0, 0);
        }
        if (adMobBanner != null) {
            adMobBanner.pause();
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
        if (adMobBanner != null) {
            adMobBanner.resume();
        }
    }

    @Override
    public void onBackPressed() {
        exit();
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        winAnimation.cancel();
        creditAnimation.cancel();
        betAnimation.cancel();
        if (notify != null) {
            notify.cancel();
        }
        if (adMobBanner != null) {
            adMobBanner.setAdListener(null);
            adMobBanner.destroy();
            adMobBanner = null;
        }
        if (adMobInterstitial != null) {
            adMobInterstitial.setAdListener(null);
            adMobInterstitial = null;
        }
        adRequest = null;
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideNavigation();
        }
    }

    void hideNavigation() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    void notify(int message) {
        if (notify != null) {
            notify.cancel();
        }
        notify = Toast.makeText(this, getString(message), Toast.LENGTH_SHORT);
        notify.show();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView3:
                bet = bet < bets.length - 1 ? bet + 1 : 0;
                updateText();
                betAnimation.start();
                if (isForeground) {
                    sounds.play(buttonSound, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.imageView4:
                bet = bets.length - 1;
                updateText();
                betAnimation.start();
                if (isForeground) {
                    sounds.play(buttonSound, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.imageView2:
                spin();
                break;
        }
    }

    void spin() {
        creditAnimation.start();
        if (credit < bets[bet]) {
            noCredit();
            return;
        }

        credit -= bets[bet];
        updateText();
        findViewById(R.id.imageView3).setEnabled(false);
        findViewById(R.id.imageView4).setEnabled(false);
        findViewById(R.id.imageView2).setEnabled(false);

        if (isForeground) {
            sounds.play(spinSound, 1, 1, 0, 0, 1);
        }

        numRotations = 0;
        if (rotationTask != null) {
            rotationTask.cancel();
        }
        rotationTask = new rotation();
        timer.schedule(rotationTask, 0, 100);
    }

    private class rotation extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(() -> {
                numRotations++;
                item0 = (int) Math.round(Math.random() * (numItems - 1));
                if (numRotations == NUM_ROTATION && Math.random() <= (float) WIN_PERCENT * 0.01f) {
                    item1 = item2 = item0;
                } else {
                    item1 = (int) Math.round(Math.random() * (numItems - 1));
                    item2 = (int) Math.round(Math.random() * (numItems - 1));
                }
                ((ImageView) findViewById(R.id.imageView10)).setImageResource(getResources().getIdentifier("item" + item0, "drawable", getPackageName()));
                ((ImageView) findViewById(R.id.imageView11)).setImageResource(getResources().getIdentifier("item" + item1, "drawable", getPackageName()));
                ((ImageView) findViewById(R.id.imageView12)).setImageResource(getResources().getIdentifier("item" + item2, "drawable", getPackageName()));
                if (numRotations == NUM_ROTATION) {
                    rotationTask.cancel();
                    findViewById(R.id.imageView3).setEnabled(true);
                    findViewById(R.id.imageView4).setEnabled(true);
                    findViewById(R.id.imageView2).setEnabled(true);
                    if (item0 == item1 && item0 == item2) {
                        addMoney(wins[bet][item0]);
                    }
                }
            });
        }
    }

    @SuppressLint("SetTextI18n")
    void updateText() {
        ((TextView) findViewById(R.id.textView2)).setText(getString(R.string.money) + credit);
        ((TextView) findViewById(R.id.textView)).setText(getString(R.string.money) + bets[bet]);
        ((TextView) findViewById(R.id.textView3)).setText(getString(R.string.money) + win);
    }

    void exit() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Back to main menu?");
        alertDialogBuilder
                .setMessage("YES for exit.")
                .setCancelable(false)
                .setPositiveButton("Go back",
                        (dialog, id) -> {
                            startActivityForResult(new Intent(GameActivity.this, MainMenu.class),1);
                            finish();
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void noCredit() {
        notify(R.string.no_credit);
        if (isForeground) {
            sounds.play(creditSound, 1, 1, 0, 0, 1);
        }
    }

    void addMoney(int money) {
        win += money;
        updateText();

        if (isForeground) {
            sounds.play(winSound, 1, 1, 0, 0, 1);
        }
        winAnimation.start();
        showAdmobInterstitial();
    }

    void showAdmobInterstitial() {
        if (adMobInterstitial != null) {
            sharedPreferences.edit().putInt("admob", !sharedPreferences.contains("admob") ? 1 : sharedPreferences.getInt("admob", 0) + 1).apply();
            if (adMobInterstitial.isLoaded()) {
                if (sharedPreferences.getInt("admob", 0) >= interstitialAdInterval) {
                    sharedPreferences.edit().putInt("admob", 0).apply();
                    adMobInterstitial.show();
                }
            } else if (!adMobInterstitial.isLoading())
                adMobInterstitial.loadAd(adRequest);
        }
    }

    void adMob() {
        if (getResources().getBoolean(R.bool.show_admob)) {
            MobileAds.initialize(this);
            AdRequest.Builder builder = new AdRequest.Builder();
            if (getResources().getBoolean(R.bool.admob_test))
                builder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice(
                        MD5(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
            adRequest = builder.build();
            adMobInterstitial = new InterstitialAd(this);
            adMobInterstitial.setAdUnitId(getString(R.string.adMob_interstitial));
            adMobInterstitial.setAdListener(new AdListener() {
                public void onAdClosed() {
                    adMobInterstitial.loadAd(adRequest);
                }
            });
            adMobInterstitial.loadAd(adRequest);
        }
    }

    String MD5(String str) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < array.length; ++i)
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            return sb.toString().toUpperCase(Locale.ENGLISH);
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.e("ERROR", e.getMessage());
        }
        return null;
    }
}
