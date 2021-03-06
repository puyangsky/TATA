package com.avoscloud.chat.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.ListItemAdapter;
import com.avoscloud.chat.model.Comment;
import com.avoscloud.chat.model.Image;
import com.avoscloud.chat.model.Moment;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.util.GetCity;
import com.avoscloud.chat.util.ItemEntity;
import com.avoscloud.chat.util.MomentCacheUtils;
import com.avoscloud.chat.view.MomentListView;
import com.avoscloud.leanchatlib.model.LeanchatUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * Created by puyangsky on 2015/12/3.
 */
public class SquareFragment extends BaseFragment{
	private static final int COMPLETED = 0;
	public MomentListView mListView;
    private ArrayList<ItemEntity> itemEntities;
    public ArrayList<ArrayList<String>> commentItems;
    private ListItemAdapter adapter;
    public static List<Moment> moments;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.square_fragment, container, false);
    }
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == COMPLETED) {
				initView();
			}
		}
	};

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
	    final ProgressDialog dialog = showSpinnerDialog();
	    new Thread(new Runnable() {
		    @Override
		    public void run() {
			    try {
				    initData();
			    } catch (Exception e) {
				    Log.d("pyt", "initData : " + e.getMessage());
			    }
			    Message message = new Message();
			    message.what = COMPLETED;
			    handler.sendMessage(message);
			    dialog.dismiss();
		    }
	    }).start();

        headerLayout.showTitle(R.string.square_title);
    }

    public void initView() {
        mListView = (MomentListView) getView().findViewById(R.id.square_list_item);

        adapter = new ListItemAdapter(getActivity(), itemEntities, commentItems);
//	    Log.d("pyt", "INIT VIEW: itemEntities size = " + itemEntities.size());
        mListView.initMomentListView(adapter);
    }

    public void initData() throws Exception {

	    //若缓存中为空，则需要初始化。导致第一次加载会很慢。
		if (MomentCacheUtils.getCachedMoments() == null) {
			//获取所有的moments并存入缓存中
			getMomentsFromServer();
			Log.d("pyt", "Cache is NULL!");
		}else {
			//从缓存中取
			try {
				Log.d("pyt", "Hit Moment Cache!");
				initItem(MomentCacheUtils.getCachedMoments(), 20);
			} catch (Exception e) {
				Log.d("pyt", e.getMessage());
			}
		}
    }
	public void getMomentsFromServer() throws Exception{
		moments = new ArrayList<>();
		AVQuery<Moment> query = AVObject.getQuery(Moment.class);
		query.orderByDescending("createdAt");
		query.include("user");
		query.include("content");
		query.include("createdAt");
		query.include("position");
		query.include("fileList");          //这里include一个类的数据，会自动填充
		query.include("zan");
		List<LeanchatUser> friends = CacheService.getFriends();
		query.whereContainedIn("user", friends);
		query.findInBackground(new FindCallback<Moment>() {
			@Override
			public void done(List<Moment> results, AVException e) {
				if (e != null || results == null) {
					return;
				}
				moments = results;
				initItem(results, 20);
				Log.d("pyt", "找到了" + moments.size() + "条记录");
			}
		});
	}

	public void initItem(List<Moment> results, int size) {
		itemEntities = new ArrayList<>();
		commentItems = new ArrayList<>();
		int i = 0;
		for (Moment moment : results) {
			i++;
			Log.d("pys", "cache moment ...");
			MomentCacheUtils.cacheMoment(moment.getObjectId(), moment);
			List<Image> imageList = moment.getFileList();
			ArrayList<String> imageUrls = new ArrayList<String>();
			if (imageList == null) {
				imageUrls = null;
			} else {
				for (Image image : imageList) {
					imageUrls.add(image.getFile().getUrl());
//					Log.d("pya", "itemEntitiesUrl" + image.getFile().getUrl());
				}
			}
			try {
				String city = "";
				final double lat = moment.getPosition().getLatitude();
				final double log = moment.getPosition().getLongitude();
				try {
					FutureTask<String> task = new FutureTask<String>(
							new Callable<String>() {
								@Override
								public String call() throws Exception {
									return GetCity.getCity(lat, log);
								}
							}
					);
					new Thread(task).start();
					city = task.get();
				} catch (Exception e1) {
					Log.d("pyt", "ERROR: 城市：" + e1.getMessage());
				}
				ItemEntity entity = new ItemEntity(
						moment.getUser().getAvatarUrl(),
						moment.getUser().getUsername(),
						moment.getContent(),
						imageUrls,
						city,
						new SimpleDateFormat("MM-dd HH:mm").format(moment.getCreatedAt()),
						-1
				);
				itemEntities.add(entity);
			} catch (Exception e2) {
				Log.d("pyt", "失败:" + e2.getMessage());
			}
			//查找每条moment对应的评论集合
			final ArrayList<String> commentItem = new ArrayList<>();
			AVQuery<Comment> commentQuery = AVObject.getQuery(Comment.class);
			commentQuery.orderByDescending("createdAt");
			commentQuery.include("content");
			commentQuery.include("moment");
			commentQuery.include("createdAt");
			commentQuery.include("user");
			commentQuery.whereEqualTo("moment", moment);
			commentQuery.findInBackground(new FindCallback<Comment>() {
				@Override
				public void done(List<Comment> commentList, AVException e) {
					if (e == null) {
						for (Comment comment : commentList) {
							commentItem.add(comment.getContent());
//							Log.d("pyt", "评论内容：" + comment.getContent());
						}
					} else {
						Log.d("pyt", e.getMessage());
					}
				}
			});
			commentItems.add(commentItem);
			if (i >= size) {
				break;
			}
		}
		Log.d("pyt", "itemEntities size = " + itemEntities.size());
		Log.d("pyt", "MAP SIZE = " + MomentCacheUtils.momentMap.size());
//		adapter.notifyDataSetChanged();
	}


    @Override
    public void onResume() {
        super.onResume();
//        initData();
    }

}
