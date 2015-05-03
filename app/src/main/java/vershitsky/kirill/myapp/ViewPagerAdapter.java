package vershitsky.kirill.myapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

import vershitsky.kirill.myapp.SaveData.DBConnection;

/**
 * Created by Вершицкий on 23.04.2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private AppUser user;
    ArrayList<String> locations = new ArrayList<String>();
    ArrayList<String> searchTypes = new ArrayList<String>();
    private ArrayList<String> searchSex;

    public ViewPagerAdapter(FragmentManager fm, AppUser user, ArrayList<String> searchSex) {
        super(fm);
        this.user = user;
        this.searchSex = searchSex;
        if(!user.getLocality().equals(Constants.UNKNOWN)){
            locations.add(user.getLocality());
            searchTypes.add(Constants.LOCALITY);
        }

        if(!user.getAdminArea().equals(Constants.UNKNOWN)){
            locations.add(user.getAdminArea());
            searchTypes.add(Constants.REGION);
        }
        if(!user.getCountryName().equals(Constants.UNKNOWN)){
            locations.add(user.getCountryName());
            searchTypes.add(Constants.COUNTRY);
        }

        Log.d("LOCATIONS", locations.toString());
   }

    @Override
    public Fragment getItem(int position) {
//        String searchViewUrl= "";
//        JSONObject jsKeysToSearch;


        TabFragment tabFragment = TabFragment.newInstance(user, searchTypes.get(position), searchSex);
        return tabFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return locations.get(position);
    }

    @Override
    public int getCount() {
        return locations.size();
    }
}
