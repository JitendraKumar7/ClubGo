package in.clubgo.app.popups;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.defuzed.clubgo.R;

public class PopUpAddReview extends PopupWindow implements View.OnClickListener {
    private Context mContext;
    private static MyClickListener myClickListener;

    public PopUpAddReview(Context mContext) {
        super(mContext);

        this.mContext = mContext;

        LayoutInflater inflater = (LayoutInflater) (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_add_review, null); //custom_layout is your xml file which contains popuplayout
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

        setContentView(layout);

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void mShow(View anchor) {
        showAsDropDown(anchor);
    }

    public void mDismiss() {
        PopUpAddReview.this.dismiss();
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