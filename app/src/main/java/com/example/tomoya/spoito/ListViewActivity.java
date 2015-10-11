package com.example.tomoya.spoito;

/**
 * Created by Tomoya on 15/10/11.
 */
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        // リストビューに表示するためのデータを設定
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1);
        adapter.add("listview item 1");
        adapter.add("listview item 2");
        adapter.add("listview item 3");

        // リストビューにデータを設定
        ListView listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setAdapter(adapter);
    }
}
