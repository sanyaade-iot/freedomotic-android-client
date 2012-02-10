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



import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;

public class RoomsFragment extends Fragment {
    	
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
         TitlePageIndicator titleIndicator = (TitlePageIndicator)vi.findViewById(R.id.tabtitles);
         titleIndicator.setViewPager(mPager);
                         
    	return vi;
    }
        
    
    public int getCurrentItem()
    {
    	return mPager.getCurrentItem();
    
    }
         
    public static class MyAdapter extends FragmentPagerAdapter implements TitleProvider{
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
    
  
    
	
}
