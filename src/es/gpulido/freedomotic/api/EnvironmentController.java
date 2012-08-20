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
import it.freedomotic.model.object.Behavior;
import it.freedomotic.model.object.BooleanBehavior;
import it.freedomotic.model.object.EnvObject;
import it.freedomotic.model.object.ListBehavior;
import it.freedomotic.model.object.RangedIntBehavior;
import it.freedomotic.reactions.Payload;
import it.freedomotic.reactions.Statement;
import it.freedomotic.restapi.server.interfaces.EnvironmentResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.restlet.resource.ClientResource;


import es.gpulido.freedomotic.ui.preferences.Preferences;

public class EnvironmentController  extends Observable {

	public static final int REST_ERROR = 1;
	public static final int STOMP_LOGIN_ERROR = 2;
	public static final int STOMP_CONNECT_FAILED_ERROR = 3;
	public static final int CONNECTED= 4;
	
	private static EnvironmentController INSTANCE=null;  //Singleton reference
	private static EnvironmentResource resourceEnvironment;	
	private Environment freedomEnvironment;
	private ArrayList<Zone> rooms;
	//variable to store rooms that doesn't have objects asigned
	private ArrayList<Zone> nonEmptyRooms;
	private HashMap<String,EnvObject> freedomObjectsDictionary;
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
    	//FreedomoticController.getInstance().addObserver(this);   
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
		//TODO: at this moment all data is refreshed.
		// We could just add another method to refresh already loaded data
		freedomEnvironment =resourceEnvironment.retrieveEnvironment();
		if (freedomEnvironment != null)
		{
			rooms= new ArrayList<Zone>();			
			nonEmptyRooms= new ArrayList<Zone>();
			freedomObjectsDictionary = new HashMap<String, EnvObject>();
			for(Zone z: getZones())
			{
				if (z.isRoom())
				{				
					rooms.add(z);
					if (z.getObjects().size()!= 0)
						nonEmptyRooms.add(z);
				}
				
				for (EnvObject obj: z.getObjects())
					freedomObjectsDictionary.put(obj.getName(), obj);
			}
			roomsSize=rooms.size();
			setChanged();
			EnvironmentController.getInstance().notifyObservers();
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
	public ArrayList<Zone> getNonEmptyRooms()
	{					
		return nonEmptyRooms;		
	}
	public Zone getNonEmptyRoom(int roomNumber)
	{					
		return nonEmptyRooms.get(roomNumber);		
	}
	public Zone getRoom(int roomNumber)
	{		
		return rooms.get(roomNumber);		
	}
	
	public EnvObject getObject(String objectName)
	{			
		return freedomObjectsDictionary.get(objectName);
		
	}

	//Updates an existing EnvObject with the data from a message payload 
	public  void updateObject(Payload payload)
	{
		EnvObject obj = EnvironmentController.getInstance().getObject(payload.getStatements("object.name").get(0).getValue());		
		if (obj!= null)
		{
			Iterator it = payload.iterator();
	        while (it.hasNext()) {
	            Statement st = (Statement) it.next();	            
				if (st.getAttribute().equalsIgnoreCase("object.currentRepresentation"))
				{    			
					if (obj.getCurrentRepresentationIndex() !=  Integer.parseInt(st.getValue()))
					{
						obj.setCurrentRepresentation(Integer.parseInt(st.getValue()));
						setChanged();
					}    				    				
				}    			    			    			
				else if (!st.getAttribute().equalsIgnoreCase("object.name"))
				{ 
					Behavior bh = obj.getBehavior(st.getAttribute());							 
					if (bh instanceof BooleanBehavior)
					{
						boolean bl = Boolean.parseBoolean(st.getValue()); 
						if (bl !=((BooleanBehavior)bh).getValue())
						{
							((BooleanBehavior) bh).setValue(bl);
							setChanged();
						}
	
					}
					else if (bh instanceof RangedIntBehavior)
					{
						int val = Integer.parseInt(st.getValue());
						if (val !=((RangedIntBehavior)bh).getValue())
						{
							((RangedIntBehavior) bh).setValue(val);
							setChanged();    						
						}								 								 
					}
					else if (bh instanceof ListBehavior)
					{
						String val = st.getValue();
						if (!val.equals(((ListBehavior)bh).getSelected()))
							((ListBehavior)bh).setSelected(val);
							setChanged();
					}
				}    		
			}
			if (hasChanged())
			{				
				notifyObservers(obj);
			}
		}
	}
			
	
}

