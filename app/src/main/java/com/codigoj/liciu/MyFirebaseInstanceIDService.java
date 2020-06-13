package com.codigoj.liciu;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.codigoj.liciu.utils.DeleteTokenService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

/**
 * Created by root on 30/06/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("DeleteTokenService", "New token:"+ token);
    }

}
