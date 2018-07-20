package in.clubgo.app.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.defuzed.clubgo.R;

import in.clubgo.app.app.AppController;
import in.clubgo.app.listener.ConnectivityReceiver;
import in.clubgo.app.popups.PopUpInternet;
import in.clubgo.app.utility.Constants;
import in.clubgo.app.utility.CustomProgressDialog;
import in.clubgo.app.utility.Utility;


/**
 * Created by Jitendra Soam on 08/08/2016.
 */
public abstract class BaseActivity extends AppCompatActivity
        implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private View v;
    private Toast toast;
    public Activity activity;
    public static String USERUNIQUEID;
    private PopUpInternet popUpInternet;
    private CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        activity = this;
        popUpInternet = new PopUpInternet(activity);
        v = getWindow().getDecorView().getRootView();
        progressDialog = new CustomProgressDialog(activity);
        USERUNIQUEID = Utility.getPref(getApplicationContext(), Constants.USER_ID, null);

        Log.e("BaseActivity", "J " + USERUNIQUEID);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppController.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    protected void showProgress() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    protected void hideDialog() {
        if (popUpInternet.isShowing())
            popUpInternet.mDismiss();
    }

    protected void dismissProgress() {

        if (!((Activity) activity).isFinishing() &&
                progressDialog != null &&
                progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void showDialog(final boolean bool) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                if (!popUpInternet.isShowing() && v != null)
                    popUpInternet.show(v, bool);
            }
        }, 100L);
    }

    protected void makeToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        if (!toast.getView().isShown()) {
            toast.setText(message);
            toast.show();
        }
    }

    protected void launchIntent(Class<? extends Activity> cls, boolean finish) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        if (finish)
            finish();

    }

    protected void launchIntent(Class<? extends Activity> cls, Bundle bundle, boolean finish) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
        if (finish)
            finish();

    }


    /*******************************************************************************************
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *******************************************************************************************/


    private ViewGroup rootLayout;
    private boolean keyboardListenersAttached = false;

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // navigation bar height
            int navigationBarHeight = 0;
            int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                navigationBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // status bar height
            int statusBarHeight = 0;
            resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }

            // display window size for the app layout
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

            // screen height - (user app height + status + nav) ..... if non-zero, then there is a soft keyboard
            int keyboardHeight = rootLayout.getRootView().getHeight() - (statusBarHeight + navigationBarHeight + rect.height());

            if (keyboardHeight <= 0) {
                onHideKeyboard();
            } else {
                onShowKeyboard(keyboardHeight);
            }
        }
    };

    protected void onShowKeyboard(int keyboardHeight) {

    }

    protected void onHideKeyboard() {

    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null)
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        progressDialog = null;

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }

}
