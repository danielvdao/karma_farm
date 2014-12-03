package com.cs371m.ads.karma_farm;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Intent;



/**
 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
 */
public class KFMain extends Activity
        implements KFNavigationDrawerFragment.NavigationDrawerCallbacks, KFSubmissionsListFragment.SubmissionListListener {

    private static final String TAG = "KFMain";
    public static final String COMMENTS_FRAGMENT = "KFCommentsListFragment";
    public static final String SUBMISSIONS_FRAGMENT = "KFSubmissionsListFragment";
    public static final String CONTENT_FRAGMENT = "KFContentFragment"; // TODO

    public static final int LOGIN_DIALOG = 0;
    public static final int COMMENT_DIALOG = 1;

    public KFSubmissionsListFragment mKFSubmissionsListFragment;
    public KFCommentsListFragment mKFCommentsFragment;
    public KFContentFragment mKFContentFragment;

    private KFNavigationDrawerFragment mNavigationDrawerFragment;
    private boolean firstTime; // TODO FIX THIS
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mSubredditName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_nav_bar);

        mSharedPreferences = getPreferences(MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        if (mSharedPreferences.getString("username", null) != null)
            Log.d(TAG, "have user: " + mSharedPreferences.getString("username", null));

        if (mSharedPreferences.getString("password", null) != null)
            Log.d(TAG, "with password: " + mSharedPreferences.getString("password", null));

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
                break;
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
    public void onSubmissionSelected(String url) {

        Log.d(TAG, "submission selected");

        // attach content view
        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFContentFragment.newInstance(url), CONTENT_FRAGMENT)
                .addToBackStack(null)
                .commit();


    }

    public void onSubmissionCommentsSelected(String id) {
        // attach comments view
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
            actionBar.setTitle("All");
            firstTime = false;
        }
    }

    @Override
    public void onBackPressed() {
        //handle each potentially attached fragments back routine respectively here

        // hide progress bar if we were looing at post
        if (getFragmentManager().findFragmentByTag(CONTENT_FRAGMENT) != null) {
            KFContentFragment fragment = (KFContentFragment) getFragmentManager().findFragmentById(R.id.container);
            if (fragment != null && fragment.getTag().equals(CONTENT_FRAGMENT))
                fragment.hideProgressBar();
        }
        super.onBackPressed();
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
            showDialog(LOGIN_DIALOG);
//            Intent intent = new Intent(getApplicationContext(), KFLoginTask.class);
//            startActivity(intent);
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
            case COMMENT_DIALOG:
                dialog = this.commentDialog(builder);
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
                                    Toast.makeText(getApplicationContext(), "Please enter valid credentials.", Toast.LENGTH_LONG).show();
                                }

                                else {
                                      Toast.makeText(getApplicationContext(), "Validating credentials", Toast.LENGTH_LONG).show();
//                                    tryLogin(username.getText().toString(), password.getText().toString());
//                                    intent.putExtra("username", username.getText().toString());
//                                    intent.putExtra("password", password.getText().toString());
//                                    startActivity(intent);
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    private Dialog commentDialog(AlertDialog.Builder builder) {

        LayoutInflater inflater = LayoutInflater.from(this);

        final View commentView = inflater.inflate(R.layout.comment_dialog, null);
        builder.setMessage(R.string.comment_message)
                .setView(commentView)
                .setCancelable(false)
                .setPositiveButton(R.string.post,
                        new DialogInterface.OnClickListener() {
                            // get login info to pass to login task
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), KFCommentTask.class);
                                EditText comment = (EditText) commentView.findViewById(R.id.comment_text);
                            }
                        })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

}
