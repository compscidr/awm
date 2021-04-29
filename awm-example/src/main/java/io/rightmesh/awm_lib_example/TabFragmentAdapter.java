package io.rightmesh.awm_lib_example;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private String tabTitles[] = new String[] { "Scan Results", "Map View", "Settings", "Privacy Policy" };

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        if (i == 0) {
            return new ScanFragment();
        } else if (i == 1) {
            return new MapFragment();
        } else if (i == 2) {
            return new Preferences();
        } else {
            return new PrivacyFragment();
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
