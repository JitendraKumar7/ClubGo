package in.clubgo.app.book;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;

import in.clubgo.app.app.AppController;
import in.clubgo.app.base.BaseActivity;
import in.clubgo.app.Tickets.ActivityTickets;
import in.clubgo.app.events.ActivityEventsDetails;
import in.clubgo.app.events.FragmentTickets;
import in.clubgo.app.utility.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ActivityBook extends BaseActivity {

    private static String myPrice = "0";
    private ImageView ivBack, ivMinus1, ivPlus1, ivMinus2, ivPlus2, ivMinus3, ivPlus3;
    private int intCouple = 0, mCouple = 0, intFemale = 0, mFemale = 0, intMale = 0, mMale = 0;
    private TextView tvTitle, tvCouplePriceDiscounted, tvFemalePriceDiscounted, tvMalePriceDiscounted,
            tvCouplePrice, tvFemalePrice, tvMalePrice, tvCouplePriceDescription, tvFemalePriceDescription,
            tvMalePriceDescription, tvCoupleCount, tvFemaleCount, tvMaleCount, tvCouple, tvFemale, tvMale, tvPrice, tvBook, tvCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        inIt();
    }

    private void inIt() {
        ivBack = (ImageView) findViewById(R.id.back);

        tvTitle = (TextView) findViewById(R.id.toolbar_title);
        tvTitle.setText(ActivityEventsDetails.title);


        tvCouplePrice = (TextView) findViewById(R.id.ticket_txtCouplePrice);
        tvFemalePrice = (TextView) findViewById(R.id.ticket_txtFemalePrice);
        tvMalePrice = (TextView) findViewById(R.id.ticket_txtMalePrice);

        tvCouplePriceDiscounted = (TextView) findViewById(R.id.ticket_txtCouplePriceDiscounted);
        tvFemalePriceDiscounted = (TextView) findViewById(R.id.ticket_txtFemalePriceDiscounted);
        tvMalePriceDiscounted = (TextView) findViewById(R.id.ticket_txtMalePriceDiscounted);

        tvCouplePriceDescription = (TextView) findViewById(R.id.ticket_txtCouplePriceDescription);
        tvFemalePriceDescription = (TextView) findViewById(R.id.ticket_txtFemalePriceDescription);
        tvMalePriceDescription = (TextView) findViewById(R.id.ticket_txtMalePriceDescription);

        tvCoupleCount = (TextView) findViewById(R.id.ticket_txtCoupleCount);
        tvFemaleCount = (TextView) findViewById(R.id.ticket_txtFemaleCount);
        tvMaleCount = (TextView) findViewById(R.id.ticket_txtMaleCount);

        tvCouple = (TextView) findViewById(R.id.ticket_txtCouple);
        tvFemale = (TextView) findViewById(R.id.ticket_txtFemale);
        tvMale = (TextView) findViewById(R.id.ticket_txtMale);

        ivMinus1 = (ImageView) findViewById(R.id.ticket_ivMinus1);
        ivPlus1 = (ImageView) findViewById(R.id.ticket_ivPlus1);

        ivMinus2 = (ImageView) findViewById(R.id.ticket_ivMinus2);
        ivPlus2 = (ImageView) findViewById(R.id.ticket_ivPlus2);

        ivMinus3 = (ImageView) findViewById(R.id.ticket_ivMinus3);
        ivPlus3 = (ImageView) findViewById(R.id.ticket_ivPlus3);

        tvPrice = (TextView) findViewById(R.id.ticket_txtPrice);
        tvBook = (TextView) findViewById(R.id.book_txtBookNow);
        tvCall = (TextView) findViewById(R.id.ticket_txtCall);

        tvCouplePriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvFemalePriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvMalePriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);


        if (FragmentTickets.CouplePrice.equals("0")) {
            tvCouplePrice.setText("Free");
            tvCouplePriceDiscounted.setText("");
        } else {
            if (FragmentTickets.CoupleDiscounted.equals("0")) {
                tvCouplePriceDiscounted.setText("");
                tvCouplePrice.setText("\u20B9 " + FragmentTickets.CouplePrice);
            } else {
                tvCouplePrice.setText("\u20B9 " + FragmentTickets.CouplePriceDiscounted);
                tvCouplePriceDiscounted.setText("\u20B9 " + FragmentTickets.CouplePrice);
            }
        }

        if (FragmentTickets.FemalePrice.equals("0")) {
            tvFemalePrice.setText("Free");
            tvFemalePriceDiscounted.setText("");
        } else {
            if (FragmentTickets.FemaleDiscounted.equals("0")) {
                tvFemalePriceDiscounted.setText("");
                tvFemalePrice.setText("\u20B9 " + FragmentTickets.FemalePrice);
            } else {
                tvFemalePrice.setText("\u20B9 " + FragmentTickets.FemalePriceDiscounted);
                tvFemalePriceDiscounted.setText("\u20B9 " + FragmentTickets.FemalePrice);
            }
        }

        if (FragmentTickets.MalePrice.equals("0")) {
            tvMalePrice.setText("Free");
            tvMalePriceDiscounted.setText("");
        } else {
            if (FragmentTickets.MaleDiscounted.equals("0")) {
                tvMalePriceDiscounted.setText("");
                tvMalePrice.setText("\u20B9 " + FragmentTickets.MalePrice);
            } else {
                tvMalePrice.setText("\u20B9 " + FragmentTickets.MalePriceDiscounted);
                tvMalePriceDiscounted.setText("\u20B9 " + FragmentTickets.MalePrice);
            }
        }

        tvCouplePriceDescription.setText(FragmentTickets.CouplePriceDescription);
        tvFemalePriceDescription.setText(FragmentTickets.FemalePriceDescription);
        tvMalePriceDescription.setText(FragmentTickets.MalePriceDescription);


        //set value of count variables
        intCouple = FragmentTickets.CoupleCount;
        intFemale = FragmentTickets.FemaleCount;
        intMale = FragmentTickets.MaleCount;

        String s1 = intCouple + "";
        String s2 = intFemale + "";
        String s3 = intMale + "";

        tvCoupleCount.setText(s1);
        tvFemaleCount.setText(s2);
        tvMaleCount.setText(s3);

        myPrice(intCouple, intFemale, intMale);

        ivBack.setOnClickListener(this);

        ivMinus1.setOnClickListener(this);
        ivPlus1.setOnClickListener(this);

        ivMinus2.setOnClickListener(this);
        ivPlus2.setOnClickListener(this);

        ivMinus3.setOnClickListener(this);
        ivPlus3.setOnClickListener(this);

        tvBook.setOnClickListener(this);
        tvCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back:
                onBackPressed();
                break;

            case R.id.ticket_ivMinus1:
                if (intCouple > 0) {
                    intCouple = intCouple - 1;
                    String s = intCouple + "";
                    tvCoupleCount.setText(s);

                    myPrice(intCouple, intFemale, intMale);
                }
                break;

            case R.id.ticket_ivPlus1:
                intCouple = intCouple + 1;
                String s = intCouple + "";
                tvCoupleCount.setText(s);

                myPrice(intCouple, intFemale, intMale);
                break;

            case R.id.ticket_ivMinus2:
                if (intFemale > 0) {
                    intFemale = intFemale - 1;
                    String s2 = intFemale + "";
                    tvFemaleCount.setText(s2);

                    myPrice(intCouple, intFemale, intMale);
                }
                break;

            case R.id.ticket_ivPlus2:
                intFemale = intFemale + 1;
                String s2 = intFemale + "";
                tvFemaleCount.setText(s2);

                myPrice(intCouple, intFemale, intMale);
                break;

            case R.id.ticket_ivMinus3:
                if (intMale > 0) {
                    intMale = intMale - 1;
                    String s3 = intMale + "";
                    tvMaleCount.setText(s3);

                    myPrice(intCouple, intFemale, intMale);
                }
                break;

            case R.id.ticket_ivPlus3:
                intMale = intMale + 1;
                String s3 = intMale + "";
                tvMaleCount.setText(s3);

                myPrice(intCouple, intFemale, intMale);
                break;

            case R.id.book_txtBookNow:
                showProgress();
                if (intCouple == 0 && intFemale == 0 && intMale == 0) {
                    dismissProgress();
                    makeToast("Please Select Number of People");
                } else {
                    int max = FragmentTickets.max;
                    int min = FragmentTickets.min;

                    if (max != 0 && min != 0 && mCouple != 0 && mMale != 0) {

                        if ((min / max) >= (mMale / mCouple)) {
                            webServices(mCouple + "", mFemale + "", mMale + "", myPrice);
                        } else {
                            dismissProgress();
                            makeToast("Please Follow Couple " + max + ":" + min + " Male Ratio");
                        }
                    } else if (mCouple != 0 || mFemale != 0) {
                        webServices(mCouple + "", mFemale + "", mMale + "", myPrice);
                    } else {
                        dismissProgress();
                        makeToast("Please Follow Couple " + max + ":" + min + " Male Ratio");
                    }
                }
                break;
            case R.id.ticket_txtCall:
                AppController.getInstance().mCalling(this);
                break;
            default:
                break;
        }
    }

    private void myPrice(int couple, int female, int male) {

        int pCouple, pFemale, pMale;
        int cCouple = couple, cFemale = female, cMale = male;

        if (cFemale == cMale) {
            cCouple = cCouple + cFemale;
            cFemale = 0;
            cMale = 0;
        } else if (cFemale > cMale) {
            cCouple = cCouple + cMale;
            cFemale = cFemale - cMale;
            cMale = 0;
        } else {
            cCouple = cCouple + cFemale;
            cMale = cMale - cFemale;
            cFemale = 0;
        }

        try {
            if (FragmentTickets.CoupleDiscounted.equals("0")) {
                pCouple = Integer.parseInt(FragmentTickets.CouplePrice);
            } else {
                pCouple = Integer.parseInt(FragmentTickets.CouplePriceDiscounted);
            }
            if (FragmentTickets.FemaleDiscounted.equals("0")) {
                pFemale = Integer.parseInt(FragmentTickets.FemalePrice);
            } else {
                pFemale = Integer.parseInt(FragmentTickets.FemalePriceDiscounted);
            }
            if (FragmentTickets.MaleDiscounted.equals("0")) {
                pMale = Integer.parseInt(FragmentTickets.MalePrice);
            } else {
                pMale = Integer.parseInt(FragmentTickets.MalePriceDiscounted);
            }

            int price = (pCouple * cCouple) + (pFemale * cFemale) + (pMale * cMale);
            myPrice = price + "";
            Log.e("price", myPrice + " ");
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        this.mCouple = cCouple;
        mFemale = cFemale;
        mMale = cMale;
        tvCouple.setText("Couple " + cCouple);
        tvFemale.setText("Female " + cFemale);
        tvMale.setText("Male " + cMale);
        if (myPrice.equals("0")) {
            tvPrice.setText("Free");
        } else {
            tvPrice.setText("\u20B9 " + myPrice);
        }

    }

    private void webServices(final String coupleTic, final String femaleTic, final String maleTic, final String price) {

        showProgress();
        String[] separate = ActivityEventsDetails.bookDate.split(":");
        final String date = separate[0] + " " + separate[1] + " " + separate[2];

        Log.e("userid", USERUNIQUEID);
        Log.e("tickid", ActivityEventsDetails.ticId);
        Log.e("eventid", ActivityEventsDetails.eventId);
        Log.e("bookdate", date);
        Log.e("maletic", maleTic);
        Log.e("femaletic", femaleTic);
        Log.e("coupletic", coupleTic);
        Log.e("price", price);

        String URLs = Constants.BaseURL + "bookingpost.php";
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        dismissProgress();
                        Log.e("Booking Response", response.toString());
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("msg").equals("1")) {
                                Toast.makeText(getApplicationContext(), "Congratulations!\nyour Ticket is booked", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ActivityBook.this, ActivityTickets.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                ActivityBook.this.finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "Opps! something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                dismissProgress();
                error.printStackTrace();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("price", price);
                params.put("bookdate", date);
                params.put("maletic", maleTic);
                params.put("femaletic", femaleTic);
                params.put("coupletic", coupleTic);
                params.put("userid", USERUNIQUEID);
                params.put("ticid", ActivityEventsDetails.ticId);
                params.put("type", FragmentTickets.type + "");
                params.put("eventid", ActivityEventsDetails.eventId);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");

        try {
            Log.e("Jitendra  Request", new String(strRequest.getBody()));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {

    }

}
