/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.webtito.ebitu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

public class NearbyHandler implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener  {
    private static final String TAG = NearbyActivity.class.getSimpleName();

    private static final int TTL_IN_SECONDS = 30 * 60; // 30 minutes.

    private Strategy PUB_SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(TTL_IN_SECONDS).build();

    private static final int NEW_PUBLISHER = 1;
    private static final int NEW_SONG = 2;

    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private Message mPubMessage;
    private Activity mActivity;
    private String mUUID;

    private Handler mHandler = new Handler();

    /**
     * Creates a UUID and saves it to {@link SharedPreferences}. The UUID is added to the published
     * message to avoid it being undelivered due to de-duplication. See {@link NearbyMessage} for
     * details.
     */
    private String getUUID() {
        SharedPreferences sharedPreferences = mActivity.getApplicationContext().getSharedPreferences(
                mActivity.getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        String uuid = sharedPreferences.getString("Nearby_UUID", "");
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
            sharedPreferences.edit().putString("Nearby_UUID", uuid).apply();
        }
        return uuid;
    }

    /**
     * Adapter for working with messages from nearby publishers.
     */
    private ArrayAdapter<String> mNearbyMessagesArrayAdapter;

    public NearbyHandler(Activity builder) {
        mActivity = builder;

        mMessageListener = new MessageListener() {
            @Override
            public void onFound(final Message message) {
                // Called when a new message is found.
                final NearbyMessage nMessage = NearbyMessage.fromNearbyMessage(message);
                //mNearbyMessagesArrayAdapter.add(messageBody);

                boolean ignore = false;
                if(mActivity instanceof SongActivity) {
                    SongActivity song = (SongActivity) mActivity;
                    if(song.getSongID() == nMessage.getSong()) {
                        ignore = true;
                    }
                }

                if(!ignore) {
                    new AlertDialog.Builder(mActivity)
                            .setTitle(R.string.nearby_new_publisher_title)
                            .setMessage(nMessage.getSender() + " propose de chanter \"" + nMessage.getTitle() + "\" \n\nVeux-tu charger ce chant ?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Intent nextScreen = new Intent(mActivity.getApplicationContext(), SongActivity.class);
                                    nextScreen.putExtra("id", nMessage.getSong());
                                    mActivity.startActivity(nextScreen);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                            /*Intent nextScreen = new Intent(mActivity.this, MainActivity.class);
                            startActivity(nextScreen);*/
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onLost(final Message message) {
                // Called when a message is no longer detectable nearby.
            }
        };

        // If GoogleApiClient is connected, perform sub actions in response to user action.
        // If it isn't connected, do nothing, and perform sub actions when it connects (see
        // onConnected()).
        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            subscribe();
        } else {
            //TODO
        }*/

        mUUID = getUUID();

        final List<String> nearbyDevicesArrayList = new ArrayList<>();
        mNearbyMessagesArrayAdapter = new ArrayAdapter<>(mActivity,
                android.R.layout.simple_list_item_1,
                nearbyDevicesArrayList);

        buildGoogleApiClient();
        mHandler.postDelayed(new Runnable() {
            public void run() {
                mGoogleApiClient.connect();
            }
        }, 1000);
    }

    public void publish_song(String title, int id) {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            publish(title, id);
        } else {
            // TODO
        }
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private static final int REQUEST_RESOLVE_ERROR = 1001;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(mActivity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            logAndShowSnackbar("Exception while connecting to Google Play services: " +
                    result.getErrorMessage());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        logAndShowSnackbar("Connection suspended. Error code: " + i);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        subscribe();
    }

    /**
     * Subscribes to messages from nearby devices and updates the UI if the subscription either
     * fails or TTLs.
     */
    private void subscribe() {
        Log.i(TAG, "Subscribing");
        mNearbyMessagesArrayAdapter.clear();
        SubscribeOptions options = new SubscribeOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "No longer subscribing");
                        // TODO
                    }
                }).build();

        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Subscribed successfully.");
                        } else {
                            logAndShowSnackbar("Could not subscribe, status = " + status);
                        }
                    }
                });
    }

    private void publish(String title, int id) {
        Log.i(TAG, "Publishing");
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(PUB_SUB_STRATEGY)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(TAG, "No longer publishing");
                    }
                }).build();

        SharedPreferences mPrefs =  PreferenceManager.getDefaultSharedPreferences(mActivity);
        String name = mPrefs.getString("settings_display_name", "Anonyme");

        mPubMessage = NearbyMessage.newNearbyMessage(mUUID, name, title, id);

        Nearby.Messages.publish(mGoogleApiClient, mPubMessage, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            logAndShowSnackbar("Le chant est maintenant proposé aux utilisateurs d'eBitu à proximité !");
                        } else {
                            logAndShowSnackbar("Could not publish, status = " + status);
                        }
                    }
                });
    }

    /**
     * Stops subscribing to messages from nearby devices.
     */
    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }

    /**
     * Stops publishing message to nearby devices.
     */
    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
    }

    /**
     * Logs a message and shows a {@link Snackbar} using {@code text};
     *
     * @param text The text used in the Log message and the SnackBar.
     */
    private void logAndShowSnackbar(final String text) {
        Log.w(TAG, text);
        Toast.makeText(mActivity.getApplication().getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    /*public void onDestroy() {
        unpublish();
        unsubscribe();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }*/
}