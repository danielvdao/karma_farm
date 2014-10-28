package com.cs371m.ads.karma_farm;

import android.app.Activity;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;


public class KFMain extends Activity
        implements KFNavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private KFNavigationDrawerFragment mNavigationDrawerFragment;

    private KFSubmissionsListFragment mKFSubmissionsListFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mSubredditName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        mNavigationDrawerFragment = (KFNavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mSubredditName = getTitle();

        Log.d("TAG", "SubredditName set to "  + mSubredditName);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mKFSubmissionsListFragment = new KFSubmissionsListFragment();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();

        switch (position) {
            case 0:
                mSubredditName = getString(R.string.title_section1);
                break;
            case 1:
                mSubredditName = getString(R.string.title_section2);
                break;
            case 2:
                mSubredditName = getString(R.string.title_section3);
                break;
        }

        setTitle(mSubredditName);

        Log.d("TAG", "Position selection is: " + position);
        Log.d("TAG", "mSubredditName is: " + mSubredditName);

        fragmentManager.beginTransaction()
                .replace(R.id.container, KFSubmissionsListFragment.newInstance((String) mSubredditName))
                .commit();
    }

//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mSubredditName = getString(R.string.title_section1);
//                break;
//            case 2:
//                mSubredditName = getString(R.string.title_section2);
//                break;
//            case 3:
//                mSubredditName = getString(R.string.title_section3);
//                break;
//        }
//    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mSubredditName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.nav_bar, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
