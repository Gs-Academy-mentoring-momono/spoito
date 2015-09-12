package com.example.tomoya.spoito;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

import io.realm.Realm;

/**
 * ロケーションのタイトルや情報などを保存するActivity
 * データはRealmDBに保存する
 */
public class AddLocationInfoActivity extends AppCompatActivity {

    Realm mRealm;

    public static Intent createIntent(Context context, LatLng latLng){
        Intent intent = new Intent(context, AddLocationInfoActivity.class);
        intent.putExtra("location", latLng);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_info); //TODO 画面作る, タイトルや情報いれてOKするとデータをDBに保存する
        //データを保存後は finish()を実行して画面を閉じる。そうすると前の画面(MapsActivityに戻る)
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_location_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(mRealm != null){
            mRealm.close();
        }
        super.onDestroy();
    }
}
