package com.phicomm.smartplug.base;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class BaseListAdapter<T>
        extends BaseAdapter {
    private List<T> modelList;

    public int getCount() {
        if (modelList == null) {
            return 0;
        } else {
            return modelList.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public Object getItem(int position) {
        return modelList.get(position);
    }

    public List<T> getList() {
        return modelList;
    }

    public T getModel(int position) {
        return (T) modelList.get(position);
    }

    public abstract View getView(int position, View view, ViewGroup parent);

    public void setList(List<T> list) {
        modelList = list;
    }
}

