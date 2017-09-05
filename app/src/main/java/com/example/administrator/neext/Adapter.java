package com.example.administrator.neext;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.administrator.neext.R.id.checkbox;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.parseInt;

/**
 * Created by Administrator on 2017/8/4 0004.
 */

public class Adapter extends BaseAdapter {
    String[] items;
    String[] id;
    ArrayList selectedColor = new ArrayList();
    private HashMap<Integer, Boolean> isChecked;// 用来记录是否被选中
    private HashMap<Integer, Boolean> selectBoard;// 用来记录是否被选中
    private boolean isMultiSelect = false;// 是否处于多选状态

    Map map=new HashMap();

    List<String[]> itemlist = new ArrayList();// 数据
    private Context mContext;
    public Adapter(Context mContext, List<String[]> itemlist1, int position){
        super();
        this.mContext = mContext;
        itemlist = itemlist1;
        selectBoard = new HashMap<Integer, Boolean>();
        isChecked = new HashMap<Integer, Boolean>();

            for (int i = 0; i < itemlist.size(); i++) {
                selectBoard.put(i, false);
                isChecked.put(i, false);
            }

        // 如果长按Item，则设置长按的Item中的CheckBox为选中状态
        if (isMultiSelect && position >= 0) {
            isChecked.put(position, true);
        }
    }
    public boolean setMultiChecked(int position){
        isMultiSelect=TRUE;
        isChecked.put(position, true);
        return true;
    }

    @Override
    public int getCount() {
        return itemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return itemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public String getIId(int position){
       return (String) map.get(position);
    }//id与position的对应表
    public ArrayList<Integer> getPositionChecked(){
        ArrayList<Integer> positionChecked = null;
        for(int i =0;i<=isChecked.size();i++){
            if(isChecked.get(i)==true){
                positionChecked.add(i);
            }//获取所有已经checked的position的id
        }
        return positionChecked;//返回所有选中项的id为arraylist
    }
    public HashMap<Integer, Boolean> getCheckedList(){
        return isChecked;
    }
    public void setColor(HashMap<Integer, Boolean> sboard){
        for(int i=0;i<itemlist.size();i++){
            if(sboard.get(i)){
                selectBoard.put(i,true);
            }else{
                selectBoard.put(i,false);
            };
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_layout, null);
            //换颜色
            if(selectBoard.get(position)){
                convertView.setBackgroundColor(Color.parseColor("#00CCFF") );
            }else{
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF") );
            }
            //
            holder = new ViewHolder();
            holder.checkbox1 = (CheckBox)convertView.findViewById(R.id.checkbox);
            holder.textView0 = (TextView) convertView.findViewById(R.id.event_id);
            holder.textView1 = (TextView) convertView.findViewById(R.id.event_name);
            holder.textView2 = (TextView) convertView.findViewById(R.id.event_time);
            holder.textView3 = (TextView) convertView.findViewById(R.id.event_right);
            holder.checkbox1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isChecked.get(position)==true){
                        holder.checkbox1.setChecked(false);
                        isChecked.put(position, false);
                    }else{
                        holder.checkbox1.setChecked(true);
                        isChecked.put(position, true);
                    }

                }
            });
            convertView.setTag(holder);

        }    else {
            holder = (ViewHolder) convertView.getTag();
        }

        String[] projectItem = itemlist.get(position);
        if(isMultiSelect){
            holder.checkbox1.setVisibility(View.VISIBLE);
        }else{
            holder.checkbox1.setVisibility(View.GONE);
        }

        holder.checkbox1.setChecked(isChecked.get(position));
        holder.textView0.setText(projectItem[0]);
        holder.textView1.setText(projectItem[1]);
        holder.textView2.setText(projectItem[2]);
        holder.textView3.setText(projectItem[3]);
        //把id和position对应起来。每一次都对应一次
        map.put(position, projectItem[0]);
        return convertView;
    }

    class ViewHolder{
        CheckBox checkbox1;
        TextView textView0;
        TextView textView1;
        TextView textView2;
        TextView textView3;
    }
}
