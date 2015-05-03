package vershitsky.kirill.myapp;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;

import com.google.samples.apps.iosched.ui.widget.SlidingTabLayout;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.ToggleDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {
    private AppUser user;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager pager;
    private SlidingTabLayout tabs;
    private Toolbar toolbar;
    private ArrayList<String> searchSex = new ArrayList<String>() ;
    public static final int SWITCH_ITEM_FEMALE = 3;
    public static final int SWITCH_ITEM_MALE = 2;
    public static final int SEARCH_ITEM = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        searchSex = new ArrayList<String>();
        user = (AppUser) intent.getParcelableExtra(Constants.APP_USER_KEY);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (user.getSex().equals(Constants.SEX_MALE)){
            searchSex.add(Constants.SEX_FEMALE);
        }
        else searchSex.add(Constants.SEX_MALE);
        addNavigationDrawer();

        Log.d("ARRAY", searchSex.toString());
        drawTabs();
    }

    public void drawTabs(){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), user, searchSex );
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
    }

    public void addNavigationDrawer(){
        Drawable userPhoto = new BitmapDrawable(getResources(), user.getUserPhoto());

        OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {
                if(iDrawerItem.getIdentifier() == SWITCH_ITEM_MALE){
                    if(b) searchSex.add(Constants.SEX_MALE);
                    else searchSex.remove(Constants.SEX_MALE);
                }
                if(iDrawerItem.getIdentifier() == SWITCH_ITEM_FEMALE){
                    if(b) searchSex.add(Constants.SEX_FEMALE);
                    else searchSex.remove(Constants.SEX_FEMALE);
                }
                Log.d("SearchSex", searchSex.toString());
            }
        };
       SwitchDrawerItem switchMale = new SwitchDrawerItem().withOnCheckedChangeListener(onCheckedChangeListener).withName(R.string.drawer_item_male).withIcon(FontAwesome.Icon.faw_male).withIdentifier(SWITCH_ITEM_MALE);
        SwitchDrawerItem switchFemale = new SwitchDrawerItem().withOnCheckedChangeListener(onCheckedChangeListener).withName(R.string.drawer_item_female).withIcon(FontAwesome.Icon.faw_female).withIdentifier(SWITCH_ITEM_FEMALE);
        if(searchSex.contains(Constants.SEX_FEMALE)){
            switchFemale.setChecked(true);
        }
        if(searchSex.contains(Constants.SEX_MALE)){
            switchMale.setChecked(true);
        }
            AccountHeader.Result headerResult = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_image)
                .addProfiles(
                        new ProfileDrawerItem().withName(user.getFirstName() + " " + user.getLastName()).withIcon(userPhoto)
                )
                .withAlternativeProfileHeaderSwitching(false)
                .build();

        new Drawer().withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_update_loc).withIcon(FontAwesome.Icon.faw_repeat).withIdentifier(1),
                        new SectionDrawerItem().withName(R.string.drawer_item_seach_set),
                        switchMale,
                        switchFemale,
                        new PrimaryDrawerItem().withName(R.string.drawer_item_search).withIcon(FontAwesome.Icon.faw_search).withIdentifier(SEARCH_ITEM),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_info).withIdentifier(5),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_feedback).withIcon(FontAwesome.Icon.faw_pencil).withIdentifier(6)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        if(iDrawerItem.getIdentifier() == SEARCH_ITEM){
                            drawTabs();
                        }
                        Log.d("onItemClick", "i=" + i + " l=" + l + " drawer item=" + iDrawerItem.getIdentifier());
                    }
                }).build();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
