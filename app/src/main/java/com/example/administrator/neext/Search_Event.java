package com.example.administrator.neext;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import java.util.ArrayList;
import android.os.Message;
/**
 * Created by Administrator on 2017/9/1 0001.
 */

public class Search_Event extends AppCompatActivity{
    Toolbar toolbar_search;
    EditText editText;
    private String sqlquery_check = "select * from 'events' where event_name like '%";
    private ArrayList itemlist;
    private String[] content;
    private String id;
    private String event_name;
    private String event_time;
    private String event_right;
    private Adapter adapter;
    private static final int NOSELECT_STATE = -1;
    private ListView search_result_list;
    private ImageButton back_button;


    SQLiteHelper mg;
    private static SQLiteDatabase db1;
    @Override
    public void onCreate(Bundle SavedInstance){
        SQLiteHelper.initManager(getApplication());
        mg = SQLiteHelper.getManager();
        db1 = mg.getDatabase("events1.db");
        setContentView(R.layout.search);
        super.onCreate(SavedInstance);
        toolbar_search = (Toolbar)findViewById(R.id.search_bar);
        search_result_list = (ListView)findViewById(R.id.search_result_list);
        back_button = (ImageButton)findViewById(R.id.back_arrow);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent();
//                startActivity(intent);
                finish();
            }
        });
        setSupportActionBar(toolbar_search);
        itemlist = new ArrayList();
        editText = (EditText)findViewById(R.id.search_content);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //返回的是上一个值
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editText.getText().toString().equals("")){
                    Cursor c = db1.rawQuery(sqlquery_check+s+"%'",null);
                    if(c.getCount()==0){
                        Toast.makeText(getApplicationContext(), "无结果", Toast.LENGTH_SHORT).show();
                    }
                    c.moveToFirst();
                    itemlist.clear();
                    content = new String[]{};//把之前的记录清零
                    while (!c.isAfterLast())
                    {
                        id = c.getString(c.getColumnIndex("id"));
                        event_name = c.getString(c.getColumnIndex("event_name"));
                        event_time = c.getString(c.getColumnIndex("event_time"));
                        event_right = c.getString(c.getColumnIndex("event_right"));
                        content = new String[]{id, event_name, event_time, event_right};
                        itemlist.add(content);
                        c.moveToNext();
                    }
                    c.close();
                    adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
                    search_result_list.setAdapter(adapter);
                }else{
                    itemlist.clear();
                    adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
                    search_result_list.setAdapter(adapter);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
//                if (s.equals("")){
//                    itemlist.clear();
//                    adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
//                    search_result_list.setAdapter(adapter);
//                }
            }
        });
    }
//    public Handler mHandler=new Handler()
//    {
//        public void handleMessage(Message msg)
//        {
//            switch(msg.what)
//            {
//                case 1:
//
//                    break;
//                default:
//                    break;
//            }
//            super.handleMessage(msg);
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_functions, menu);
        return true;
    }
    //点击搜索按钮：
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int res_id = item.getItemId();
        switch (res_id){
            case R.id.submit:
//                String s = editText.getText().toString();
//                Toast.makeText(getApplicationContext(), "searching for "+s, Toast.LENGTH_SHORT).show();
//                Cursor c = db1.rawQuery(sqlquery_check+s+"%'",null);
//                c.moveToFirst();
//                itemlist.clear();
//                content = new String[]{};//把之前的记录清零
//                while (!c.isAfterLast())
//                {
//                    //以下是查询所有事件的代码：
//                    id = c.getString(c.getColumnIndex("id"));
//                    event_name = c.getString(c.getColumnIndex("event_name"));
//                    event_time = c.getString(c.getColumnIndex("event_time"));
//                    event_right = c.getString(c.getColumnIndex("event_right"));
////                    event_name.setText(event_list_value);
////                    event_time.setText(event_time_value);
////                    event_right.setText(event_right_value);
//                    content = new String[]{id, event_name, event_time, event_right};
//                    itemlist.add(content);
//                    c.moveToNext();
////
//                }
//                c.close();
//                adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
//                search_result_list.setAdapter(adapter);
                break;
            default:
                return true;
        }


        return true;
    }
    @Override
    public void onBackPressed() {
//        Intent intent=new Intent();
//        intent.putExtra("data_return", "Cancelled");
//        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
