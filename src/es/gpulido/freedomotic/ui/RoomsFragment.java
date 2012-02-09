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



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.astuetz.viewpagertabs.ViewPagerTabProvider;
import com.astuetz.viewpagertabs.ViewPagerTabs;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;

public class RoomsFragment extends Fragment {
    /** Called when the activity is first created. */
	
	public static int NUM_ITEMS = 1;

    MyAdapter mAdapter;

    ViewPager mPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	
    	 NUM_ITEMS = EnvironmentController.getInstance().getRooms().size();    	 
         View vi= inflater.inflate(R.layout.viewpagertest, container, false);
         
         mAdapter = new MyAdapter(getActivity().getSupportFragmentManager());
         mPager = (ViewPager)vi.findViewById(R.id.zonepanelpager);
         mPager.setAdapter(mAdapter);
      
         // Bind the ViewPager to ViewPagerTabs
         ViewPagerTabs tabs = (ViewPagerTabs)vi.findViewById(R.id.tabs);
         tabs.setViewPager(mPager);
         tabs.setViewPager(mPager);
////     tabs.setBackgroundColor(0x00FFFFFF);
////     tabs.setBackgroundColorPressed(0x33333333);
////     tabs.setTextColor(0x44A80000);
////     tabs.setTextColorCenter(0xFFA80000);
////     tabs.setLineColorCenter(0xFFA80000);
////     tabs.setLineHeight(5);
////     tabs.setTextSize(22);
////     tabs.setTabPadding(5, 1, 5, 10);
    	
    	
    	return vi;
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


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
        }
    }
    
    
	
}
