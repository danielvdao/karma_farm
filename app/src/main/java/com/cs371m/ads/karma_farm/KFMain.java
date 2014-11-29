package com.cs371m.ads.karma_farm;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;

import android.content.Intent;


/**
 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
 */
public class KFMain extends Activity
        implements KFNavigationDrawerFragment.NavigationDrawerCallbacks, KFSubmissionsListFragment.OnSubmissionSelectedListener {

    private static final String TAG = "KFMain";
    public static final String COMMENTS_FRAGMENT = "KFCommentsListFragment";
    public static final String SUBMISSIONS_FRAGMENT = "KFSubmissionsListFragment";
    public static final String CONTENT_FRAGMENT = "KFContentFragment"; // TODO

    private static final int LOGIN_DIALOG = 0;

    public KFSubmissionsListFragment mKFSubmissionsListFragment;
    public KFCommentsListFragment mKFCommentsFragment;
    public KFContentFragment mKFContentFragment;

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
        mKFContentFragment = new KFContentFragment();

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
                    .replace(R.id.container, KFSubmissionsListFragment.newInstance("All")
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

        // attach content view
        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFContentFragment.newInstance(id), CONTENT_FRAGMENT)
                .addToBackStack(null)
                .commit();

        // attached comments view
//        getFragmentManager().beginTransaction()
//                .replace(R.id.container, KFCommentsListFragment.newInstance(id), COMMENTS_FRAGMENT)
//                .addToBackStack(null)
//                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        if(!firstTime)
            actionBar.setTitle(mSubredditName);
        else {
            actionBar.setTitle("All");
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

        if (id == R.id.action_login) {
//            showDialog(LOGIN_DIALOG);
            Intent intent = new Intent(getApplicationContext(), KFLoginTask.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case LOGIN_DIALOG:
                dialog = this.loginDialog(builder);
                break;
        }

        return dialog;
    }

    private Dialog loginDialog(AlertDialog.Builder builder) {

        LayoutInflater inflater = LayoutInflater.from(this);

        final View loginView = inflater.inflate(R.layout.login_dialog, null);
        builder.setMessage(R.string.login_message)
                .setView(loginView)
                .setCancelable(false)
                .setPositiveButton(R.string.login,
                        new DialogInterface.OnClickListener() {
                            // get login info to pass to login task
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), KFLoginTask.class);
                                EditText username = (EditText) loginView.findViewById(R.id.username);
                                EditText password = (EditText) loginView.findViewById(R.id.password);

                                //TODO Make an actual try/catch statement for when the user doesn't enter in a good password
                                if (username.getText() == null || password.getText() == null) {
                                    Log.d(TAG, "User hasn't entered anything");
                                    Toast.makeText(getApplicationContext(), "Either the user/pw wasn't entered, please try again.", Toast.LENGTH_LONG).show();
                                }

                                else {
                                    intent.putExtra("username", username.getText().toString());
                                    intent.putExtra("password", password.getText().toString());
                                    startActivity(intent);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

}
