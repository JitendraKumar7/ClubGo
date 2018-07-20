package in.clubgo.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.defuzed.clubgo.R;

import java.util.List;

/**
 * Created by Jitendra Soam on 28/3/16.
 */
public class EventDateAdapter extends RecyclerView.Adapter<EventDateAdapter.CustomViewHolder> {

    private Context context;
    private int mPosition = 9999;
    private List<String> mDataList;
    private static MyClickListener myClickListener;

    public EventDateAdapter(Context context, List<String> mDataList) {
        this.context = context;
        this.mDataList = mDataList;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvDays;
        private TextView tvDate;
        private RelativeLayout myBackground;

        public CustomViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tvDays = (TextView) itemView.findViewById(R.id.dayTxt);
            tvDate = (TextView) itemView.findViewById(R.id.dateTxt);
            myBackground = (RelativeLayout) itemView.findViewById(R.id.relativelayout);
        }

        @Override
        public void onClick(View view) {
            if (mPosition == getPosition()) {
                mPosition = 9999;
                notifyDataSetChanged();
                String[] separate = mDataList.get(getPosition()).split(":");
                myClickListener.onItemClick(mPosition, separate[0] + separate[1]);
            } else {
                mPosition = getPosition();
                notifyDataSetChanged();
                String[] separate = mDataList.get(getPosition()).split(":");
                myClickListener.onItemClick(mPosition, separate[0] + separate[1]);
            }

        }
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_date, null);
        CustomViewHolder rcv = new CustomViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        String[] separate = mDataList.get(position).split(":");
        holder.tvDays.setText(separate[2]);
        holder.tvDate.setText(separate[0] + " " + separate[1]);

        if (position == mPosition) {
            holder.tvDays.setTextColor(Color.WHITE);
            holder.tvDate.setTextColor(Color.WHITE);
            holder.myBackground.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else {
            holder.tvDays.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.BLACK);
            holder.myBackground.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        public void onItemClick(int position, String date);
    }

    public void setPosition(int position) {
        this.mPosition = 9999;
    }
}