package com.example.administrator.neext;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity implements Menu{
    private ListView next_list;
    private TextView event_name;
    private TextView event_time;
    private TextView event_right;
    private FloatingActionButton fab_delete;
    private Adapter adapter;
    private List<String[]> itemlist;
    private String[] content;
    private String sql_message = "";
    private String id;
    private String event_list_value;
    private String event_time_value;
    private String event_right_value;
    private String event_Title, year, month, day, hour, minute, Time_String;
    private String sqlquery_check = "select * from 'events'";
    private static final int NOSELECT_STATE = -1;
    public Toolbar toolbar;
    SQLiteHelper mg;
    private static SQLiteDatabase db;
    private int count =0;
    ArrayList<Integer> list_items;
    HashMap<Integer, Boolean> selectBoard;
    private boolean isInActionMode;
    private boolean isInDeleteMode=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SQLiteHelper.initManager(getApplication());
        mg = SQLiteHelper.getManager();
        db = mg.getDatabase("events1.db");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        next_list = (ListView) findViewById(R.id.next_list);
        event_name = (TextView) findViewById(R.id.event_name);
        event_time = (TextView) findViewById(R.id.event_time);
        event_right = (TextView) findViewById(R.id.event_right);
        content = new String[]{null, "我的下一件事", "30天", "1"};
        itemlist = new ArrayList();
        itemlist.add(content);
        adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
        next_list.setAdapter(adapter);
        selectBoard = new HashMap<Integer, Boolean>();
        refresh();
        for(int i=0;i<itemlist.size();i++){
            selectBoard.put(i, false);
        }

        fab_delete = (FloatingActionButton) findViewById(R.id.fab_delete);
//        registerForContextMenu(next_list);
//        next_list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            public void onCreateContextMenu(ContextMenu menu, View v,
//                                            ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add(0, 0, 0, "删除");
//                menu.add(0, 1, 0, "编辑");
//                menu.add(0, 2, 0, "多选");
//            }
//
//        });
        next_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        list_items = new ArrayList();
        next_list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                isInDeleteMode = true;
                mode.setTitle(count+"item selected");
                list_items.add(position);
                if(selectBoard.get(position)){
                    //如果有被选择,设为false，即取消选中
                    selectBoard.put(position, false);
                    if(count>0) {
                        count = count - 1;
                        mode.setTitle(count+"item selected");
                    }
                }else{
                    selectBoard.put(position, true);
                        count = count+1;
                        mode.setTitle(count+"item selected");
                }
                Toast.makeText(getApplicationContext(), "选中了"+position, Toast.LENGTH_SHORT).show();
                adapter.setColor(selectBoard);
                next_list.setAdapter(adapter);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_delete, menu);

                mode.setTitle("Delete");
                isInActionMode = true;
                isInDeleteMode = false;
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_delete:
                        isInDeleteMode = true;
                        for(int i=0;i<selectBoard.size();i++){
                            if(selectBoard.get(i)){
                                db.delete("events", "id=?", new String[]{String.valueOf(adapter.getIId(i))});
                                Toast.makeText(getApplicationContext(), "删除了"+i, Toast.LENGTH_SHORT).show();
                            }
                        }
                        mode.finish();
                        refresh();
                        return true;
                    case R.id.action_edit:
                        break;
                    case R.id.action_share:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                count=0;
                for(int i=0;i<itemlist.size();i++){
                    selectBoard.put(i, false);
                }
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Add_event.class);

                startActivityForResult(intent, 1);
                Cursor c = db.rawQuery(sqlquery_check, null);
                c.moveToFirst();
                itemlist.clear();
                content = new String[]{};//把之前的记录清零
                while (!c.isAfterLast()) {
                    //以下是查询所有事件的代码：
                    id = c.getString(c.getColumnIndex("id"));
                    event_list_value = c.getString(c.getColumnIndex("event_name"));
                    event_time_value = c.getString(c.getColumnIndex("event_time"));
                    event_right_value = c.getString(c.getColumnIndex("event_right"));
                    content = new String[]{id, event_list_value, event_time_value, event_right_value};
                    itemlist.add(content);
                    c.moveToNext();
                }
                c.close();
                adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
                adapter.notifyDataSetChanged();
                next_list.setAdapter(adapter);
                Snackbar.make(view, sql_message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    //点击搜索按钮：
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int res_id = item.getItemId();
        if(res_id==R.id.menu_search){
            Toast.makeText(getApplicationContext(), "shitSelected", Toast.LENGTH_SHORT).show();
        }else{

        }
        Intent intent_search = new Intent(MainActivity.this, Search_Event.class);
        startActivity(intent_search);
//        finish();

        return true;
    }
    public boolean sqldelete(int id){
        db.delete("events", "id=?", new String[]{String.valueOf(id)});
        return false;
    }
    //处理用户输入的返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {

                    String returnData = data.getStringExtra("data_return");
//                    Toast.makeText(getApplicationContext(), returnData, Toast.LENGTH_SHORT).show();
                    event_Title = returnData.split(":")[0];
                    year = returnData.split(":")[1];
                    month = returnData.split(":")[2];
                    day = returnData.split(":")[3];
                    hour = returnData.split(":")[4];
                    minute = returnData.split(":")[5];
                    Time_String = year+"-"+month+"-"+day+" "+hour+":"+minute;
                    String sqlquery_add = "insert into 'events' values(null, "+event_Title+", "+ Time_String+", 1"+")";
                    Log.d("MainActivity", returnData);

                    ContentValues values=new ContentValues();
