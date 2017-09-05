package com.example.administrator.neext;

/**
 * Created by Administrator on 2017/8/30 0030.
 */
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;
import static java.lang.Boolean.TRUE;

public class Edit_Event extends AppCompatActivity{
    private EditText editText;
    private TextView showdate;
    private TextView showtime;
    private int year;
    private int month;
    private int day;
    private int mhour;
    private int mminute;
    private Button btn1;
    private Button btn2;
    private FloatingActionButton fab2;
    private String sqlquery_check = "select * from 'events' where id =";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        editText = (EditText)findViewById(R.id.event_text);
//        timePicker = (TimePicker) findViewById(R.id.timePicker);
        showdate = (TextView)findViewById(R.id.time_view);
        showtime = (TextView)findViewById(R.id.time_view2);
        btn1 = (Button)findViewById(R.id.button1);
        btn2 = (Button)findViewById(R.id.button2);
        FloatingActionButton fab2;
        Calendar mycalendar=Calendar.getInstance();
        year=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day=mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
        mhour=mycalendar.get(Calendar.HOUR);//获取这个小时
        mminute=mycalendar.get(Calendar.MINUTE);//获取这个小时
        boolean is24hourview=TRUE;
        showdate.setText("当前日期:"+year+"-"+(month+2)+"-"+day); //显示当前的年月日
        showtime.setText("当前时间:"+mhour+"点"+mminute+"分"); //显示当前的年月日

        if(!getIntent().getExtras().get("id").toString().equals("none")){
            Toast.makeText(getApplicationContext(), "要编辑的是："+getIntent().getExtras().get("id").toString(), Toast.LENGTH_SHORT).show();
            String editId = getIntent().getExtras().get("id").toString();
            SQLiteHelper.initManager(getApplication());
            SQLiteHelper mg = SQLiteHelper.getManager();
            final SQLiteDatabase db1 = mg.getDatabase("events1.db");
            Cursor c = db1.rawQuery(sqlquery_check+editId, null);
            c.moveToFirst();
            String id = null;
            String event_list_value = null;
            String event_time_value = null;
            String event_right_value = null;
            ArrayList itemlist = new ArrayList();
            itemlist.clear();
            String[] content = new String[]{};//把之前的记录清零
            while (!c.isAfterLast()) {
                //以下是查询所有事件的代码：
                id = c.getString(c.getColumnIndex("id"));
//                    Toast.makeText(getApplicationContext(), "所选的id是："+id, Toast.LENGTH_SHORT).show();
                event_list_value = c.getString(c.getColumnIndex("event_name"));
                event_time_value = c.getString(c.getColumnIndex("event_time"));
                event_right_value = c.getString(c.getColumnIndex("event_right"));
                content = new String[]{id, event_list_value, event_time_value, event_right_value};
                itemlist.add(content);
                c.moveToNext();
            }
            c.close();
            editText.setText(event_list_value);
            String[] DateTime = event_time_value.split(" ");
            showdate.setText("选中日期："+DateTime[0]);
            showtime.setText("选中时间："+DateTime[1]);
        }



        //提交按钮：
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent=new Intent();
                month=month+1;
                intent.putExtra("data_return", editText.getText().toString()+":"+year+":"+month+":"+day+":"+mhour+":"+mminute);
                intent.putExtra("id", getIntent().getExtras().get("id").toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd=new DatePickerDialog(Edit_Event.this,Datelistener,year,month,day);
                dpd.show();

            }
        });
        final boolean finalIs24hourview = is24hourview;
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog tpd=new TimePickerDialog(Edit_Event.this, Timelistener,mhour,mminute, finalIs24hourview);
                tpd.show();
            }
        });
    }

    private DatePickerDialog.OnDateSetListener Datelistener=new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
            year=myyear;
            month=monthOfYear;
            day=dayOfMonth;
            //更新日期
            updateDate();
        }
        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate()
        {
            //在TextView上显示日期
            showdate.setText("选中日期："+year+"-"+(month+1)+"-"+day);
        }


    };
    private TimePickerDialog.OnTimeSetListener Timelistener=new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mhour = hourOfDay;
            mminute = minute;
            updateTime();
        }
        private void updateTime()
        {
            //在TextView上显示时间
            showtime.setText("选中时间:"+mhour+"点"+mminute+"分");

        }
    };
    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra("data_return", "Cancelled");
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
