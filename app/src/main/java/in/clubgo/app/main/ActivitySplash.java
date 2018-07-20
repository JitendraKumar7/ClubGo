package in.clubgo.app.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.defuzed.clubgo.R;

import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.home.ActivityHome;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.utility.Utility;

public class ActivitySplash extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent Status Bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_splash);

        if (ActivityHome.jitendra_soam != null) {
            ActivityHome.jitendra_soam = null;
        }

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    if (Utility.getPref(getApplicationContext(), Constants.USER_ID, null) != null) {
                        launchIntent(ActivityHome.class, true);
                    } else {
                        launchIntent(ActivityStartUp.class, true);
                    }
                }
            }
        };
        timerThread.start();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }
}


