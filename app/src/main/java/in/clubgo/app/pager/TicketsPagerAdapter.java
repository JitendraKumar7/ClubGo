package in.clubgo.app.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.clubgo.app.Tickets.FragmentLiveTickets;
import in.clubgo.app.Tickets.FragmentPastTickets;

/**
 * Created by Jitendra Soam on 13/4/16.
 */
public class TicketsPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TicketsPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentLiveTickets tab1 = new FragmentLiveTickets();
                return tab1;
            case 1:
                FragmentPastTickets tab2 = new FragmentPastTickets();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
