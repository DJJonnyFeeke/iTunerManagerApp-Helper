package me.personal.jfeeke.multimediamobileviewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

class MyStringPairAdapter extends BaseAdapter {
    private final Activity activity;
    private final List<MyStringPair> stringPairList;


    MyStringPairAdapter(Activity activity, List<MyStringPair> stringPairList) {
        super();
        this.activity = activity;
        this.stringPairList = stringPairList;
    }

    @Override
    public int getCount() {
        return stringPairList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringPairList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            convertView = inflater.inflate(R.layout.data_listrow, null);
        }
        TextView col1 = convertView.findViewById(R.id.column1);
        TextView col2 = convertView.findViewById(R.id.column2);
        TextView col3 = convertView.findViewById(R.id.column3);
        TextView col4 = convertView.findViewById(R.id.column4);
        TextView col5 = convertView.findViewById(R.id.column5);
        TextView col6 = convertView.findViewById(R.id.column6);
        TextView col7 = convertView.findViewById(R.id.column7);

        col1.setText(stringPairList.get(position).getColumnOne());
        col2.setText(stringPairList.get(position).getColumnTwo());
        col3.setText(stringPairList.get(position).getColumnThree());
        col4.setText(stringPairList.get(position).getColumnFour());
        col5.setText(stringPairList.get(position).getColumnFive());
        col6.setText(stringPairList.get(position).getColumnSix());
        col7.setText(stringPairList.get(position).getColumnSeven());

        return convertView;
    }
}
