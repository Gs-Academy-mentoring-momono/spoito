package com.example.tomoya.spoito;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        String accessToken = getSharedPreferences(S.preferences, Context.MODE_PRIVATE).getString(S.fb_access_token,null);
        if(accessToken == null){
            goToLoginActivity();
            finish();
        } else {
            goToMapActivity();
            finish();
        }
    }

    public void goToMapActivity(){
        startActivity(MapsActivity.createIntent(this));

    }

    public void goToLoginActivity(){
        startActivity(LoginActivity.createIntent(this));
    }
}
