package com.avoscloud.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.ImagePagerActivity;
import com.avoscloud.chat.util.PersonviewEntity;
import com.avoscloud.chat.view.NoScrollGridView;

import java.util.ArrayList;

/**
 * Created by puyangsky on 2015/12/13.
 */
public class PersonviewItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PersonviewEntity> items;

    public PersonviewItemAdapter(Context context, ArrayList<PersonviewEntity> items) {
        this.mContext = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.personview_item, null);
            holder.tv_publish_date = (TextView) convertView
                    .findViewById(R.id.tv_publish_time);
            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_person_content);
            holder.gridview = (NoScrollGridView) convertView
                    .findViewById(R.id.person_gridview);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        PersonviewEntity entity = items.get(position);
        holder.tv_publish_date.setText(entity.getDate());
        holder.tv_content.setText(entity.getContent());

        final ArrayList<String> imageUrls = entity.getImageUrls();
        //如果imageurls为空，不显示gridview
        if (imageUrls == null || imageUrls.size() == 0) {
            holder.gridview.setVisibility(View.GONE);
        } else {
            //给gridview设置NoScrollGridAdapter，赋值
            holder.gridview.setAdapter(new NoScrollGridAdapter(mContext,
                    imageUrls));
        }
        //给gridview设置点击事件，即点击全屏显示图片
        holder.gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                imageBrowser(position, imageUrls);
            }
        });
        return convertView;
    }

    protected void imageBrowser(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }

    class ViewHolder {
        private TextView tv_publish_date;
        private TextView tv_content;
        private NoScrollGridView gridview;
    }
}
