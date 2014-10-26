package com.cs371m.ads.karma_farm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


public class KarmaFarmSubmissionListActivity extends Activity {

    private ListView submissionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karma_farm_submission_list);


        // here is where we would make our HTTP request and parse the json into our Submission objects
        KarmaFarmSubmission submission_data[] = new KarmaFarmSubmission[]
                {
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                        new KarmaFarmSubmission("2kag29", "test title", "http://i.imgur.com/5fTrt0I.gif", 1000, R.drawable.placeholder),
                };

        KarmaFarmSubmissionAdapter adapter = new KarmaFarmSubmissionAdapter(this, R.layout.listview_item_row, submission_data);

        submissionList = (ListView)findViewById(R.id.listView1);

        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);

        submissionList.addHeaderView(header);
        submissionList.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.karma_farm_submission_list, menu);
        return true;
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
