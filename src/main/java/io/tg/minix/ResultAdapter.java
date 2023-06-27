package io.tg.minix;

import android.app.Activity;
import android.app.AlertDialog;
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
            convertView.setTag(holder = new Holder(this, convertView, list));
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.refresh(i);

        return convertView;
    }

    public static class Holder implements View.OnClickListener {

        private final TextView content;

        private final ResultAdapter resultAdapter;

        private final ArrayList<String> list;

        private int currentIdx;

        public Holder(ResultAdapter adapter, View root, ArrayList<String> list) {
            this.resultAdapter = adapter;
            this.list = list;
            this.content = root.findViewById(R.id.content);
            root.findViewById(R.id.delete).setOnClickListener(this);
        }

        public void refresh(int i) {
            currentIdx = i;
            content.setText(list.get(i));
        }

        @Override
        public void onClick(View view) {
            new AlertDialog.Builder(view.getContext())
                .setMessage("确定要删除吗?")
                .setPositiveButton("确定", (dialogInterface, i) -> {
                    list.remove(currentIdx);
                    resultAdapter.notifyDataSetChanged();
                })
                .setNegativeButton("取消", null)
                .show();
        }
    }
}
