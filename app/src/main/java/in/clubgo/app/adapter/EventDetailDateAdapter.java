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
public class EventDetailDateAdapter extends RecyclerView.Adapter<EventDetailDateAdapter.CustomViewHolder> {

    private Context context;
    private int myPosition = 0;
    private List<String> mDataList;
    private static MyClickListener myClickListener;

    public EventDetailDateAdapter(Context context, List<String> mDataList) {
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
            notifyItemChanged(getPosition());
            myPosition = getPosition();
            notifyDataSetChanged();

            myClickListener.onItemClick(mDataList.get(getPosition()));
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

        // Set the selected state of the row depending on the position

        String[] separate = mDataList.get(position).split(":");
        holder.tvDays.setText(separate[2]);
        holder.tvDate.setText(separate[0] + " " + separate[1]);

        if (position == myPosition) {
            holder.myBackground.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.tvDays.setTextColor(Color.WHITE);
            holder.tvDate.setTextColor(Color.WHITE);
        } else {
            holder.myBackground.setBackgroundColor(context.getResources().getColor(R.color.gray));
            holder.tvDays.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.BLACK);
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
        public void onItemClick(String dates);
    }

}