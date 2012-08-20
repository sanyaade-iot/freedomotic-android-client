package es.gpulido.freedomotic.ui.base;

import android.support.v4.app.Fragment;

public interface IFragmentItemSelectedListener {
	public void onObjectSelected(String objectName,Fragment sender);
	public boolean isDualPanel();
}
