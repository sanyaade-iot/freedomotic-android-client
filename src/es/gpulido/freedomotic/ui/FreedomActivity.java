/*******************************************************************************
 * Copyright (c) 2011 Gabriel Pulido.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Gabriel Pulido - initial API and implementation
 ******************************************************************************/
package es.gpulido.freedomotic.ui;



import com.astuetz.viewpagertabs.ViewPagerTabProvider;
import com.astuetz.viewpagertabs.ViewPagerTabs;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomController;
import es.gpulido.freedomotic.ui.actionbar.ActionBarActivity;
import es.gpulido.freedomotic.ui.preferences.Preferences;

public class FreedomActivity extends ActionBarActivity {
    /** Called when the activity is first created. */
	
	public static int NUM_ITEMS = 1;

    MyAdapter mAdapter;

    ViewPager mPager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);                                         
        NUM_ITEMS = EnvironmentController.getInstance().getRooms().size();
        setContentView(R.layout.viewpagertest);
        
        mAdapter = new MyAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.zonepanelpager);
        mPager.setAdapter(mAdapter);
     
        // Bind the ViewPager to ViewPagerTabs
        ViewPagerTabs tabs = (ViewPagerTabs) findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
//        tabs.setBackgroundColor(0x00FFFFFF);
//        tabs.setBackgroundColorPressed(0x33333333);
//        tabs.setTextColor(0x44A80000);
//        tabs.setTextColorCenter(0xFFA80000);
//        tabs.setLineColorCenter(0xFFA80000);
//        tabs.setLineHeight(5);
//        tabs.setTextSize(22);
//        tabs.setTabPadding(5, 1, 5, 10);
        //setContentView(R.layout.main);
        
    }
		
    public static class MyAdapter extends FragmentPagerAdapter implements ViewPagerTabProvider{
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }
    
		@Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
        	return ZoneObjectListFragment.newInstance(position);
            //return ArrayListFragment.newInstance(position);
        }

		@Override
		public String getTitle(int position) {
			
			return EnvironmentController.getInstance().getRoom(position).getName().toUpperCase();			
		}
    }
    
    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
//            View tv = v.findViewById(R.id.text);            
//            ((TextView)tv).setText("Fragment #" + mNum);
//            return v;
//        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
//            setListAdapter(new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
    
    
	
}
