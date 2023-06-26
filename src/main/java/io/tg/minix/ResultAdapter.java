package io.tg.minix;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultAdapter extends BaseAdapter {

    private final ArrayList<String> list;
    private final LayoutInflater layoutInflater;

    public ResultAdapter(Activity activity, ArrayList<String> list) {
        this.list = list;
        this.layoutInflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Holder holder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_result, viewGroup, false);
            convertView.setTag(holder = new Holder(convertView));
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.refresh(list.get(i));

        return convertView;
    }

    public static class Holder {

        private final TextView content;

        public Holder(View root) {
            content = (TextView) root;
        }

        public void refresh(String s) {
            content.setText(s);
        }
    }
}
