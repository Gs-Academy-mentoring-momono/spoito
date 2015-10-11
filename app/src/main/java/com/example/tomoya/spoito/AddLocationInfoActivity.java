package com.example.tomoya.spoito;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;



import com.google.android.gms.maps.model.LatLng;

import java.io.FileDescriptor;
import java.io.IOException;

import io.realm.Realm;

/**
 * ロケーションのタイトルや情報などを保存するActivity
 * データはRealmDBに保存する
 */
public class AddLocationInfoActivity extends AppCompatActivity {

    Realm mRealm;

    private InputMethodManager inputMethodManager;
    private LinearLayout addLayout;
    private EditText titleEdittext;
    private EditText detailInfoEdittext;
    private Button submit_btn;
    private static final int RESULT_PICK_IMAGEFILE = 1001;
    private ImageView imageView;
    private Button button;
    private Uri mPictureUri;




    public static Intent createIntent(Context context, LatLng latLng){
        Intent intent = new Intent(context, AddLocationInfoActivity.class);
        intent.putExtra("location", latLng);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location_info);
        //TODO 画面作る, タイトルや情報いれてOKするとデータをDBに保存する
        //データを保存後は finish()を実行して画面を閉じる。そうすると前の画面(MapsActivityに戻る)
        titleEdittext = (EditText)findViewById(R.id.title);
        detailInfoEdittext = (EditText)findViewById(R.id.comment);
        submit_btn = (Button) findViewById(R.id.submit_btn);
        submit_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        imageView = (ImageView)findViewById(R.id.image_view);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                // Filter to only show results that can be "opened", such as a
                // file (as opposed to a list of contacts or timezones)
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                // Filter to show only images, using the image MIME data type.
                // it would be "*/*".
                intent.setType("image/*");

                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }


        });



//        ーーーーー背景クリックでキーボード隠す処理↓ーーーーー
        //画面全体のレイアウト
        addLayout = (LinearLayout)findViewById(R.id.addLayout);
        //キーボード表示を制御するためのオブジェクト
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("", "Uri: " + uri.toString());

                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void submit() {
//        タイトルがない時はトースト
        String title = titleEdittext.getText().toString();
        String detailInfo = detailInfoEdittext.getText().toString();
        if (title.equals("")) {
            Toast.makeText(getApplicationContext(), "タイトルを入力してください。",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mRealm = Realm.getInstance(this);


//... add or update objects here ...


        LatLng latLng = getIntent().getParcelableExtra("location"); //前の画面からの緯度経度情報
        mRealm = Realm.getInstance(this);
        mRealm.beginTransaction();
        LocationData locationData = mRealm.createObject(LocationData.class); // Create a new object
        locationData.setTitle(title);
        locationData.setDetailInfo(detailInfo);
        locationData.setLatitude(latLng.latitude);
        locationData.setLongitude(latLng.longitude);
        locationData.setUriString(mPictureUri.toString()); //Uriをstringにして保存してあげる
        mRealm.commitTransaction();

        Toast.makeText(getApplicationContext(), "保存しました",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    /**
     * EditText編集時に背景をタップしたらキーボードを閉じるようにするタッチイベントの処理
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //キーボードを隠す
        inputMethodManager.hideSoftInputFromWindow(addLayout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //背景にフォーカスを移す
        addLayout.requestFocus();

        return false;
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
