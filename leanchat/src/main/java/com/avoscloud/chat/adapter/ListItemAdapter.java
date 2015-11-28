package com.avoscloud.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.chat.activity.ImagePagerActivity;
import com.avoscloud.chat.util.ItemEntity;
import com.avoscloud.chat.view.NoScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.avoscloud.chat.R;


import java.util.ArrayList;

/**
 * Created by Puyangsky on 2015/11/22.
 */
public class ListItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ItemEntity> items;

    public ListItemAdapter(Context ctx, ArrayList<ItemEntity> items) {
        this.mContext = ctx;
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
        if (convertView == null) {
            holder = new ViewHolder();
            //填充 convertView
            convertView = View.inflate(mContext, R.layout.moment_item, null);
            holder.iv_avatar = (ImageView) convertView
                    .findViewById(R.id.iv_avatar);
            holder.tv_username = (TextView) convertView
                    .findViewById(R.id.tv_username);
            holder.tv_content = (TextView) convertView
                    .findViewById(R.id.tv_content);
            holder.gridview = (NoScrollGridView) convertView
                    .findViewById(R.id.gridview);
            holder.tv_position = (TextView) convertView
                    .findViewById(R.id.tv_position);
            holder.tv_time = (TextView) convertView
                    .findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ItemEntity itemEntity = items.get(position);
        holder.tv_username.setText(itemEntity.getUsername());
        holder.tv_content.setText(itemEntity.getContent());
        holder.tv_position.setText(itemEntity.getPosition());
        holder.tv_time.setText(itemEntity.getPublishTime());
        DisplayImageOptions options = new DisplayImageOptions.Builder()//
                .showImageOnLoading(R.drawable.ic)
                .showImageOnFail(R.drawable.ic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(itemEntity.getAvatar(),
                holder.iv_avatar, options);
        final ArrayList<String> imageUrls = itemEntity.getImageUrls();
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
                imageBrower(position, imageUrls);
            }
        });
        return convertView;
    }

    /**全屏显示图片的事件，点击跳转到图片查看activity
     * @param :position:点击第几张图片
     * @param :urls2：图片的链接的集合
    **/
    protected void imageBrower(int position, ArrayList<String> urls2) {
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls2);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
        mContext.startActivity(intent);
    }

    class ViewHolder {
        private ImageView iv_avatar;
        private TextView tv_username;
        private TextView tv_content;
        private TextView tv_time;
        private TextView tv_position;
        private NoScrollGridView gridview;
    }
}
