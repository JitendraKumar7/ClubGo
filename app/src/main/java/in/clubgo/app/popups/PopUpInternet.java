package in.clubgo.app.popups;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defuzed.clubgo.R;

import in.clubgo.app.app.AppController;

public class PopUpInternet extends PopupWindow implements View.OnClickListener {

    private TextView tvCall;
    private Context mContext;

    public PopUpInternet(Context mContext) {
        super(mContext);
        this.mContext = mContext;

        LayoutInflater inflater = (LayoutInflater) (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_internet, null); //custom_layout is your xml file which contains popuplayout
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.popuplayout);

        tvCall = (TextView) view.findViewById(R.id.popup_internet_txtCall);
        tvCall.setOnClickListener(this);

        setContentView(layout);

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void show(View anchor, boolean b) {
        if (b) {
            tvCall.setVisibility(View.GONE);
        }
        showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
    }

    public void mDismiss() {
        PopUpInternet.this.dismiss();
    }

    @Override
    public void onClick(View v) {
        //myClickListener.onItemClick(v);
        AppController.getInstance().mCalling(mContext);
    }
}