package vershitsky.kirill.myapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;

import vershitsky.kirill.myapp.SaveData.DBConnection;

/**
 * Created by Вершицкий on 23.04.2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private AppUser user;
    ArrayList<String> locations = new ArrayList<String>();
    ArrayList<String> searchTypes = new ArrayList<String>();

    public ViewPagerAdapter(FragmentManager fm, AppUser user) {
        super(fm);
        this.user = user;

        if(!user.getCountryName().equals(Constants.UNKNOWN)){
            locations.add(user.getCountryName());
            searchTypes.add(Constants.COUNTRY);
        }
        if(!user.getAdminArea().equals(Constants.UNKNOWN)){
            locations.add(user.getAdminArea());
            searchTypes.add(Constants.REGION);
        }
        if(!user.getLocality().equals(Constants.UNKNOWN)){
            locations.add(user.getLocality());
            searchTypes.add(Constants.LOCALITY);
        }
        Log.d("LOCATIONS", locations.toString());
   }

    @Override
    public Fragment getItem(int position) {
        String searchViewUrl= "";
        Log.d("POSITION_VIEW_PAGER", position + "");
        switch (searchTypes.get(position)){
            case Constants.COUNTRY: searchViewUrl = Constants.viewUrlRequest(DBConnection.Views.BY_COUNTRY, user.getCountryName(), "1"); break;
            case Constants.REGION: searchViewUrl = Constants.viewUrlRequest(DBConnection.Views.BY_COUNTRY_ADMIN, user.getCountryName(), user.getAdminArea(),"1"); break;
            case Constants.LOCALITY:
                if (user.getAdminArea().equals(Constants.UNKNOWN)){
                    searchViewUrl = Constants.viewUrlRequest(DBConnection.Views.BY_COUNTRY_LOCALITY, user.getCountryName(), user.getLocality(), "1"); break;
                }
                else{
                    searchViewUrl = Constants.viewUrlRequest(DBConnection.Views.BY_FULL_LOCATION, user.getCountryName(), user.getAdminArea(), user.getLocality(), "1"); break;
                }
        }

        TabFragment tabFragment = TabFragment.newInstance(user, searchViewUrl);
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
