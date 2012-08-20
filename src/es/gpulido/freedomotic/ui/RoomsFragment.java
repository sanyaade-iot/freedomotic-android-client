package es.gpulido.freedomotic.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.viewpagerindicator.TitlePageIndicator;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
//Thanks to http://tamsler.blogspot.com/2011/11/android-viewpager-and-fragments-part-ii.html
public class RoomsFragment extends SherlockFragment implements Observer{
	public static int NUM_ITEMS = 0;	
	RoomsAdapter mAdapter;
	ViewPager mPager;
	static boolean  mInitialized = false;	
	TitlePageIndicator titleIndicator;
	//this is outside the adapter, to prevent the reinitialization of the hashmap.
	//AS the getView is not always called this maintains in sync the data
	protected static SparseArray<ZoneObjectListFragment> mPageReferenceMap= new SparseArray<ZoneObjectListFragment>();
//	
//	@Override
//	public void onAttach(android.app.Activity activity) {
//		System.out.println("GPT Rooms: onAttach");
//		super.onAttach(activity);
//		
//	}; 
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		System.out.println("GPT Rooms: onCreate");
//		super.onCreate(savedInstanceState);
//		
//	};
//	
//	
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		System.out.println("GPT Rooms: onActivityCreated");
//		super.onActivityCreated(savedInstanceState);				
//	};
//	
//	@Override
//	public void onStart() {
//		System.out.println("GPT Rooms: onStart");
//		super.onStart();		
//		
//	};
//	
//	@Override
//	public void onResume() {
//		System.out.println("GPT Rooms: onResume");
//		super.onResume();		
//		
//	};
//	
//	@Override
//	public void onPause() {
//		System.out.println("GPT Rooms: onPause");
//	//	mAdapter.saveState();
//		super.onPause();		
//		
//	};
//	
//	@Override
//	public void onStop() {
//		System.out.println("GPT Rooms: onStop");
//		super.onStop();		
//		
//	};
//	@Override
//	public void onDestroyView() {
//		System.out.println("GPT Rooms: onDestroyView");
//		super.onDestroyView();		
//		
//	};
//	@Override
//	public void onDestroy() {
//		System.out.println("GPT Rooms: onDestroy");
//		super.onDestroy();		
//		
//	};
//	
//	@Override
//	public void onDetach() {
//		System.out.println("GPT Rooms: onDetach");
//		super.onDetach();		
//		
//	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
				
		if (container == null)
			return null;
		View vi = inflater.inflate(R.layout.fragment_roomselecter, container,false);
		
		mPager = (ViewPager) vi.findViewById(R.id.room_panel_pager);
		mAdapter = new RoomsAdapter(getSherlockActivity().getSupportFragmentManager());		
		new setAdapterTask().execute();		
		
		// Bind the ViewPager to ViewPagerTabs
		titleIndicator = (TitlePageIndicator) vi.findViewById(R.id.tabtitles);		
		titleIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageScrollStateChanged(int arg0) {
			
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			
			}

			public void onPageSelected(int currentIndex) {				
				ZoneObjectListFragment zoneFragment = ((RoomsAdapter)mPager.getAdapter()).getFragment(currentIndex);			
				if (zoneFragment != null)
					zoneFragment.selectItem();				
			}
			
			
		});		
		initialize();
		return vi;
	}
	
	@Override
	public void onResume() {
		super.onResume();		
		EnvironmentController.getInstance().addObserver(this);
	};

	@Override
	public void onPause() {
		EnvironmentController.getInstance().deleteObserver(this);
		super.onPause();
	};
	private class setAdapterTask extends AsyncTask<Void,Void,Void>{
	      protected Void doInBackground(Void... params) {
	            return null;
	        }
	        @Override
	        protected void onPostExecute(Void result) {
	        	mPager.setAdapter(mAdapter);
	        	mAdapter.notifyDataSetChanged();
	        	titleIndicator.setViewPager(mPager);
	        }
	}	
	protected void initialize() {		
		if (EnvironmentController.getInstance().getEnvironment() != null)
	    {						
			RoomsFragment.mInitialized = true;			
	    }
	}
	
		
    public static class RoomsAdapter extends FragmentPagerAdapter {
    	
        public RoomsAdapter (FragmentManager fm) {        	
        	super(fm);        	
        }    
		@Override
        public int getCount() {			
			if (RoomsFragment.mInitialized){			
				return EnvironmentController.getInstance().getNonEmptyRooms().size();
			}
			return 0;
        }

        @Override
        public SherlockListFragment getItem(int position) { 
        	//TODO: figure out how to make it circular
        	//TestFragment.newInstance(CONTENT[position % CONTENT.length])
        	
        	ZoneObjectListFragment zoneFragment = ZoneObjectListFragment.newInstance(position);
			mPageReferenceMap.put(Integer.valueOf(position), zoneFragment);			
        	return zoneFragment;        	
        }
        @Override
        public int getItemPosition(Object object) {        	        
        	    return POSITION_NONE;        	        	
        };
        
        @Override
        public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub			
			return EnvironmentController.getInstance().getNonEmptyRoom(position).getName().toUpperCase();
		}
		
		@Override
		public void destroyItem(View container, int position, Object object) {		
			super.destroyItem(container, position, object);			
			mPageReferenceMap.remove(Integer.valueOf(position));
		}
		
		public ZoneObjectListFragment getFragment(int key) {
			
			return mPageReferenceMap.get(key);
		}
    }

	public void update(Observable observable, Object data) { 
		initialize();		
		getActivity().runOnUiThread(new Runnable() {
		    public void run() {
		    	mAdapter.notifyDataSetChanged();
		    }
		});
		
	}
















}
