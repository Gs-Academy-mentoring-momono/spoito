package com.example.tomoya.spoito;

/**
 * Created by Tomoya on 15/10/11.
 */
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListViewActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

// ListViewのインスタンスを取得
        ListView list = (ListView)findViewById(R.id.listView1);

        // リストアイテムのラベルを格納するArrayListをインスタンス化
        ArrayList<String> labelList = new ArrayList<String>();

        // "List Item + ??"を20個リストに追加
        for(int i=1; i<=20; i++){
            labelList.add("List Item "+i);
        }

        // Adapterのインスタンス化
        // 第三引数にlabelListを渡す
        CustomAdapter mAdapter = new CustomAdapter(this, 0, labelList);

        // リストにAdapterをセット
        list.setAdapter(mAdapter);

        // リストアイテムの間の区切り線を非表示にする
        list.setDivider(null);
    }

}
