package com.totemmagic.slots;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.AppsFlyerLibCore;
import com.onesignal.OneSignal;

import java.util.Map;

public class MainActivity extends AppCompatActivity {
    final Handler handler = new Handler();
    static NetworkInfo netInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OneSignal.setAppId(BuildConfig.ONESIGNAL_DEV_KEY);
        OneSignal.initWithContext(this);
        startActivityForResult(new Intent(MainActivity.this, SplashScreenActivity.class), 0);
        handler.postDelayed(() -> {
            if (!isOnline(MainActivity.this)) {
                finishActivity(0);
                startActivityForResult(new Intent(MainActivity.this, ConnectionCheckActivity.class), 1);
            }
            finishActivity(0);
            startActivityForResult(new Intent(MainActivity.this, MainMenu.class), 2);
            finish();
        }, 3000);

        AppsFlyerConversionListener conversionListener = new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    Log.d(AppsFlyerLibCore.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onConversionDataFail(String errorMessage) {
                Log.d(AppsFlyerLibCore.LOG_TAG, "error getting conversion data: " + errorMessage);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> conversionData) {
                for (String attrName : conversionData.keySet()) {
                    Log.d(AppsFlyerLibCore.LOG_TAG, "attribute: " + attrName + " = " + conversionData.get(attrName));
                }
            }

            @Override
            public void onAttributionFailure(String errorMessage) {
                Log.d(AppsFlyerLibCore.LOG_TAG, "error onAttributionFailure : " + errorMessage);
            }
        };
        AppsFlyerLib.getInstance().init(BuildConfig.APPFLYER_DEV_KEY, conversionListener, getApplicationContext());
        AppsFlyerLib.getInstance().start(this);
        AppsFlyerLib.getInstance().setDebugLog(true);
    }

    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
