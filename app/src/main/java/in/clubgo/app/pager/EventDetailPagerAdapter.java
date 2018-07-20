package in.clubgo.app.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.clubgo.app.fragment.FragmentBusyNight;
import in.clubgo.app.fragment.FragmentCost;
import in.clubgo.app.fragment.FragmentCuisines;
import in.clubgo.app.fragment.FragmentHighlights;
import in.clubgo.app.fragment.FragmentKnownFor;
import in.clubgo.app.fragment.FragmentOpeningHours;

/**
 * Created by Jitendra Soam on 20/4/16.
 */
public class EventDetailPagerAdapter extends FragmentStatePagerAdapter {

    public EventDetailPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new FragmentOpeningHours();
                break;
            case 1:
                frag = new FragmentKnownFor();
                break;
            case 2:
                frag = new FragmentCuisines();
                break;
            case 3:
                frag = new FragmentHighlights();
                break;
            case 4:
                frag = new FragmentCost();
                break;
            case 5:
                frag = new FragmentBusyNight();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = " ";
        switch (position) {
            case 0:
                title = "Opening Hours";
                break;
            case 1:
                title = "Known For";
                break;
            case 2:
                title = "Cuisines";
                break;
            case 3:
                title = "Facilities";
                break;
            case 4:
                title = "Cost";
                break;
            case 5:
                title = "Busy Nights";
                break;

        }

        return title;
    }
}