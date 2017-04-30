package be.webtito.ebitu;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

public class SongActivity extends AppCompatActivity {

    TextView textViewLyrics;
    ClipData.Item ItemFavoris;
    private boolean isFavorite;
    private int mSongID;
    private String mSongTitle;
    static DBHelper myDB;

    private NearbyHandler mNearbyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        isFavorite = false;
        textViewLyrics = (TextView) findViewById(R.id.textViewLyrics);
        String Lyrics;

        myDB = new DBHelper(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Lyrics= null;
            } else {
                //Lyrics= extras.getString("chant");
                mSongID = extras.getInt("id");
                Cursor res = myDB.getChantByID(mSongID);
                res.moveToFirst();
                mSongTitle = res.getString(1);
                Lyrics = res.getString(2);
                if(res.getInt(4) == 1){
                    isFavorite = true;
                }
                else {
                    isFavorite = false;
                }
            }
        } else {
            Lyrics= (String) savedInstanceState.getSerializable("chant");
        }

        setTitle(mSongTitle);
        textViewLyrics.setText(Lyrics);

        SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
        int textColor = mPrefs.getInt("settings_color_text", getResources().getColor(R.color.black));
        int backColor = mPrefs.getInt("settings_color_background", getResources().getColor(R.color.white));
        ScrollView songForm = (ScrollView) findViewById(R.id.song_form);
        textViewLyrics.setTextColor(textColor);
        songForm.setBackgroundColor(backColor);

        mNearbyHandler = new NearbyHandler(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favoris, menu);
        updateFavorite(menu);
        return true;
    }

    private void updateFavorite(Menu menu) {
        if(isFavorite){
            menu.findItem(R.id.action_favoris).setIcon(android.R.drawable.btn_star_big_on);
        }
        else {
            menu.findItem(R.id.action_favoris).setIcon(android.R.drawable.btn_star_big_off);
        }
    }

    private void setFavorite(boolean s) {
        Cursor res = myDB.updateFave(mSongID,s);
        res.moveToFirst();
        res.close();
        isFavorite = s;
    }


    public String getSongTitle() {
        return mSongTitle;
    }

    public int getSongID() {
        return mSongID;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favoris) {
            if(isFavorite) {
                item.setIcon(android.R.drawable.btn_star_big_off);
                setFavorite(false);
            } else {
                item.setIcon(android.R.drawable.btn_star_big_on);
                setFavorite(true);
            }
        }

        if (id == R.id.action_nearby) {
            mNearbyHandler.publish_song(getSongTitle(), getSongID());
        }

        return super.onOptionsItemSelected(item);
    }

}


