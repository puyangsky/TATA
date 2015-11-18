package com.avoscloud.chat.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.R;
import com.avoscloud.chat.App;
import com.avoscloud.chat.service.CacheService;
import com.avoscloud.chat.service.PreferenceMap;
import com.avoscloud.chat.activity.ContactPersonInfoActivity;
import com.avoscloud.chat.adapter.DiscoverFragmentUserAdapter;
import com.avoscloud.chat.view.BaseListView;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.leanchatlib.model.LeanchatUser;
import com.avoscloud.leanchatlib.utils.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-9-17.
 */
public class DiscoverFragment extends BaseFragment {

  private final SortDialogListener distanceListener = new SortDialogListener(Constants.ORDER_DISTANCE);
  private final SortDialogListener updatedAtListener = new SortDialogListener(Constants.ORDER_UPDATED_AT);
  @InjectView(R.id.list_near)
  BaseListView<LeanchatUser> listView;
  DiscoverFragmentUserAdapter adapter;
  List<LeanchatUser> nears = new ArrayList<LeanchatUser>();
  int orderType;
  PreferenceMap preferenceMap;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.discover_fragment, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    preferenceMap = PreferenceMap.getCurUserPrefDao(getActivity());
    orderType = preferenceMap.getNearbyOrder();
    headerLayout.showTitle(R.string.discover_title);
    headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.discover_fragment_sort).setPositiveButton(R.string.discover_fragment_loginTime,
            updatedAtListener).setNegativeButton(R.string.discover_fragment_distance, distanceListener).show();
      }
    });
    initXListView();
    listView.onRefresh();
  }

  private void initXListView() {
    adapter = new DiscoverFragmentUserAdapter(ctx, nears);
    listView = (BaseListView<LeanchatUser>) getView().findViewById(R.id.list_near);
    listView.init(new BaseListView.DataFactory<LeanchatUser>() {
      @Override
      public List<LeanchatUser> getDatasInBackground(int skip, int limit, List<LeanchatUser> currentDatas) throws Exception {
        return findNearbyPeople(orderType, skip, limit);
      }
    }, adapter);

    listView.setItemListener(new BaseListView.ItemListener<LeanchatUser>() {
      @Override
      public void onItemSelected(LeanchatUser item) {
        ContactPersonInfoActivity.goPersonInfo(ctx, item.getObjectId());
      }
    });

    PauseOnScrollListener listener = new PauseOnScrollListener(ImageLoader.getInstance(),
        true, true);
    listView.setOnScrollListener(listener);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    preferenceMap.setNearbyOrder(orderType);
  }

  public List<LeanchatUser> findNearbyPeople(int orderType, int skip, int limit) throws AVException {
    PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(App.ctx);
    AVGeoPoint geoPoint = preferenceMap.getLocation();
    if (geoPoint == null) {
      Logger.i("geo point is null");
      return new ArrayList<>();
    }
    AVQuery<LeanchatUser> q = LeanchatUser.getQuery(LeanchatUser.class);
    AVUser user = AVUser.getCurrentUser();
    q.whereNotEqualTo(Constants.OBJECT_ID, user.getObjectId());
    if (orderType == Constants.ORDER_DISTANCE) {
      q.whereNear(LeanchatUser.LOCATION, geoPoint);
    } else {
      q.orderByDescending(Constants.UPDATED_AT);
    }
    q.skip(skip);
    q.limit(limit);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    List<LeanchatUser> users = q.find();
    CacheService.registerUsers(users);
    return users;
  }

  public class SortDialogListener implements DialogInterface.OnClickListener {
    int orderType;

    public SortDialogListener(int orderType) {
      this.orderType = orderType;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
      DiscoverFragment.this.orderType = orderType;
      listView.onRefresh();
    }
  }
}
