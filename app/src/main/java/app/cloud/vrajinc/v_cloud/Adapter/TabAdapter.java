package app.cloud.vrajinc.v_cloud.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragment_titleList = new ArrayList<>();
    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    public void addFragmenttoList(Fragment fragment, String title)
    {
        fragmentList.add(fragment);
        fragment_titleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragment_titleList.get(position);
        //return null;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
