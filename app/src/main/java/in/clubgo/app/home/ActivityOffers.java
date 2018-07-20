package in.clubgo.app.home;

import android.os.Bundle;
import android.view.View;

import com.defuzed.clubgo.R;
import in.clubgo.app.base.BaseDrawerActivity;

public class ActivityOffers extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        ivBtnOffers.setImageResource(R.mipmap.per_selected);
        tvBtnOffers.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

    @Override
    public void onClick(View v) {

    }
}
