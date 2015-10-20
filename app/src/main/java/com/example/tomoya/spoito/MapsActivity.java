package com.example.tomoya.spoito;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;



public class MapsActivity extends AppCompatActivity {

    static class MyInfoWindowAdapter implements InfoWindowAdapter{

        private final View myContentsView;
        private Context mContext;
        private HashMap<LatLng,String> mUriList;
        MyInfoWindowAdapter(Context context){
            mContext = context;
            myContentsView = View.inflate(mContext, R.layout.custom_info_contents,null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());
            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());
            ImageView pict = ((ImageView)myContentsView.findViewById(R.id.pict));
            Picasso.with(mContext)
                    .load(Uri.parse(mUriList.get(marker.getPosition())))
                    .resize(200,150)
                    .centerCrop()
                    .into(pict);

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

        public void setUriList(HashMap<LatLng,String> list){
            mUriList = list;
        }

    }

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private int mPositionNum = 0;
    private HashMap<LatLng, String> mUriMap = new HashMap<>();
    private static final int MENU_DELETE_MARKERS = 0;
    private static final int GO_TO_LISTVIEW = 1;
    private static final int REQUEST_FOR_LOCATION_INFO = 100;
    Realm mRealm;

    public static Intent createIntent(Context context){
        Intent intent = new Intent(context, MapsActivity.class);
        return intent;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        facebookログイン
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();
        MyInfoWindowAdapter myInfoWindowAdapter = new MyInfoWindowAdapter(this);
        myInfoWindowAdapter.setUriList(mUriMap);
        mMap.setInfoWindowAdapter(myInfoWindowAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        if(mRealm != null){
            mRealm.close();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_DELETE_MARKERS, Menu.NONE, "(Debug用)マーカー消す");
        menu.add(Menu.NONE, GO_TO_LISTVIEW, Menu.NONE, "リストビューに飛ぶ");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case MENU_DELETE_MARKERS:
                deleteDatafromRealm();
                return true;
            case GO_TO_LISTVIEW:
                gotoListViewActivity();
        }

        return false;
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    /**
     * データベースに保存されている位置情報データをマップに表示する
     */
    private void setUpMap() {
        loadDataFromRealm();
        LatLng clicklat = new LatLng(35.671241, 139.765041);
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(clicklat)      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(80)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.addMarker(new MarkerOptions()
                .position(clicklat)
                .title("クリックした場所"));
//              .icon(BitmapDescriptorFactory.fromResource(R.drawable.you)));
        mMap.getFocusedBuilding();




        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                mPositionNum++;
                //TODO: 長押ししたら、そこの位置情報を情報登録画面に渡してあげる。情報を登録したら、mapの画面に戻ってくる
                Intent intent = new Intent(getApplication(), AddLocationInfoActivity.class);
                intent.putExtra("location", latLng);
                startActivityForResult(intent, REQUEST_FOR_LOCATION_INFO);
                //gotoAddLocationInfoActivity(latLng);


            }
        });
    }

    /**
     * ロケーションをDBに保存する
     * @param latLng
     */
    private void saveLocation(LatLng latLng){
        if(mRealm == null) {
            mRealm = Realm.getInstance(this);
        }
        mRealm.beginTransaction();
        LocationData data = mRealm.createObject(LocationData.class);
        data.setLatitude(latLng.latitude);
        data.setLongitude(latLng.longitude);
        data.setTitle("position " + mPositionNum);
        mRealm.commitTransaction();
    }

    private void drawMarker(LatLng latLng){
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("position " + mPositionNum));
    }

    private void gotoAddLocationInfoActivity(LatLng latLng){
        Intent intent = AddLocationInfoActivity.createIntent(this, latLng);
        startActivityForResult(intent, REQUEST_FOR_LOCATION_INFO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FOR_LOCATION_INFO){
            //DBから再ロードする
            loadDataFromRealm();
        }
    }

    private void loadDataFromRealm(){
        mUriMap.clear();
        if(mRealm == null) {
            mRealm = Realm.getInstance(this);
        }
        RealmQuery<LocationData> query = mRealm.where(LocationData.class);

        RealmResults<LocationData> realmResults = query.findAll();
        HashMap<LatLng,String> uriList = new HashMap<>();
        for (LocationData data: realmResults ) {
            LatLng latLng = new LatLng(data.getLatitude(), data.getLongitude());
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)

                    .title(data.getTitle())
                    .snippet(data.getDetailInfo())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.you)));
            uriList.put(latLng,data.getUriString());
        }
        mUriMap.putAll(uriList);
    }

    private void deleteDatafromRealm(){
        if(mRealm == null) {
            mRealm = Realm.getInstance(this);
        }
        mRealm.beginTransaction();
        RealmQuery<LocationData> query = mRealm.where(LocationData.class);
        RealmResults<LocationData> realmResults = query.findAll();
        realmResults.clear();
        mRealm.commitTransaction();

        mMap.clear();
        mPositionNum = 0;
    }

    private void gotoListViewActivity(){
        Intent intent = new Intent(MapsActivity.this,ListViewActivity.class);
        startActivity(intent);
    }

}






