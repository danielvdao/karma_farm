package com.cs371m.ads.karma_farm;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Intent;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
 */
public class KFMain extends Activity
        implements KFNavigationDrawerFragment.NavigationDrawerCallbacks, KFSubmissionsListFragment.SubmissionListListener {

    private static final String TAG = "KFMain";
    public static final String COMMENTS_FRAGMENT = "KFCommentsListFragment";
    public static final String SUBMISSIONS_FRAGMENT = "KFSubmissionsListFragment";
    public static final String CONTENT_FRAGMENT = "KFContentFragment"; // TODO

    public static final String[] DEFAULT_SUBS =
            {"announcement", "Art", "AskReddit", "askscience", "aww", "blog",
            "books", "creepy", "dataisbeautiful", "DIY", "Documentaries",
            "EarthPorn", "explainlikeimfive", "Fitness", "food", "funny",
            "Futurology", "gadgets", "gaming", "GetMotivated", "gifs",
            "history", "IAmA", "InternetIsBeautiful", "Jokes", "LifeProTips",
            "listentothis", "mildlyinteresting", "movies", "Music", "news",
            "nosleep", "nottheonion", "oldschoolcool", "personalfinance",
            "philosophy", "photoshopbattles", "pics", "science",
            "Showerthoughts", "space", "sports", "television", "tifu",
            "todayilearned", "TwoXChromosomes", "UpliftingNews", "videos",
            "worldnews", "writingprompts"};

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

        mSubredditName = DEFAULT_SUBS[position];
        Log.d(TAG, "Drawer item selected " + mSubredditName);

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

            MenuItem loginItem = menu.findItem(R.id.action_login);
            MenuItem logoutItem = menu.findItem(R.id.action_logout);
            Log.d(TAG, "logged_in: " + mSharedPreferences.getInt("logged_in", 0));
            if (mSharedPreferences.getInt("logged_in", 0) == 1){
                loginItem.setVisible(false);
                logoutItem.setVisible(true);
            }

            else{
                loginItem.setVisible(true);
                logoutItem.setVisible(false);
            }

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


        if (id == R.id.action_login) {
            showDialog(LOGIN_DIALOG);
        }

        if (id == R.id.action_logout) {
            mEditor.putString("username", null);
            mEditor.putString("password", null);
            mEditor.putInt("logged_in", 0);
            mEditor.commit();
            Toast.makeText(getApplicationContext(), "You have logged out.", Toast.LENGTH_LONG).show();
            invalidateOptionsMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        switch (id) {
            case LOGIN_DIALOG:
                dialog = this.loginDialog(builder);
                break;
            case COMMENT_DIALOG:
                String comment_id = args.getString("id");
                dialog = this.commentDialog(builder, comment_id);
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
                                EditText username = (EditText) loginView.findViewById(R.id.username);
                                EditText password = (EditText) loginView.findViewById(R.id.password);

                                //TODO Make an actual try/catch statement for when the user doesn't enter in a good password
                                if (username.getText() == null || password.getText() == null) {
                                    Log.d(TAG, "User hasn't entered anything");
                                    Toast.makeText(getApplicationContext(), "Please enter valid credentials.", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d(TAG, "in the login and hopefully im seeing this");
                                    new LoginTask().execute(username.getText().toString(), password.getText().toString());
                                    Log.d(TAG, "logged_in in dialog: " + mSharedPreferences.getInt("logged_in", 0));

                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    private Dialog commentDialog(AlertDialog.Builder builder, String id) {

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
                                if (mSharedPreferences.getInt("logged_in", 0) == 1){
                                    String username = mSharedPreferences.getString("username", null);
                                    String password = mSharedPreferences.getString("password", null);
                                    String text = comment.toString();
                                    //need comment id
                                    //String comment_id = comment id;
                                    //new CommentTask().execute(username, password, text, comment_id);
                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "Please login!", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton(R.string.cancel, null);

        return builder.create();
    }

    private class CommentTask extends AsyncTask<String, String, Double>{
        private JSONObject result;

        @Override
        protected Double doInBackground(String... params){
            postData(params[0], params[1], params[2], params[3]);
            return null;
        }


        protected void onPostExecute(Double result){
            Log.d(TAG, "finished POST request");

            try {
                if (this.result.getString("success").equals("True")) {
                    Toast.makeText(getApplicationContext(), "Comment succeeded", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(getApplicationContext(), "Comment failed, please try again", Toast.LENGTH_LONG).show();
                }
            }

            catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Sorry an error on our end has happened!", Toast.LENGTH_LONG).show();
            }


        }

        public void postData(String username, String password, String comment, String comment_id){
            DefaultHttpClient comment_client = new DefaultHttpClient();
            JSONObject comment_json = new JSONObject();
            Log.d(TAG, "in postData username - " + username + " password - " + password);
            try {
                comment_json.put("username", username);
                comment_json.put("password", password);
                comment_json.put("text", comment);
                comment_json.put("comment_id", comment_id);
                HttpPost post_request = new HttpPost("http://104.131.71.174/api/v0/comment");
                StringEntity params = new StringEntity(comment_json.toString());
                post_request.addHeader("content-type", "application/json");
                post_request.setEntity(params);

                HttpResponse response = comment_client.execute(post_request);
                HttpEntity entity = response.getEntity();
                String entity_string = EntityUtils.toString(entity);

                result = new JSONObject(entity_string);

                Log.d(TAG, "success: " + result.getString("success"));

            } catch (Exception ex) {
                Log.d(TAG, "Exception: " + ex.toString());
            } finally {
                comment_client.getConnectionManager().shutdown();
                Log.d(TAG, "Finished");
            }
        }
    }


    private class LoginTask extends AsyncTask<String, String, Double>{
        private JSONObject result;

        @Override
        protected Double doInBackground(String... params){
            postData(params[0], params[1]);
            return null;
        }


        protected void onPostExecute(Double result){
            Log.d(TAG, "finished POST request");

            try {
                if (this.result.getString("success").equals("True")) {
                    Toast.makeText(getApplicationContext(), "Login succeeded", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(getApplicationContext(), "Login failed, please try again", Toast.LENGTH_LONG).show();
                }
            }

            catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Sorry an error on our end has happened!", Toast.LENGTH_LONG).show();
            }

            invalidateOptionsMenu();


        }

        public void postData(String username, String password){
            DefaultHttpClient login_client = new DefaultHttpClient();
            JSONObject login_json = new JSONObject();
            Log.d(TAG, "in postData username - " + username + " password - " + password);
            try {
                login_json.put("username", username);
                login_json.put("password", password);
                HttpPost post_request = new HttpPost("http://104.131.71.174/api/v0/login");
                StringEntity params = new StringEntity(login_json.toString());
                post_request.addHeader("content-type", "application/json");
                post_request.setEntity(params);

                HttpResponse response = login_client.execute(post_request);
                HttpEntity entity = response.getEntity();
                String entity_string = EntityUtils.toString(entity);

                result = new JSONObject(entity_string);

                if (result.getString("success").equals("True")){
                    mEditor.putString("username", username);
                    mEditor.putString("password", password);
                    mEditor.putInt("logged_in", 1);
                    mEditor.commit();
                }

                else{
                    mEditor.putString("username", null);
                    mEditor.putString("password", null);
                    mEditor.putInt("logged_in", 0);
                    mEditor.commit();
                }

                Log.d(TAG, "success: " + result.getString("success"));

            } catch (Exception ex) {
                Log.d(TAG, "Exception: " + ex.toString());
            } finally {
                login_client.getConnectionManager().shutdown();
                Log.d(TAG, "Finished");
            }
        }
    }
}
