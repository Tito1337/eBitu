package be.webtito.ebitu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NearbyActivity extends AppCompatActivity {

    private String mNickname;
    private EditText mNicknameView;
    private NearbyHandler mNearbyHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        setupActionBar();

        mNearbyHandler = new NearbyHandler(this);

        mNicknameView = (EditText) findViewById(R.id.nickname);
        mNickname = getNickname();
        mNicknameView.setText(mNickname);

        Button mConnectButton = (Button) findViewById(R.id.connect_button);
        mConnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activateNearby();
            }
        });
    }

    private String getNickname() {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("nickname", getString(R.string.nearby_publisher));
    }

    private void setNickname(String nickname) {
        mNickname = nickname;
        SharedPreferences sharedPreferences = getSharedPreferences(
                getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("nickname", nickname).apply();
    }

    public void activateNearby() {
        setNickname(mNicknameView.getText().toString());
        //mNearbyHandler.new_publisher(getNickname());

        new AlertDialog.Builder(this)
                .setTitle(R.string.nearby_new_publisher_title)
                .setMessage(R.string.nearby_new_publisher_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent nextScreen = new Intent(NearbyActivity.this, MainActivity.class);
                        startActivity(nextScreen);
                    }})
                .show();
    }

    private void setupActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mNearbyHandler.onDestroy();
    }
}