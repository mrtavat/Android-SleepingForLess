package com.akexorcist.sleepingforless.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.akexorcist.sleepingforless.bus.BusProvider;
import com.github.ppamorim.dragger.DraggerActivity;

/**
 * Created by Akexorcist on 3/10/2016 AD.
 */
public class SFLDraggerActivity extends DraggerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
    }

    protected void openActivity(Class<? extends Activity> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    protected void openActivity(Class<? extends Activity> activityClass, Bundle bundle) {
        startActivity(new Intent(this, activityClass).putExtras(bundle));
    }

    @Override
    protected void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        BusProvider.getInstance().unregister(this);
    }
}