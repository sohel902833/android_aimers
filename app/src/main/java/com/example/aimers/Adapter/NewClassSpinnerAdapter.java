package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aimers.Model.NewClassModel;
import com.example.aimers.R;

import java.util.List;


public class NewClassSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<NewClassModel> values;

    public NewClassSpinnerAdapter(Context context, List<NewClassModel> values) {
        this.context = context;
        this.values=values;
    }

    @Override
    public int getCount() {
            return values.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.spinner_item_layout, parent, false);
            TextView textView=convertView.findViewById(R.id.spinnerHeaderTExt);
            textView.setText(values.get(position).getBatchName()+""+values.get(position).getDepartmentName()+"("+values.get(position).getSession()+")-"+values.get(position).getGroup());
        }
        return convertView;
    }
}
