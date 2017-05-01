package be.webtito.ebitu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    /**
     * DB Testing..
     */
    static DBHelper myDB;
    static ArrayList<String> myChants = new ArrayList<String>();

     /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    ListView listView;

    private boolean mCurrentNightMode;
    private NearbyHandler mNearbyHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
        mCurrentNightMode = mPrefs.getBoolean("pref_night_mode", false);
        if(mCurrentNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);
        myDB = new DBHelper(this);

        mNearbyHandler = new NearbyHandler(this);

        /*listView = (ListView) findViewById(R.id.listView1);
        String[] Chants = {"Titre 1", "Titre 2", "Titre 3", "Titre 4", "Titre 5", "Titre 6", "Titre 7", "Titre 8", "Titre 9", "Titre 10", "Titre 11", "Titre 12",
                            "Titre 13", "Titre 14", "Titre 15", "Titre 17", "Titre 18", "Titre 19", "Titre 20", "Titre 21", "Titre 22", "Titre 23"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, Chants);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // l'index de l'item dans notre ListView
                int itemPosition = position;

                // On récupère le texte de l'item cliqué
                String itemValue = (String) listView
                        .getItemAtPosition(position);

                // On affiche ce texte avec un Toast
                Toast.makeText(
                        getApplicationContext(),
                        "Position :" + itemPosition + "  ListItem : "
                                + itemValue, Toast.LENGTH_LONG).show();
            }

        });*/
       /* boolean isOnline =isOnline(this.getBaseContext());
        if(isOnline) {
            Toast.makeText(null, "Pas de données dans la DB!", Toast.LENGTH_SHORT).show();
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }
    public static boolean isOnline(Context context) {
        boolean result = false;
        if (context != null) {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null) {
                    result = networkInfo.isConnected();
                }
            }
        }
        return result;
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();

        SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
        if(mCurrentNightMode != mPrefs.getBoolean("pref_night_mode", false)) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent nextScreen = new Intent(this, SettingsActivity.class);
            startActivity(nextScreen);
            return true;
        }

        if(id==R.id.about){
            Intent nextScreen = new Intent(this, AboutActivity.class);
            startActivity(nextScreen);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            try {
                myDB.createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //myDB.openDataBase();
            //Cursor res = myDB.getTitlesList();



/*            else{
                do{
                    //buffer.append("Title : "+ res.getString(0)+"\n");
                    myChants.add(res.getString(0));
                    Log.d("myChants",res.getString(res.getColumnIndex("Title")));
                } while(res.moveToNext());
            }
            res.moveToFirst();*/


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            final ListView listView = (ListView) rootView.findViewById(R.id.listView1);
            final Cursor allSongs = myDB.getAllSongs();
            allSongs.moveToFirst();
            if(allSongs.getCount() == 0) {
                Toast.makeText(getActivity(), "Pas de données dans la DB!", Toast.LENGTH_SHORT).show();
            }
            String[] fromFieldTitles = new String[] {DBHelper.COL_Title_2};
            int[] toViewIDs = new int[] {android.R.id.text1};
            final SimpleCursorAdapter allSongsAdapter = new SimpleCursorAdapter(this.getContext(), android.R.layout.simple_list_item_1, allSongs, fromFieldTitles, toViewIDs, 0);;
            int listID = getArguments().getInt(ARG_SECTION_NUMBER);
            if(listID == 1) {
                listView.setAdapter(allSongsAdapter);
            } else if(listID == 2) {

                final Cursor favedSongs = myDB.getAllFavorites();
                final SimpleCursorAdapter favedSongsAdapter = new SimpleCursorAdapter(this.getContext(), android.R.layout.simple_list_item_1, favedSongs, fromFieldTitles, toViewIDs, 0);;
                listView.setAdapter(favedSongsAdapter);
            } else {
                final Cursor lastSongs = myDB.getAllByLast();
                final SimpleCursorAdapter lastSongsAdapter = new SimpleCursorAdapter(this.getContext(), android.R.layout.simple_list_item_1, lastSongs, fromFieldTitles, toViewIDs, 0);;
                listView.setAdapter(lastSongsAdapter);
            }
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int position, long arg3) {
                    // TODO Auto-generated method stub
                    listView.invalidate();

                    Cursor song = (Cursor) allSongsAdapter.getItem(position);
                    updateLastSelected(song);

                    Intent intent = new Intent(getActivity(), SongActivity.class);
                    intent.putExtra("id", song.getInt(0));
                    startActivity(intent);
                }
            });


/*            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    // l'index de l'item dans notre ListView
                    int itemPosition = position;

                    // On récupère le texte de l'item cliqué
                    String itemValue = (String) listView
                            .getItemAtPosition(position);

                    // On affiche ce texte avec un Toast
                    Toast.makeText(
                            getApplicationContext(),
                            "Position :" + itemPosition + "  ListItem : "
                                    + itemValue, Toast.LENGTH_LONG).show();
                }

            });*/

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chants";
                case 1:
                    return "Favoris";
                case 2:
                    return "Historique";
            }
            return null;
        }
    }
    public static void updateLastSelected(Cursor song){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateSelected = df.format(c.getTime());
        Cursor res = myDB.updateDate(song.getInt(0),dateSelected);
        res.moveToFirst();
        res.close();
    }

/*    public void populateListView(){
        Cursor res = myDB.getTitlesList();
        String[] fromFieldTitles = new String[] {DBHelper.COL_Title_2};
        int[] toViewIDs = new int[] {android.R.id.text1};
        SimpleCursorAdapter myCursorAd;
        myCursorAd = new SimpleCursorAdapter(getBaseContext(),R.layout.fragment_main, res, fromFieldTitles, toViewIDs,0);

        ListView myList = (ListView) findViewById(R.id.listView1);
        myList.setAdapter(myCursorAd);
    }*/
}
