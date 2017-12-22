package com.example.malai.authenticate_app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
/**
 * Created by aarumuga on 31-08-2017.
 */

public class Authenticator_Prolife_Service extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        ProlifeAuthenticator authenticator = new ProlifeAuthenticator(this);
        return authenticator.getIBinder();
    }
}
