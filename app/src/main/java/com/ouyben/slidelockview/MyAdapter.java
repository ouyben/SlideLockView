package com.ouyben.slidelockview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.List;

/**
 * TODO :
 * Created by owen
 * on 2016-11-01.
 */

public class MyAdapter extends BaseAdapter {

    private Context mContext;
    private List mList;
    private LayoutInflater mInflater;

    public MyAdapter(Context context, List list) {
        mContext = context;
        mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_layout, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.mSlideLockView.setLockListener(new SlideLockView.OnLockListener() {
            @Override
            public void onOpenLockSuccess() {
                Toast.makeText(mContext, "滑动成功,item:" + i, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public static class ViewHolder {
        public View rootView;
        public SlideLockView mSlideLockView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.mSlideLockView = (SlideLockView) rootView.findViewById(R.id.slideLockView);
        }

    }
}
