package com.suda.msgcenter.ui;

import com.suda.msgcenter.R;
import com.suda.msgcenter.util.LogUtil;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BaseFragment extends Fragment {

	private Dialog loginDlg;
	private ProgressBar pb_login;
	private TextView tv_login;
	
	public void showLoginDlg(){
		if(loginDlg == null)
			loginDlg = new Dialog(getActivity(), R.style.MyProgressDialog);
			View view = View.inflate(getActivity(), R.layout.login_loading_dialog, null);
			pb_login = (ProgressBar) view.findViewById(R.id.pb_login);
			tv_login = (TextView) view.findViewById(R.id.tv_login);
			loginDlg.setContentView(view);
			loginDlg.show();
	}
	
	public void dismissLoginDlg() {
		if (loginDlg != null)
			loginDlg.dismiss();
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		LogUtil.v("Fragment onActivityCreated()");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		LogUtil.v("Fragment onAttach()");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LogUtil.v("Fragment onCreate()");
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtil.v("Fragment onCreateView()");
		return null;
	}

	@Override
	public void onDestroy() {
		LogUtil.v("Fragment onDestroy()");
		super.onDestroy();
	}

	@Override
	public void onDestroyView() {
		LogUtil.v("Fragment onDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		LogUtil.v("Fragment onDetach()");
		super.onDetach();
	}

	@Override
	public void onPause() {
		LogUtil.v("Fragment onPause()");
		super.onPause();
	}

	@Override
	public void onResume() {
		LogUtil.v("Fragment onResume()");
		super.onResume();
	}

	@Override
	public void onStart() {
		LogUtil.v("Fragment onStart()");
		super.onStart();
	}

	@Override
	public void onStop() {
		LogUtil.v("Fragment onStop()");
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		LogUtil.v("Fragment onSaveInstanceState()");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		LogUtil.v("Fragment setUserVisibleHint()");
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		LogUtil.v("Fragment onViewStateRestored()");
		super.onViewStateRestored(savedInstanceState);
	}

}
