/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.gpulido.freedomotic.ui;


import es.gpulido.freedomotic.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DashboardFragment extends Fragment {

 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container);

        // Attach event handlers
        root.findViewById(R.id.home_btn_objects).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {               
//                if (UIUtils.isHoneycombTablet(getActivity())) {
//                    startActivity(new Intent(getActivity(), ScheduleMultiPaneActivity.class));
//                } else {
//                    startActivity(new Intent(getActivity(), ScheduleActivity.class));
//                }
            	startActivity(new Intent(getActivity(),ObjectsActivity.class));
                
            }
            
        });
 
        root.findViewById(R.id.home_btn_map).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {//                
//                startActivity(new Intent(getActivity(),
//                        UIUtils.getMapActivityClass(getActivity())));
            	startActivity(new Intent(getActivity(),HousingPlanActivity.class));            	 
            }
        });

        root.findViewById(R.id.home_btn_pageviewer).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {//                
//                startActivity(new Intent(getActivity(),
//                        UIUtils.getMapActivityClass(getActivity())));
            	startActivity(new Intent(getActivity(),FreedomActivity.class));            	 
            }
        });
        

        return root;
    }
}
