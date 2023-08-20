package com.example.aimers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.aimers.Model.Batch;
import com.example.aimers.R;

import java.util.List;


public class BatchSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Batch> values;

    public BatchSpinnerAdapter(Context context, List<Batch> values) {
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

            Batch batch=values.get(position);

            String value=batch.getBatchName()+"("+batch.getSession()+")-"+batch.getGroup();
            textView.setText(""+value);
        }
        return convertView;
    }
}
