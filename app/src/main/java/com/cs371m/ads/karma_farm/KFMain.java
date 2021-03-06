package com.cs371m.ads.karma_farm;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * KarmaFarm Main Activity
 */
public class KFMain extends Activity
        implements KFNavigationDrawerFragment.NavigationDrawerCallbacks,
        KFSubmissionsListFragment.SubmissionListListener,
        KFCommentsListFragment.CommentListListener {

    private static final String TAG = "KFMain";
    private static final long EXIT_BACK_WINDOW_MILLIS = 1500l;

    public static final String COMMENTS_FRAGMENT = "KFCommentsListFragment";
    public static final String SUBMISSIONS_FRAGMENT = "KFSubmissionsListFragment";
    public static final String CONTENT_FRAGMENT = "KFContentFragment"; // TODO
    public static final int LOGIN_DIALOG = 0;
    public static final int COMMENT_DIALOG = 1;
    public static final int EXIT_DIALOG = 3;



    // TODO Add constants for our endpoints
    public static final String[] DEFAULT_SUBS =
    {"all", "art", "AskReddit", "askscience", "aww", "blog",
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

    private KFNavigationDrawerFragment mNavigationDrawerFragment;

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Handler mHandler;
    private CharSequence mTitle;
    private CharSequence mSubredditName;
    private boolean mBackPressedRecently;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_nav_bar);

        mSharedPreferences = getPreferences(MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        if (savedInstanceState != null) {
            mTitle = savedInstanceState.getString("mTitle");
            mSubredditName = savedInstanceState.getString("mSubredditName");
        }
        else {
            mTitle = "all";
            mSubredditName = "all";
        }
        setTitle(mTitle);

        mHandler = new Handler();

        mNavigationDrawerFragment = (KFNavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        mBackPressedRecently = false;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Fragment lastFragment = getFragmentManager().findFragmentById(R.id.container);

        if (lastFragment != null) {
            Log.d(TAG, "last fragment is \n\n\n" + lastFragment.toString());
            // don't add old submissions to backstack
            if(lastFragment instanceof KFCommentsListFragment) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, lastFragment, COMMENTS_FRAGMENT)
                        .commit();
            }
            else if(lastFragment instanceof KFSubmissionsListFragment) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, lastFragment, SUBMISSIONS_FRAGMENT)
                        .commit();
            }
        } else {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, KFSubmissionsListFragment.newInstance("all")
                            , SUBMISSIONS_FRAGMENT)
                    .commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        mSubredditName = DEFAULT_SUBS[position];
        mTitle = mSubredditName.toString();

        Log.d(TAG, mSubredditName + " selected from drawer");

        setTitle(mSubredditName);

        // update the main content by replacing fragments
        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFSubmissionsListFragment.newInstance((String) mSubredditName))
                .commit();
    }

    @Override
    public void onSubmissionSelected(String url, String title) {

        mTitle = url;
        getActionBar().setTitle(url);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFContentFragment.newInstance(url, title), CONTENT_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void onSubmissionCommentsSelected(String id, String title) {
        // attach comments view
        mTitle = title;
        getActionBar().setTitle(mTitle);
        getFragmentManager().beginTransaction()
                .replace(R.id.container, KFCommentsListFragment.newInstance(id), COMMENTS_FRAGMENT)
                .addToBackStack(null)
                .commit();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        Fragment lastFragment = fm.findFragmentById(R.id.container);
        if (lastFragment instanceof KFContentFragment) {
            KFContentFragment fragment = (KFContentFragment) lastFragment;
            WebView webView = ((KFContentFragment) lastFragment).getWebView();
            if (webView != null ) {
                if( webView.canGoBack()) {
                    fragment.showProgressBar();
                    webView.goBack();
                    fragment.hideProgressBar();
                    return;
                }
                fragment.hideProgressBar();
            }

        } else if (fm.getBackStackEntryCount() == 0) {
//            showDialog(EXIT_DIALOG);
            if(mBackPressedRecently) {
                super.onBackPressed();
            } else {
                Toast.makeText(getApplicationContext(), R.string.exit_toast, Toast.LENGTH_SHORT).show();
                mBackPressedRecently = true;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mBackPressedRecently = false;
                    }
                }, EXIT_BACK_WINDOW_MILLIS);
                return;
            }

        }

        getActionBar().setTitle(mSubredditName);

        //handle each potentially attached fragments back routine respectively here
        // hide progress bar if we were looing at post
