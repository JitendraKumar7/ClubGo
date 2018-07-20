package in.clubgo.app.popups;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.defuzed.clubgo.R;

public class PopUpEventsMenu extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private static MyClickListener myClickListener;

    public PopUpEventsMenu(Context mContext) {
        super(mContext);

        this.mContext = mContext;


        LayoutInflater inflater = (LayoutInflater) (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_events_menu, null); //custom_layout is your xml file which contains popuplayout
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

        FrameLayout layout1 = (FrameLayout) view.findViewById(R.id.menu_frmLocate);
        FrameLayout layout2 = (FrameLayout) view.findViewById(R.id.menu_frmShare);
        FrameLayout layout3 = (FrameLayout) view.findViewById(R.id.menu_frmBookCab);
        FrameLayout layout4 = (FrameLayout) view.findViewById(R.id.menu_frmCallUs);
        FrameLayout layout5 = (FrameLayout) view.findViewById(R.id.menu_frmCancel);

        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);
        layout4.setOnClickListener(this);
        layout5.setOnClickListener(this);

        setContentView(layout);

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void mShow(View anchor) {
        //showAsDropDown(anchor);
        int u = 3 * (anchor.getHeight());
        setAnimationStyle(R.anim.slide_in);
        showAtLocation(anchor, Gravity.RIGHT | Gravity.BOTTOM, 0, u);
    }

    public void mDismiss() {
        PopUpEventsMenu.this.dismiss();
    }

    @Override
    public void onClick(View v) {
        myClickListener.onItemClick(v);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(View v);
    }
}