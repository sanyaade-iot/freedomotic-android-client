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
package es.gpulido.freedomotic.api;

import it.freedomotic.model.environment.Environment;
import it.freedomotic.model.environment.Zone;
import it.freedomotic.model.object.EnvObject;
import it.freedomotic.restapi.server.interfaces.EnvironmentResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import org.restlet.resource.ClientResource;

import es.gpulido.freedomotic.ui.preferences.Preferences;

public class EnvironmentController  extends Observable  {

	public static final int REST_ERROR = 1;
	public static final int CONNECTED= 2;
	
	private static EnvironmentController INSTANCE=null;  //Singleton reference
	private static EnvironmentResource resourceEnvironment;	
	private Environment freedomEnvironment;
	private ArrayList<Zone> rooms;
	private int roomsSize=0;
		
	 // Private constructor suppresses 
    private EnvironmentController() {    		
    }
 
    // Sync creator to avoid multi-thread problems
    private static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new EnvironmentController();
        }
    }
 
    public static EnvironmentController getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }
    
    public int init()
    { 
    	if(!prepareRestResource())
    		return REST_ERROR;
    	return CONNECTED;
    }
         
    public static boolean prepareRestResource()
    {
        ClientResource cr = new ClientResource(Preferences.getServerString()+"/v1/environment/");
        resourceEnvironment = cr.wrap(EnvironmentResource.class);
        //TODO: Find how to check the configuration
        return true;
    }
     	
	public void retrieve() throws Exception
	{		
		freedomEnvironment =resourceEnvironment.retrieveEnvironment();
		if (freedomEnvironment != null)
		{
			rooms= new ArrayList<Zone>();							
			for(Zone z: getZones())
			{
				if (z.isRoom())
					{rooms.add(z);
				System.out.println("room: "+z.getName()+ "number of rooms: " +rooms.size());}
			}
			roomsSize=rooms.size();			
		}						
		
	}
	
	public Environment getEnvironment()
	{		
		return freedomEnvironment;		
	}	
	
	public ArrayList<Zone> getZones()
	{		
		return freedomEnvironment.getZones();		
	}	
	public ArrayList<Zone> getRooms()
	{					
		return rooms;		
	}
	
	public Zone getRoom(int roomNumber)
	{		
		return rooms.get(roomNumber);		
	}
}