//        if (fm.findFragmentByTag(CONTENT_FRAGMENT) != null) {
//            KFContentFragment fragment = (KFContentFragment) fm.findFragmentById(R.id.container);
//            if (fragment != null && fragment.getTag().equals(CONTENT_FRAGMENT))
//                fragment.hideProgressBar();
//        }
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("mTitle", mTitle.toString());
        outState.putString("mSubredditName", mSubredditName.toString());

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

    public void vote(String id, String isSubmission, String action) {
        Log.d(TAG, "action : " + action);

        if(mSharedPreferences.getInt("logged_in", 0) == 1) {
            new VotingTask().execute(mSharedPreferences.getString("username", null),
                    mSharedPreferences.getString("password", null), id, isSubmission, action);
        }

        else
            Toast.makeText(getApplicationContext(), "Please login to vote.", Toast.LENGTH_LONG).show();
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
            case EXIT_DIALOG:
                dialog = this.exitDialog(builder);
                break;
        }

        return dialog;
    }

    // going to set press back twice to exit
    private Dialog exitDialog(AlertDialog.Builder builder) {
        builder.setMessage(R.string.exit_message)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            // get login info to pass to login task
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                .setNegativeButton(R.string.no, null);

        return builder.create();
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
        final String comment_id = id;
        final View commentView = inflater.inflate(R.layout.comment_dialog, null);

        builder.setMessage(R.string.comment_message)
                .setView(commentView)
                .setCancelable(false)
                .setPositiveButton(R.string.post,
                        new DialogInterface.OnClickListener() {
                            // get login info to pass to login task
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent(getApplicationContext(), KFCommentTask.class);
                                EditText comment = (EditText) commentView.findViewById(R.id.comment_text);
                                if (mSharedPreferences.getInt("logged_in", 0) == 1){
                                    String username = mSharedPreferences.getString("username", null);
                                    String password = mSharedPreferences.getString("password", null);
                                    String text = comment.getText().toString();
//                                    need comment id
                                    new CommentTask().execute(username, password, text, comment_id);
                                    KFCommentsListFragment listFragment = (KFCommentsListFragment) getFragmentManager().findFragmentByTag(COMMENTS_FRAGMENT);
                                    // show text in comment list
                                    listFragment.showComment(username, text);
                                }

                                else {
                                    Toast.makeText(getApplicationContext(), "Please login to reply.", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), "Comment succeeded.", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(getApplicationContext(), "Comment failed, please try again.", Toast.LENGTH_LONG).show();
                }
            }

            catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Sorry an error on our end has happened.", Toast.LENGTH_LONG).show();
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
        private String username;
        @Override
        protected Double doInBackground(String... params){
            username = params[0].toString();
            postData(params[0], params[1]);
            return null;
        }


        protected void onPostExecute(Double result){
            Log.d(TAG, "finished POST request");

            try {
                if (this.result.getString("success").equals("True")) {
                    Toast.makeText(getApplicationContext(), "You have logged in as " + username + ".", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(getApplicationContext(), "Login failed, please try again " + username + ".", Toast.LENGTH_LONG).show();
                }
            }

            catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Sorry " + username + " an error on our end has happened.", Toast.LENGTH_LONG).show();
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

    protected class VotingTask extends AsyncTask<String, String, Double>{
        private JSONObject result;

        @Override
        protected Double doInBackground(String... params){
            postData(params[0], params[1], params[2], params[3], params[4]);
            return null;
        }


        protected void onPostExecute(Double result){
            Log.d(TAG, "finished POST request");

            try {
                if (this.result.getString("success").equals("True")) {
//                    Toast.makeText(getApplicationContext(), "Vote succeeded.", Toast.LENGTH_LONG).show();
                }

                else{
                    Toast.makeText(getApplicationContext(), "Vote failed, please try again", Toast.LENGTH_SHORT).show();
                }
            }

            catch (Exception ex){
                Toast.makeText(getApplicationContext(), "Sorry an error on our end has happened!", Toast.LENGTH_LONG).show();
            }


        }

        public void postData(String username, String password, String id, String submission, String type){
            DefaultHttpClient vote_client = new DefaultHttpClient();
            JSONObject vote_json = new JSONObject();


            Log.d(TAG, "in VotingTask username - " + username + " password - " + password);
            try {
                vote_json.put("username", username);
                vote_json.put("password", password);
                vote_json.put("submission", submission);
                vote_json.put("id", id);
                String request = "http://104.131.71.174/api/v0/";
                if (type.equals("UP"))
                    request += "upvote";
                else if (type.equals("DOWN"))
                    request += "downvote";
                else
                    request += "clear_vote";

                HttpPost post_request = new HttpPost(request);
                StringEntity params = new StringEntity(vote_json.toString());
                post_request.addHeader("content-type", "application/json");
                post_request.setEntity(params);

                HttpResponse response = vote_client.execute(post_request);
                HttpEntity entity = response.getEntity();
                String entity_string = EntityUtils.toString(entity);

                result = new JSONObject(entity_string);

                Log.d(TAG, "success: " + result.getString("success"));

            } catch (Exception ex) {
                Log.d(TAG, "Exception: " + ex.toString());
            } finally {
                vote_client.getConnectionManager().shutdown();
                Log.d(TAG, "Finished");
            }
        }
    }

}
