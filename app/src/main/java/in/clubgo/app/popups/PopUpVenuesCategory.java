package in.clubgo.app.popups;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.defuzed.clubgo.R;

public class PopUpVenuesCategory extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private static MyClickListener myClickListener;
    TextView txtall, txtCafe, txtPubs;

    public PopUpVenuesCategory(Context mContext) {
        super(mContext);

        this.mContext = mContext;


        LayoutInflater inflater = (LayoutInflater) (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_venues_category, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.popup_layout);

        txtall = (TextView) view.findViewById(R.id.pop_txtAll);
        txtCafe = (TextView) view.findViewById(R.id.pop_txtCafe);
        txtPubs = (TextView) view.findViewById(R.id.pop_txtClubs);

        txtall.setOnClickListener(this);
        txtCafe.setOnClickListener(this);
        txtPubs.setOnClickListener(this);
        layout.setOnClickListener(this);

        setContentView(layout);

        //setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void mShow(View anchor) {
        showAsDropDown(anchor);
    }

    public void mDismiss() {
        PopUpVenuesCategory.this.dismiss();
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
