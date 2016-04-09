package org.imarks.vliegertest.model;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

/**
 * Created by Mark on 29-3-2016.
 */
public final class ApiClient implements GoogleApiClient.OnConnectionFailedListener {

    private static ApiClient instance = null;

    public GoogleApiClient mGoogleApiClient;

    public static synchronized ApiClient getInstance()
    {
        if (instance == null){
            instance = new ApiClient();
        }

        return instance;
    }

    public void setApiClient(Context context, FragmentActivity activity){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage(activity,
                        this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        mGoogleApiClient.connect();
    }

    public GoogleApiClient getApiClient(){
        return mGoogleApiClient;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
