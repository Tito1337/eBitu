package be.webtito.ebitu;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
     /*   String Lyrics = "Hakuna Matata,\n" +
                "Mais quelle phrase magnifique\n" +
                "\n" +
                "Hakuna Matata,\n" +
                "Quel chant fantastique!\n" +
                "\n" +
                "Ces mots signifient\n" +
                "Que tu vivras ta vie,\n" +
                "Sans aucun souci,\n" +
                "Philosophie\n" +
                "Ce très jeune phacochère\n" +
                "J'étais jeune et phacochère\n" +
                "Bel organe\n" +
                "Merci!\n" +
                "Un jour, quelle horreur\n" +
                "Il comprit que son odeur\n" +
                "Au lieu de sentir la fleur\n" +
                "Soulevait les coeurs.\n" +
                "\n" +
                "Mais y'a dans tout cochon\n" +
                "Un poète qui sommeille.\n" +
                "Quel martyr\n" +
                "Quand personne\n" +
                "Peut plus vous sentir!\n" +
                "Disgrâce infâme (Parfum d'infâme)\n" +
                "Inonde mon âme (Oh! Ça pue le drame)\n" +
                "Je déclenche une tempête (Pitié, arrête!)\n" +
                "A chaque fois que je ...\n" +
                "Non Pumbaa, pas devant les enfants!\n" +
                "Oh! Pardon!\n" +
                "\n" +
                "Hakuna Matata,\n" +
                "Mais quelle phrase magnifique!\n" +
                "Hakuna Matata,\n" +
                "Quel chant fantastique!\n" +
                "Ces mots signifient\n" +
                "Que tu vivras ta vie,\n" +
                "Ouais, chante petit!\n" +
                "\n" +
                "Sans aucun souci\n" +
                "Philosophie\n" +
                "Hakuna Matata!\n" +
                "Hakuna Matata,\n" +
                "Hakuna Matata,\n" +
                "Hakuna Matata,\n" +
                "Hakuna\n" +
                "Ces mots signifient\n" +
                "Que tu vivras ta vie,\n" +
                "Sans aucun souci,\n" +
                "Philosophie\n" +
                "Hakuna Matata!\n" +
                "Hakuna Matata,\n" +
                "Hakuna Matata,\n";*/

        textViewLyrics.setText(Lyrics);

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


