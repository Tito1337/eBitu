package be.webtito.ebitu;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);
        isFavorite = false;
        textViewLyrics = (TextView) findViewById(R.id.textViewLyrics);
        String Lyrics;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                Lyrics= null;
            } else {
                Lyrics= extras.getString("chant");
            }
        } else {
            Lyrics= (String) savedInstanceState.getSerializable("chant");
        }

        textViewLyrics.setText(Lyrics);

        SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(this);
        int textColor = mPrefs.getInt("settings_color_text", textViewLyrics.getTextColors().getDefaultColor());
        int backColor = mPrefs.getInt("settings_color_background", Color.TRANSPARENT);
        ScrollView songForm = (ScrollView) findViewById(R.id.song_form);
        textViewLyrics.setTextColor(textColor);
        songForm.setBackgroundColor(backColor);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favoris, menu);
        return true;
    }

    private void setFavorite(boolean s) {
        //TODO : store in DB
        isFavorite = s;
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
        return super.onOptionsItemSelected(item);
    }
}


