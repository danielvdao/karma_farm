package com.cs371m.ads.karma_farm;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
 */
public class KFMain extends Activity
        implements KFNavigationDrawerFragment.NavigationDrawerCallbacks, KFSubmissionsListFragment.OnSubmissionSelectedListener {

    private static final String TAG = "KFMain";
    public static final String COMMENTS_FRAGMENT = "KFCommentsListFragment";
    public static final String SUBMISSIONS_FRAGMENT = "KFSubmissionsListFragment";
    public static final String CONTENT_FRAGMENT = "KFContentFragment"; // TODO

    public KFSubmissionsListFragment mKFSubmissionsListFragment;
    public KFCommentsListFragment mKFCommentsFragment;

    private KFNavigationDrawerFragment mNavigationDrawerFragment;
    private boolean firstTime; // TODO FIX THIS

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mSubredditName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        mKFSubmissionsListFragment = new KFSubmissionsListFragment();
        mKFCommentsFragment = new KFCommentsListFragment();

        mNavigationDrawerFragment = (KFNavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mSubredditName = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Fragment lastFragment = getFragmentManager().findFragmentById(R.id.container);

        if (lastFragment != null) {

            Log.d(TAG, "reattaching " + lastFragment.toString());

            // don't add old submissions to backstack
            if(lastFragment instanceof KFCommentsListFragment) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, lastFragment)
                        .commit();
            }
            else if(lastFragment instanceof KFSubmissionsListFragment) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, lastFragment)
                        .commit();
            }
        } else {
            firstTime = true;
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, KFSubmissionsListFragment.newInstance("frontpage")
                            , SUBMISSIONS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Log.d(TAG, "drawer item selected");

        switch (position) {
            case 0:
                mSubredditName = getString(R.string.title_section0);
                break;
            case 1:
                mSubredditName = getString(R.string.title_section1);
                break;
            case 2:
                mSubredditName = getString(R.string.title_section2);
                break;
            case 3:
                mSubredditName = getString(R.string.title_section3);
                break;
            case 4:
                mSubredditName = getString(R.string.title_section4);

            case 5:
                mSubredditName = getString(R.string.title_section5);
                break;

        }


        setTitle(mSubredditName);

        // update the main content by replacing fragments
        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFSubmissionsListFragment.newInstance((String) mSubredditName))
                .commit();
    }

    @Override
    public void onSubmissionSelected(String id) {

        Log.d(TAG, "submission selected");
        // attached comments view
        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFCommentsListFragment.newInstance(id), COMMENTS_FRAGMENT)
                .addToBackStack(null)   
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        if(!firstTime)
            actionBar.setTitle(mSubredditName);
        else {
            actionBar.setTitle("frontpage");
            firstTime = false;
        }
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
