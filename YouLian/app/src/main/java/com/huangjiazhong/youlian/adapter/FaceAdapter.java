package com.huangjiazhong.youlian.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.huangjiazhong.youlian.R;
import com.huangjiazhong.youlian.xmpp.XXApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/23.
 */

public class FaceAdapter extends BaseAdapter {

    private Context context;
    private Map<String, Integer> mFaceMap;
    private List<Integer> faceList = new ArrayList<Integer>();

    public FaceAdapter(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        mFaceMap = XXApp.getInstance().getFaceMap();
        initData();
    }

    private void initData() {
        Set<Map.Entry<String, Integer>> entries = mFaceMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
            faceList.add(entry.getValue());
        }
    }

    @Override
    public int getCount() {
        return faceList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return faceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView =View.inflate(context,R.layout.face,null);
            viewHolder.faceIV = (ImageView) convertView
                    .findViewById(R.id.face_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position == faceList.size()) {
            viewHolder.faceIV.setImageResource(R.drawable.emotion_del_selector);
            viewHolder.faceIV.setBackgroundDrawable(null);
        } else {
            viewHolder.faceIV.setImageResource(faceList.get(position));
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView faceIV;
    }
}
