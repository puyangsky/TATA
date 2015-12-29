package com.avoscloud.chat.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.MainActivity;
import com.avoscloud.chat.view.HeaderLayout;
import com.avoscloud.chat.view.MyHeadLayout;

public class BaseFragment extends Fragment {
  protected MyHeadLayout headerLayout;
  protected Context ctx;

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ctx = getActivity();
    headerLayout = (MyHeadLayout) getView().findViewById(R.id.headerLayout);
	  headerLayout.showLeftBackButton(new View.OnClickListener() {
		  @Override
		  public void onClick(View v) {
			  MainActivity.openDrawer();
		  }
	  });
  }

  protected void toast(String str) {
    Toast.makeText(this.getActivity(), str, Toast.LENGTH_SHORT).show();
  }

  protected void toast(int id) {
    Toast.makeText(this.getActivity(), id, Toast.LENGTH_SHORT).show();
  }

  protected boolean filterException(Exception e) {
    if (e != null) {
      toast(e.getMessage());
      return false;
    } else {
      return true;
    }
  }

  protected ProgressDialog showSpinnerDialog() {
    //activity = modifyDialogContext(activity);
    ProgressDialog dialog = new ProgressDialog(getActivity());
    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    dialog.setCancelable(true);
    dialog.setMessage(getString(R.string.chat_utils_hardLoading));
    if (!getActivity().isFinishing()) {
      dialog.show();
    }
    return dialog;
  }
}
