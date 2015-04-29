package vershitsky.kirill.myapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.vk.sdk.api.VKRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import vershitsky.kirill.myapp.AppUser;
import vershitsky.kirill.myapp.Constants;
import vershitsky.kirill.myapp.NavDrawAdapter;
import vershitsky.kirill.myapp.R;
import vershitsky.kirill.myapp.ViewPagerAdapter;


public class MainActivity extends ActionBarActivity {
    private final static String TAG = "MainActivity";
    private static final String APP_ID = "4802878";

    private AppUser user;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private Toolbar toolbar;

    private RecyclerView navDrawRecView;
    private RecyclerView.Adapter navDrawAdapter;
    private RecyclerView.LayoutManager recViewLayoutManager;
    private String[] items = {"Settings", "Share"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        user = (AppUser) intent.getParcelableExtra(Constants.APP_USER_KEY);
        navDrawRecView = (RecyclerView) findViewById(R.id.rec_view_nav_draw);
        navDrawRecView.setHasFixedSize(true);
        navDrawAdapter = new NavDrawAdapter(user, items);
        navDrawRecView.setAdapter(navDrawAdapter);
        recViewLayoutManager = new LinearLayoutManager(this);
        navDrawRecView.setLayoutManager(recViewLayoutManager);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),user);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(viewPagerAdapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setViewPager(pager);

        super.onCreate(savedInstanceState);
    }
}
//ConnectivityManager connMgr = (ConnectivityManager)
//        getSystemService(Context.CONNECTIVITY_SERVICE);
//NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//if (networkInfo != null && networkInfo.isConnected()) {
//        // fetch data
//        } else {
//        // display error
//        }