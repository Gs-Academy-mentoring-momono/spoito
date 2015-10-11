package com.example.tomoya.spoito;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Tomoya on 15/10/04.
 */

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;

    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //access_tokenをローカルに保存
                SharedPreferences.Editor editor = getSharedPreferences(S.preferences, Context.MODE_PRIVATE).edit();
                editor.putString(S.fb_access_token, loginResult.getAccessToken().getToken());
                editor.apply();
                gotoMapsActivity();
                finish();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoMapsActivity(){
        Intent intent = MapsActivity.createIntent(this);
        startActivity(intent);
    }
}