package com.avoscloud.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.avoscloud.chat.activity.ImagePagerActivity;
import com.avoscloud.chat.activity.MainActivity;
import com.avoscloud.chat.activity.PersonViewActivity;
import com.avoscloud.chat.fragment.SquareFragment;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.util.ItemEntity;
import com.avoscloud.chat.view.NoScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.avoscloud.chat.R;


import java.util.ArrayList;
import java.util.List;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
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
            holder.iv_zan = (ImageView) convertView
                    .findViewById(R.id.iv_zan);
            holder.iv_comment = (ImageView) convertView
                    .findViewById(R.id.iv_comment);
            holder.lv_commentList = (ListView) convertView
                    .findViewById(R.id.commentList);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ItemEntity itemEntity = items.get(position);
        holder.tv_username.setText(itemEntity.getUsername());
        holder.tv_content.setText(itemEntity.getContent());
        holder.tv_position.setText(itemEntity.getPosition());
        holder.tv_time.setText(itemEntity.getPublishTime());
        if(itemEntity.getZanFlag() < 0)
        {
            holder.iv_zan.setImageResource(R.drawable.zan);
        }else {
            holder.iv_zan.setImageResource(R.drawable.yizan);
        }
        //点赞事件
        holder.iv_zan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemEntity.getZanFlag() < 0){
                    holder.iv_zan.setImageResource(R.drawable.yizan);
                    itemEntity.setZanFlag(itemEntity.getZanFlag() * (-1));
                }else {
                    holder.iv_zan.setImageResource(R.drawable.zan);
                    itemEntity.setZanFlag(itemEntity.getZanFlag() * (-1));
                }
            }
        });
        //点击评论按钮事件
        holder.iv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.showEditText(mContext, position);
            }
        });
        //评论列表

        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic)
                .showImageOnFail(R.drawable.ic)
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
        private ImageView iv_zan;
        private ImageView iv_comment;
        public ListView lv_commentList;
        private NoScrollGridView gridview;
    }
}
