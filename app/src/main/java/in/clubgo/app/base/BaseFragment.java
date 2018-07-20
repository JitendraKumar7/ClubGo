package in.clubgo.app.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.widget.Toast;

import in.clubgo.app.utility.CustomProgressDialog;


@SuppressLint("NewApi")
public abstract class BaseFragment extends Fragment {

    private Toast toast;
    public BaseActivity activity;
    private CustomProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.activity = (BaseActivity) activity;
        progressDialog = new CustomProgressDialog(activity);

    }

    protected void showProgress() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    protected void dismissProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected void makeToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
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
            activity.finish();

    }

    protected void launchIntent(Class<? extends Activity> cls, Bundle bundle, boolean finish) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(bundle);
        startActivity(intent);
        if (finish)
            activity.finish();

    }
}
