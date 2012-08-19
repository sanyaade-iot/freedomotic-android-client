package es.gpulido.freedomotic.ui.base;

import es.gpulido.freedomotic.R;

import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public abstract class BaseMultiPanelActivity extends BaseActivity {
	boolean mDualPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			// During initial setup, plug in the details fragment.

		}
		setContentView(R.layout.activity_master_detail);
		View detailsFrame = findViewById(R.id.details);
		mDualPane = detailsFrame != null
				&& detailsFrame.getVisibility() == View.VISIBLE;
	}	
	public void setMasterFragment(Fragment fragment, int proportion,
			boolean addToBackstack) {
		replaceFragment(R.id.masters, fragment, addToBackstack);
		if (mDualPane) {
			FrameLayout test = (FrameLayout) findViewById(R.id.details);
			if (proportion == 0) {
				test.setVisibility(View.GONE);
			} else {
				test.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setDetailsOrMasterFragment(Fragment fragment,
			boolean addToBackstack) {
		if (mDualPane) {
			FrameLayout test = (FrameLayout) findViewById(R.id.details);
			test.setVisibility(View.VISIBLE);
			setDetailsFragment(fragment, addToBackstack);
		} else
			setMasterFragment(fragment, 1, addToBackstack);

	}

	public void showOrHideFragment(Fragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
		if (fragment.isHidden()) {
			ft.show(fragment);
		} else {
			ft.hide(fragment);
		}
		ft.commit();
	}

	public void setDetailsFragment(Fragment fragment, boolean addToBackstack) {
		replaceFragment(R.id.details, fragment, addToBackstack);
	}

	public void refreshMasterFragmentView() {
		IRefreshableFragment frm = (IRefreshableFragment) getSupportFragmentManager()
				.findFragmentById(R.id.masters);
		frm.refresh();
	}

	public boolean isDualPanel() {
		return mDualPane;
	}

}
