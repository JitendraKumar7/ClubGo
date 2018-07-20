package in.clubgo.app.popups;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defuzed.clubgo.R;
import com.squareup.picasso.Picasso;

import in.clubgo.app.modal.ModalEvent;

public class PopUpEventsOffers extends PopupWindow implements View.OnClickListener {
    private ModalEvent bean;
    private Context mContext;
    private ImageView ivEvent, ivVenue;
    private TextView tvDescription, tvEventName, tvVenueName;
    private static MyClickListener myClickListener;

    public PopUpEventsOffers(Context mContext) {
        super(mContext);
        this.mContext = mContext;

        LayoutInflater inflater = (LayoutInflater) (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_event_offer, null); //custom_layout is your xml file which contains popuplayout
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.popuplayout);

        layout.setOnClickListener(this);
        view.findViewById(R.id.linearLayout_event).setOnClickListener(this);
        view.findViewById(R.id.linearLayout_venues).setOnClickListener(this);
        view.findViewById(R.id.event_offer_ivCancel).setOnClickListener(this);

        ivEvent = (ImageView) view.findViewById(R.id.event_offer_ivEvent);
        ivVenue = (ImageView) view.findViewById(R.id.event_offer_ivVenue);

        tvEventName = (TextView) view.findViewById(R.id.event_offer_txtEventName);
        tvVenueName = (TextView) view.findViewById(R.id.event_offer_txtVenueName);
        tvDescription = (TextView) view.findViewById(R.id.event_offer_txtDescription);

        setContentView(layout);

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
    }

    public void show(ModalEvent bean, View anchor) {
        this.bean = bean;
        Picasso.with(mContext)
                .load(bean.getImage())
                .fit().centerInside()
                .placeholder(R.drawable.image_holder)
                .into(ivEvent);

        Picasso.with(mContext)
                .load(bean.getVenueimg())
                .fit().centerInside()
                .placeholder(R.drawable.image_holder)
                .into(ivVenue);

        tvEventName.setText(bean.getName());
        tvVenueName.setText(bean.getVenue());
        tvDescription.setText(bean.getOfferdesc());
        showAtLocation(anchor, Gravity.NO_GRAVITY, 0, 0);
    }

    public void mDismiss() {
        PopUpEventsOffers.this.dismiss();
    }

    @Override
    public void onClick(View v) {
        myClickListener.onItemClick(v, bean);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(View v, ModalEvent bean);
    }
}