//                    values.put("id",NULL);
                    values.put("event_name",event_Title);
                    values.put("event_time",Time_String);
                    values.put("event_right",1);
                    long result = db.insert("events", null, values);
                    Cursor c = db.rawQuery(sqlquery_check,null);
                    c.moveToFirst();
                    itemlist.clear();
                    content = new String[]{};//把之前的记录清零
                    while (!c.isAfterLast())
                    {
                        //以下是查询所有事件的代码：
                        id = c.getString(c.getColumnIndex("id"));
                        event_list_value = c.getString(c.getColumnIndex("event_name"));
                        event_time_value = c.getString(c.getColumnIndex("event_time"));
                        event_right_value = c.getString(c.getColumnIndex("event_right"));
                        content = new String[]{id, event_list_value, event_time_value, event_right_value};
                        itemlist.add(content);
                        c.moveToNext();
//
                    }
                    c.close();
                    adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
                    next_list.setAdapter(adapter);
                }else if (resultCode == RESULT_CANCELED) {
                    String returnData = data.getStringExtra("data_return");
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {

                    String returnData = data.getStringExtra("data_return");
                    String returnId = data.getStringExtra("id");

                    event_Title = returnData.split(":")[0];
                    year = returnData.split(":")[1];
                    month = returnData.split(":")[2];
                    day = returnData.split(":")[3];
                    hour = returnData.split(":")[4];
                    minute = returnData.split(":")[5];
                    Time_String = year+"-"+month+"-"+day+" "+hour+":"+minute;
                    Log.d("MainActivity", returnData);
                    update(returnId, event_Title, Time_String, "1");
                }else if (resultCode == RESULT_CANCELED) {
                    String returnData = data.getStringExtra("data_return");
                }
                refresh();
                break;
            default:
        }
    }
    public boolean update(String id, String event_name, String event_time, String event_right){
        ContentValues values=new ContentValues();
        values.put("id",id);
        values.put("event_right",event_right);
        values.put("event_time",event_time);
        values.put("event_name",event_name);
        db.update("events", values, "id = ?", new String[]{ id });
        return true;
    }
    @Override
    protected void onDestroy() {
        unregisterForContextMenu(next_list);
        super.onDestroy();
    }
    //给菜单项添加事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ContextMenu.ContextMenuInfo info = item.getMenuInfo();
        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo)info;
        int position = contextMenuInfo.position;//获取了item在list中的位置，即知道是长按了哪个item
        int id =  parseInt(adapter.getIId(position));
        Toast.makeText(getApplicationContext(), "所选的id是："+id, Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case 0:
                item.setChecked(true);
                sqldelete(id);
                refresh();
                break;
            case 1:
                item.setChecked(true);
                Intent intent = new Intent(MainActivity.this, Edit_Event.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 2);
                break;
            case 2:
                //多选
                item.setChecked(true);
                adapter.setMultiChecked(position);
                next_list.setAdapter(adapter);
                fab_delete.setVisibility(View.VISIBLE);
                fab_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<Integer, Boolean> CheckedList = adapter.getCheckedList();
                        Integer[] ids = null;
                        for(int i =0;i<CheckedList.size();i++){
//                            Toast.makeText(getApplicationContext(), "所删除的id是："+CheckedList.get(i), Toast.LENGTH_SHORT).show();
                            if(CheckedList.get(i)==true){
                                db.delete("events", "id=?", new String[]{String.valueOf(adapter.getIId(i))});
                            }
                        }
                        refresh();
                        fab_delete.setVisibility(View.GONE);
                    }
                });
                break;
        }
        return true;
    }
    //重新载入页面的代码
    public void refresh(){
        Cursor c = db.rawQuery(sqlquery_check, null);
        c.moveToFirst();
        itemlist.clear();
        content = new String[]{};//把之前的记录清零
        while (!c.isAfterLast()) {
            //以下是查询所有事件的代码：
            id = c.getString(c.getColumnIndex("id"));
            event_list_value = c.getString(c.getColumnIndex("event_name"));
            event_time_value = c.getString(c.getColumnIndex("event_time"));
            event_right_value = c.getString(c.getColumnIndex("event_right"));
            content = new String[]{id, event_list_value, event_time_value, event_right_value};
            itemlist.add(content);
            c.moveToNext();
        }
        c.close();
        adapter = new Adapter(getApplicationContext(), itemlist, NOSELECT_STATE);
        adapter.notifyDataSetChanged();
        next_list.setAdapter(adapter);
        for(int i=0;i<itemlist.size();i++){
            selectBoard.put(i, false);
        }
    }

    @Override
    public android.view.MenuItem add(CharSequence title) {
        return null;
    }

    @Override
    public MenuItem add(@StringRes int titleRes) {
        return null;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    @Override
    public MenuItem add(int groupId, int itemId, int order, @StringRes int titleRes) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(@StringRes int titleRes) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    @Override
    public SubMenu addSubMenu(int groupId, int itemId, int order, @StringRes int titleRes) {
        return null;
    }

    @Override
    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        return 0;
    }

    @Override
    public void removeItem(int id) {

    }

    @Override
    public void removeGroup(int groupId) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {

    }

    @Override
    public void setGroupVisible(int group, boolean visible) {

    }

    @Override
    public void setGroupEnabled(int group, boolean enabled) {

    }

    @Override
    public boolean hasVisibleItems() {
        return false;
    }

    @Override
    public MenuItem findItem(int id) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public MenuItem getItem(int index) {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        return false;
    }

    @Override
    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean performIdentifierAction(int id, int flags) {
        return false;
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {

    }


}



