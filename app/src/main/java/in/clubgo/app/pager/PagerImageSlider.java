package in.clubgo.app.pager;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.defuzed.clubgo.R;
import in.clubgo.app.utility.TouchImageView;
import in.clubgo.app.utility.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * z
 * Created by Jitendra Soam on 2/26/2016.
 */
public class PagerImageSlider extends PagerAdapter {

    private Context mContext;
    private List<String> mDataList;

    public PagerImageSlider(Context mContext, List<String> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();

        View viewItem = inflater.inflate(R.layout.view_image_slider, container, false);
        TouchImageView imageView = (TouchImageView) viewItem.findViewById(R.id.slider_imageView);

        Picasso.with(mContext)
                .load(Constants.BaseImage + mDataList.get(position))
                .placeholder(R.drawable.image_holder)
                .into(imageView);

        ((ViewPager) container).addView(viewItem);

        return viewItem;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // TODO Auto-generated method stub
        ((ViewPager) container).removeView((View) object);
    }
}
