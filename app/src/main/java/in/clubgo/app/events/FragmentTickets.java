package in.clubgo.app.events;


import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.defuzed.clubgo.R;
import in.clubgo.app.adapter.EventDetailDateAdapter;
import in.clubgo.app.app.AppController;
import in.clubgo.app.modal.ModalTicket;
import in.clubgo.app.utility.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTickets extends Fragment implements View.OnClickListener {

    private List<String> mDateList;
    private RecyclerView mRecyclerView;
    private EventDetailDateAdapter mAdapter;
    private List<ModalTicket> mTicket, mBefore, mAfter;

    public static int pos = 0, type = 0, max = 0, min = 0,
            CoupleCount = 0, FemaleCount = 0, MaleCount = 0;

    private ImageView ivMinus1, ivPlus1, ivMinus2, ivPlus2, ivMinus3, ivPlus3;
    private TextView tvGreen, tvTime1, tvTime2, tvTicket1, tvTicket2, tvTicket3,
            tvTicket4, tvCouplePriceDiscounted, tvFemalePriceDiscounted,
            tvMalePriceDiscounted, tvCouplePrice, tvFemalePrice, tvMalePrice, tvCouplePriceDescription,
            tvFemalePriceDescription, tvMalePriceDescription, tvCoupleCount, tvFemaleCount, tvMaleCount;

    public static String CouplePrice, CouplePriceDiscounted, CouplePriceDescription,
            CoupleDiscounted, FemalePrice, FemalePriceDiscounted, FemaleDiscounted,
            FemalePriceDescription, MalePrice, MalePriceDiscounted, MaleDiscounted, MalePriceDescription;


    public FragmentTickets() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_event_tickets, container, false);
        pos = 0;
        type = 0;
        MaleCount = 0;
        FemaleCount = 0;
        CoupleCount = 0;

        mTicket = new ArrayList<>();
        mBefore = new ArrayList<>();
        mAfter = new ArrayList<>();
        mDateList = new ArrayList<>();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvDate);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new EventDetailDateAdapter(getActivity(), mDateList);
        mRecyclerView.setAdapter(mAdapter);

        tvGreen = (TextView) view.findViewById(R.id.ticket_txtGreen);

        tvTime1 = (TextView) view.findViewById(R.id.ticket_txtTime1);
        tvTime2 = (TextView) view.findViewById(R.id.ticket_txtTime2);

        tvTicket1 = (TextView) view.findViewById(R.id.ticket_txtTicket1);
        tvTicket2 = (TextView) view.findViewById(R.id.ticket_txtTicket2);
        tvTicket3 = (TextView) view.findViewById(R.id.ticket_txtTicket3);
        tvTicket4 = (TextView) view.findViewById(R.id.ticket_txtTicket4);

        tvCouplePrice = (TextView) view.findViewById(R.id.ticket_txtCouplePrice);
        tvFemalePrice = (TextView) view.findViewById(R.id.ticket_txtFemalePrice);
        tvMalePrice = (TextView) view.findViewById(R.id.ticket_txtMalePrice);

        tvCouplePriceDiscounted = (TextView) view.findViewById(R.id.ticket_txtCouplePriceDiscounted);
        tvFemalePriceDiscounted = (TextView) view.findViewById(R.id.ticket_txtFemalePriceDiscounted);
        tvMalePriceDiscounted = (TextView) view.findViewById(R.id.ticket_txtMalePriceDiscounted);


        tvCouplePriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvFemalePriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        tvMalePriceDiscounted.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        tvCouplePriceDescription = (TextView) view.findViewById(R.id.ticket_txtCouplePriceDescription);
        tvFemalePriceDescription = (TextView) view.findViewById(R.id.ticket_txtFemalePriceDescription);
        tvMalePriceDescription = (TextView) view.findViewById(R.id.ticket_txtMalePriceDescription);

        tvCoupleCount = (TextView) view.findViewById(R.id.ticket_txtCoupleCount);
        tvFemaleCount = (TextView) view.findViewById(R.id.ticket_txtFemaleCount);
        tvMaleCount = (TextView) view.findViewById(R.id.ticket_txtMaleCount);

        ivMinus1 = (ImageView) view.findViewById(R.id.ticket_ivMinus1);
        ivPlus1 = (ImageView) view.findViewById(R.id.ticket_ivPlus1);

        ivMinus2 = (ImageView) view.findViewById(R.id.ticket_ivMinus2);
        ivPlus2 = (ImageView) view.findViewById(R.id.ticket_ivPlus2);

        ivMinus3 = (ImageView) view.findViewById(R.id.ticket_ivMinus3);
        ivPlus3 = (ImageView) view.findViewById(R.id.ticket_ivPlus3);

        inIt();
        return view;
    }

    private void inIt() {
        webService(((ActivityEventsDetails) getActivity()).eventId);

        tvTime1.setOnClickListener(this);
        tvTime2.setOnClickListener(this);

        tvTicket1.setOnClickListener(this);
        tvTicket2.setOnClickListener(this);
        tvTicket3.setOnClickListener(this);
        tvTicket4.setOnClickListener(this);

        ivMinus1.setOnClickListener(this);
        ivPlus1.setOnClickListener(this);

        ivMinus2.setOnClickListener(this);
        ivPlus2.setOnClickListener(this);

        ivMinus3.setOnClickListener(this);
        ivPlus3.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        ((EventDetailDateAdapter) mAdapter).setMyClickListener(new EventDetailDateAdapter.MyClickListener() {
            @Override
            public void onItemClick(String dates) {
                ActivityEventsDetails.bookDate = dates.trim();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ticket_txtTime1:
                tvTime1.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTime1.setTextColor(getResources().getColor(R.color.white));

                tvTime2.setBackgroundColor(getResources().getColor(R.color.white));
                tvTime2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                type = 0;
                if (mTicket.size() > 0) {
                    addData(pos);
                    MaleCount = 0;
                    FemaleCount = 0;
                    CoupleCount = 0;
                    tvMaleCount.setText("0");
                    tvFemaleCount.setText("0");
                    tvCoupleCount.setText("0");
                }
                break;
            case R.id.ticket_txtTime2:
                tvTime1.setBackgroundColor(getResources().getColor(R.color.white));
                tvTime1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTime2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTime2.setTextColor(getResources().getColor(R.color.white));
                type = 1;
                if (mTicket.size() > 0) {
                    addData(pos);
                    MaleCount = 0;
                    FemaleCount = 0;
                    CoupleCount = 0;
                    tvMaleCount.setText("0");
                    tvFemaleCount.setText("0");
                    tvCoupleCount.setText("0");
                }
                break;

            case R.id.ticket_txtTicket1:
                tvTicket1.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTicket1.setTextColor(getResources().getColor(R.color.white));

                tvTicket2.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket3.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket4.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                pos = 0;
                addData(pos);
                break;
            case R.id.ticket_txtTicket2:
                tvTicket1.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTicket2.setTextColor(getResources().getColor(R.color.white));

                tvTicket3.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket4.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                pos = 1;
                addData(pos);
                break;
            case R.id.ticket_txtTicket3:
                tvTicket1.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket2.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTicket3.setTextColor(getResources().getColor(R.color.white));

                tvTicket4.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket4.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                pos = 2;
                addData(pos);
                break;
            case R.id.ticket_txtTicket4:
                tvTicket1.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket2.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket2.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket3.setBackgroundColor(getResources().getColor(R.color.white));
                tvTicket3.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                tvTicket4.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTicket4.setTextColor(getResources().getColor(R.color.white));
                pos = 3;
                addData(pos);
                break;

            case R.id.ticket_ivMinus1:
                if (CoupleCount > 0) {
                    CoupleCount = CoupleCount - 1;
                    String s = CoupleCount + "";
                    tvCoupleCount.setText(s);
                }
                break;

            case R.id.ticket_ivPlus1:
                CoupleCount = CoupleCount + 1;
                String s = CoupleCount + "";
                tvCoupleCount.setText(s);
                break;

            case R.id.ticket_ivMinus2:
                if (FemaleCount > 0) {
                    FemaleCount = FemaleCount - 1;
                    String s2 = FemaleCount + "";
                    tvFemaleCount.setText(s2);
                }
                break;

            case R.id.ticket_ivPlus2:
                FemaleCount = FemaleCount + 1;
                String s2 = FemaleCount + "";
                tvFemaleCount.setText(s2);
                break;

            case R.id.ticket_ivMinus3:
                if (MaleCount > 0) {
                    MaleCount = MaleCount - 1;
                    String s3 = MaleCount + "";
                    tvMaleCount.setText(s3);
                }
                break;

            case R.id.ticket_ivPlus3:
                MaleCount = MaleCount + 1;
                String s3 = MaleCount + "";
                tvMaleCount.setText(s3);
                break;

            default:
                break;
        }

    }

    private void webService(final String itemId) {
        String URLs = Constants.BaseURL + "eventdetailjson.php?e_ticket=" + itemId;
        // Request a string response
        StringRequest strRequest = new StringRequest(Request.Method.POST, URLs,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Result handling
                        Log.e("event ticket", response.toString());
                        try {
                            JSONObject mResponse = new JSONObject(response);

                            JSONObject jsonObject = mResponse.getJSONObject("date");
                            JSONArray datesArray = jsonObject.getJSONArray("dates");
                            for (int i = 0; i < datesArray.length(); i++) {
                                mDateList.add(datesArray.getString(i).trim());
                                if (i == 0) {
                                    ActivityEventsDetails.bookDate = datesArray.getString(i).trim();
                                }
                            }

                            JSONArray ticket = mResponse.getJSONArray("ticket");
                            JSONArray before = mResponse.getJSONArray("before");
                            JSONArray after = mResponse.getJSONArray("after");

                            for (int i = 0; i < ticket.length(); i++) {

                                JSONObject ticket1 = ticket.getJSONObject(i);
                                JSONObject before1 = before.getJSONObject(i);
                                JSONObject after1 = after.getJSONObject(i);

                                ModalTicket listTic = new ModalTicket();
                                listTic.setType_id(ticket1.getString("type_id"));
                                listTic.setTicket_id(ticket1.getString("ticket_id"));
                                listTic.setName(ticket1.getString("name"));
                                listTic.setBefor(ticket1.getString("befor"));
                                listTic.setAfter(ticket1.getString("after"));
                                listTic.setGreen(ticket1.getString("green"));
                                listTic.setTerms(ticket1.getString("terms"));
                                String CurrentString = ticket1.getString("couple_ratio");
                                String[] separated = CurrentString.split(":");
                                try {
                                    listTic.setMax(Integer.parseInt(separated[0]));
                                } catch (NumberFormatException nfe) {
                                    listTic.setMax(0);
                                }
                                try {
                                    listTic.setMin(Integer.parseInt(separated[1]));
                                } catch (NumberFormatException nfe) {
                                    listTic.setMin(0);
                                }
                                mTicket.add(listTic);

                                ModalTicket listBe = new ModalTicket();
                                listBe.setCoupleprice(before1.getString("coupleprice"));
                                listBe.setCouple_dis(before1.getString("couple_dis"));
                                listBe.setCoupledesc(before1.getString("coupledesc"));
                                listBe.setC_discount(before1.getString("c_discount"));

                                listBe.setMaleprice(before1.getString("maleprice"));
                                listBe.setMale_dis(before1.getString("male_dis"));
                                listBe.setMaledesc(before1.getString("maledesc"));
                                listBe.setM_discount(before1.getString("m_discount"));

                                listBe.setFemaleprice(before1.getString("femaleprice"));
                                listBe.setFemale_dis(before1.getString("female_dis"));
                                listBe.setFemaledesc(before1.getString("femaledesc"));
                                listBe.setF_discount(before1.getString("f_discount"));
                                mBefore.add(listBe);


                                ModalTicket listAf = new ModalTicket();
                                listAf.setCoupleprice(after1.getString("coupleprice"));
                                listAf.setCouple_dis(after1.getString("couple_dis"));
                                listAf.setCoupledesc(after1.getString("coupledesc"));
                                listAf.setC_discount(after1.getString("lc_discount"));

                                listAf.setMaleprice(after1.getString("maleprice"));
                                listAf.setMale_dis(after1.getString("male_dis"));
                                listAf.setMaledesc(after1.getString("maledesc"));
                                listAf.setM_discount(after1.getString("lm_discount"));

                                listAf.setFemaleprice(after1.getString("femaleprice"));
                                listAf.setFemale_dis(after1.getString("female_dis"));
                                listAf.setFemaledesc(after1.getString("femaledesc"));
                                listAf.setF_discount(after1.getString("lf_discount"));
                                mAfter.add(listAf);

                                switch (i) {
                                    case 0:
                                        tvTicket1.setText(ticket1.getString("name"));
                                        tvTicket1.setVisibility(View.VISIBLE);
                                        addData(0);
                                        break;
                                    case 1:
                                        tvTicket2.setText(ticket1.getString("name"));
                                        tvTicket2.setVisibility(View.VISIBLE);
                                        break;
                                    case 2:
                                        tvTicket3.setText(ticket1.getString("name"));
                                        tvTicket3.setVisibility(View.VISIBLE);
                                        break;
                                    case 3:
                                        tvTicket4.setText(ticket1.getString("name"));
                                        tvTicket4.setVisibility(View.VISIBLE);
                                        break;
                                    default:
                                        break;
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
                error.printStackTrace();

            }
        });
        strRequest.setRetryPolicy(new DefaultRetryPolicy(7000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strRequest, "strRequest");
    }

    private void addData(int i) {
        ActivityEventsDetails.title = mTicket.get(i).getName();
        ActivityEventsDetails.ticId = mTicket.get(i).getTicket_id();

        if (!(mTicket.get(i).getGreen()).equals("")) {
            tvGreen.setVisibility(View.VISIBLE);
            tvGreen.setText(mTicket.get(i).getGreen());
        }
        max = mTicket.get(i).getMax();
        min = mTicket.get(i).getMin();

        tvTime2.setText("After " + mTicket.get(i).getAfter());
        tvTime1.setText("Before " + mTicket.get(i).getBefor());

        if (type == 0) {

            ActivityEventsDetails.time = mTicket.get(i).getBefor();
            CouplePrice = mBefore.get(i).getCoupleprice();
            CoupleDiscounted = mBefore.get(i).getC_discount();
            CouplePriceDiscounted = mBefore.get(i).getCouple_dis();
            CouplePriceDescription = mBefore.get(i).getCoupledesc();

            FemalePrice = mBefore.get(i).getFemaleprice();
            FemaleDiscounted = mBefore.get(i).getF_discount();
            FemalePriceDiscounted = mBefore.get(i).getFemale_dis();
            FemalePriceDescription = mBefore.get(i).getFemaledesc();

            MalePrice = mBefore.get(i).getMaleprice();
            MaleDiscounted = mBefore.get(i).getM_discount();
            MalePriceDiscounted = mBefore.get(i).getMale_dis();
            MalePriceDescription = mBefore.get(i).getMaledesc();

            if (mBefore.get(i).getCoupleprice().equals("0")) {
                tvCouplePrice.setText("Free");
                tvCouplePriceDiscounted.setText("");
            } else {
                if (mBefore.get(i).getC_discount().equals("0")) {
                    tvCouplePriceDiscounted.setText("");
                    tvCouplePrice.setText("\u20B9 " + mBefore.get(i).getCoupleprice());
                } else {
                    tvCouplePrice.setText("\u20B9 " + mBefore.get(i).getCouple_dis());
                    tvCouplePriceDiscounted.setText("\u20B9 " + mBefore.get(i).getCoupleprice());
                }
            }

            if (mBefore.get(i).getFemaleprice().equals("0")) {
                tvFemalePrice.setText("Free");
                tvFemalePriceDiscounted.setText("");
            } else {
                if (mBefore.get(i).getF_discount().equals("0")) {
                    tvFemalePriceDiscounted.setText("");
                    tvFemalePrice.setText("\u20B9 " + mBefore.get(i).getFemaleprice());
                } else {
                    tvFemalePrice.setText("\u20B9 " + mBefore.get(i).getFemale_dis());
                    tvFemalePriceDiscounted.setText("\u20B9 " + mBefore.get(i).getFemaleprice());
                }
            }

            if (mBefore.get(i).getMaleprice().equals("0")) {
                tvMalePrice.setText("Free");
                tvMalePriceDiscounted.setText("");
            } else {
                if (mBefore.get(i).getM_discount().equals("0")) {
                    tvMalePriceDiscounted.setText("");
                    tvMalePrice.setText("\u20B9 " + mBefore.get(i).getMaleprice());
                } else {
                    tvMalePrice.setText("\u20B9 " + mBefore.get(i).getMale_dis());
                    tvMalePriceDiscounted.setText("\u20B9 " + mBefore.get(i).getMaleprice());
                }
            }

            tvCouplePriceDescription.setText(mBefore.get(i).getCoupledesc());
            tvFemalePriceDescription.setText(mBefore.get(i).getFemaledesc());
            tvMalePriceDescription.setText(mBefore.get(i).getMaledesc());

        } else if (type == 1) {

            ActivityEventsDetails.time = mTicket.get(i).getAfter();
            CouplePrice = mAfter.get(i).getCoupleprice();
            CoupleDiscounted = mAfter.get(i).getC_discount();
            CouplePriceDiscounted = mAfter.get(i).getCouple_dis();
            CouplePriceDescription = mAfter.get(i).getCoupledesc();

            FemalePrice = mAfter.get(i).getFemaleprice();
            FemaleDiscounted = mAfter.get(i).getF_discount();
            FemalePriceDiscounted = mAfter.get(i).getFemale_dis();
            FemalePriceDescription = mAfter.get(i).getFemaledesc();

            MalePrice = mAfter.get(i).getMaleprice();
            MaleDiscounted = mAfter.get(i).getM_discount();
            MalePriceDiscounted = mAfter.get(i).getMale_dis();
            MalePriceDescription = mAfter.get(i).getMaledesc();

            if (mAfter.get(i).getCoupleprice().equals("0")) {
                tvCouplePrice.setText("Free");
                tvCouplePriceDiscounted.setText("");
            } else {
                if (mAfter.get(i).getC_discount().equals("0")) {
                    tvCouplePriceDiscounted.setText("");
                    tvCouplePrice.setText("\u20B9 " + mAfter.get(i).getCoupleprice());
                } else {
                    tvCouplePrice.setText("\u20B9 " + mAfter.get(i).getCouple_dis());
                    tvCouplePriceDiscounted.setText("\u20B9 " + mAfter.get(i).getCoupleprice());
                }
            }

            if (mAfter.get(i).getFemaleprice().equals("0")) {
                tvFemalePrice.setText("Free");
                tvFemalePriceDiscounted.setText("");
            } else {
                if (mAfter.get(i).getF_discount().equals("0")) {
                    tvFemalePriceDiscounted.setText("");
                    tvFemalePrice.setText("\u20B9 " + mAfter.get(i).getFemaleprice());
                } else {
                    tvFemalePrice.setText("\u20B9 " + mAfter.get(i).getFemale_dis());
                    tvFemalePriceDiscounted.setText("\u20B9 " + mAfter.get(i).getFemaleprice());
                }
            }

            if (mAfter.get(i).getMaleprice().equals("0")) {
                tvMalePrice.setText("Free");
                tvMalePriceDiscounted.setText("");
            } else {
                if (mAfter.get(i).getM_discount().equals("0")) {
                    tvMalePriceDiscounted.setText("");
                    tvMalePrice.setText("\u20B9 " + mAfter.get(i).getMaleprice());
                } else {
                    tvMalePrice.setText("\u20B9 " + mAfter.get(i).getMale_dis());
                    tvMalePriceDiscounted.setText("\u20B9 " + mAfter.get(i).getMaleprice());
                }
            }

            tvMalePriceDescription.setText(mAfter.get(i).getMaledesc());
            tvFemalePriceDescription.setText(mAfter.get(i).getFemaledesc());
            tvCouplePriceDescription.setText(mAfter.get(i).getCoupledesc());
        }

    }

}
