package in.clubgo.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import in.clubgo.app.app.AppController;
import in.clubgo.app.venues.ActivityVenuesDetails;
import in.clubgo.app.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by Jitendra Soam on 20/4/16.
 */
public class FragmentOpeningHours extends Fragment {


    public FragmentOpeningHours() {

    }

    private TextView tvMonday1, tvTuesday1, tvWednesday1, tvThursday1, tvFriday1, tvSaturday1,
            tvSunday1, tvMonday2, tvTuesday2, tvWednesday2, tvThursday2, tvFriday2, tvSaturday2, tvSunday2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opening_hours, container, false);

        tvSunday1 = (TextView) view.findViewById(R.id.open_txtSunday1);
        tvMonday1 = (TextView) view.findViewById(R.id.open_txtMonday1);
        tvTuesday1 = (TextView) view.findViewById(R.id.open_txtTuesday1);
        tvWednesday1 = (TextView) view.findViewById(R.id.open_txtWednesday1);
        tvThursday1 = (TextView) view.findViewById(R.id.open_txtThursday1);
        tvFriday1 = (TextView) view.findViewById(R.id.open_txtFriday1);
        tvSaturday1 = (TextView) view.findViewById(R.id.open_txtSaturday1);

        tvSunday2 = (TextView) view.findViewById(R.id.open_txtSunday2);
        tvMonday2 = (TextView) view.findViewById(R.id.open_txtMonday2);
        tvTuesday2 = (TextView) view.findViewById(R.id.open_txtTuesday2);
        tvWednesday2 = (TextView) view.findViewById(R.id.open_txtWednesday2);
        tvThursday2 = (TextView) view.findViewById(R.id.open_txtThursday2);
        tvFriday2 = (TextView) view.findViewById(R.id.open_txtFriday2);
        tvSaturday2 = (TextView) view.findViewById(R.id.open_txtSaturday2);

        webServices(ActivityVenuesDetails.itemId);
        return view;
    }

    private void webServices(final String ids) {

        String URLs = Constants.BaseURL + "venuedetail.php?open=" + ids;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("venue open detail", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONObject jsonObject = mResponse.getJSONObject("open");

                            tvSunday2.setText(jsonObject.getString("sunday"));
                            tvMonday2.setText(jsonObject.getString("monday"));
                            tvTuesday2.setText(jsonObject.getString("tuesday"));
                            tvWednesday2.setText(jsonObject.getString("wednesday"));
                            tvThursday2.setText(jsonObject.getString("thursday"));
                            tvFriday2.setText(jsonObject.getString("friday"));
                            tvSaturday2.setText(jsonObject.getString("saturday"));

                            if (jsonObject.getString("sunday").equals("closed")) {
                                setColor(0);
                            }
                            if (jsonObject.getString("monday").equals("closed")) {
                                setColor(1);
                            }
                            if (jsonObject.getString("tuesday").equals("closed")) {
                                setColor(2);
                            }
                            if (jsonObject.getString("wednesday").equals("closed")) {
                                setColor(3);
                            }
                            if (jsonObject.getString("thursday").equals("closed")) {
                                setColor(4);
                            }
                            if (jsonObject.getString("friday").equals("closed")) {
                                setColor(5);
                            }
                            if (jsonObject.getString("saturday").equals("closed")) {
                                setColor(6);
                            }

                            Calendar calendar = Calendar.getInstance();
                            int day = calendar.get(Calendar.DAY_OF_WEEK);

                            switch (day) {
                                case Calendar.SUNDAY:
                                    tvSunday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvSunday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                case Calendar.MONDAY:
                                    tvMonday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvMonday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                case Calendar.TUESDAY:
                                    tvTuesday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvTuesday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                case Calendar.WEDNESDAY:
                                    tvWednesday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvWednesday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                case Calendar.THURSDAY:
                                    tvThursday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvThursday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                case Calendar.FRIDAY:
                                    tvFriday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvFriday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                case Calendar.SATURDAY:
                                    tvSaturday1.setTextColor(getResources().getColor(R.color.indicator));
                                    tvSaturday2.setTextColor(getResources().getColor(R.color.indicator));
                                    break;

                                default:
                                    break;
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                error.printStackTrace();

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void setColor(final int day) {
        switch (day) {
            case 0:
                //tvSunday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvSunday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case 1:
                //tvMonday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvMonday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case 2:
                //tvTuesday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvTuesday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case 3:
                //tvWednesday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvWednesday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case 4:
                //tvThursday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvThursday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case 5:
                //tvFriday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvFriday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            case 6:
                //tvSaturday1.setTextColor(getResources().getColor(R.color.colorPrimary));
                tvSaturday2.setTextColor(getResources().getColor(R.color.colorPrimary));
                break;

            default:
                break;
        }

    }

}


