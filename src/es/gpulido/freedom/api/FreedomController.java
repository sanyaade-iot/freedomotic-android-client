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
package es.gpulido.freedom.api;


import it.freedom.model.object.Behavior;
import it.freedom.model.object.BooleanBehavior;
import it.freedom.model.object.EnvObject;
import it.freedom.model.object.ListBehavior;
import it.freedom.model.object.RangedIntBehavior;
import it.freedom.model.object.Representation;
import it.freedom.reactions.Payload;
import it.freedom.reactions.Statement;
import it.freedom.restapi.server.interfaces.ObjectResource;
import it.freedom.restapi.server.interfaces.ObjectsResource;

import java.io.IOException;

import javax.security.auth.login.LoginException;

import org.restlet.resource.ClientResource;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import es.gpulido.freedom.ui.preferences.Preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;


import net.ser1.stomp.Client;
import net.ser1.stomp.Listener;

public class FreedomController extends Observable  {

	public static final int STOMP_ERROR = 0;
	public static final int REST_ERROR = 1;
	public static final int CONNECTED= 2;
	
	private static FreedomController INSTANCE=null;  //Singleton reference
	private Client stompClient;
	private static ObjectsResource resourceObjects;		
	private ArrayList<EnvObject> freedomObjects = new ArrayList<EnvObject>();
	private HashMap<String,EnvObject> freedomObjectsDictionary;
	
	
	 // Private constructor suppresses 
    private FreedomController() {    		
    }
 
    // Sync creator to avoid multi-thread problems
    private static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new FreedomController();
        }
    }
 
    public static FreedomController getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }
    
    public int init()
    {
    	if (!initStompClient())
    		return STOMP_ERROR;
    	if(!prepareRestResource())
    		return REST_ERROR;
    	return CONNECTED;
    }

    private Listener myStompListener = new Listener()
    {	            
    	public void message(Map header, String message) {
    		
    		Payload payload = FreedomController.parseMessage(message);
    		EnvObject obj = getObject(payload.findAttribute("object.name").getValue());    		
    		    		
    		for (Statement st: payload.getStatements())
    		{
    			if (st.getAttribute().equalsIgnoreCase("object.currentRepresentation"))
    			{
    				System.out.println("current: "+obj.getCurrentRepresentationIndex() + " changed: "+Integer.parseInt(st.getValue()));
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
				FreedomController.getInstance().notifyObservers();
			}
    	}
    };
        
    public boolean initStompClient()
    {
    	try {
			//stompClient= new Client("192.168.1.20", 61666, "", "" );
			stompClient= new Client(Preferences.getBrokerIP(), Preferences.getBrokerPort(), "", "" );
			//Init the listener
			String queue="/topic/VirtualTopic.app.event.sensor.object.behavior.change";			
			stompClient.subscribe( queue, myStompListener);
    	    	
    	} catch (LoginException e) { 			
			return false;
		} catch (Exception e) {
			return false;
		}
    	return true;
    	
    }
    //TODO: create a generic client to retrieve all resources. See restlet project.
    public static boolean prepareRestResource()
    {
        ClientResource cr = new ClientResource(Preferences.getEnvironmentURL()+"objects/");
        resourceObjects = cr.wrap(ObjectsResource.class);
        //TODO: Find how to check the configuration
        return true; 
    }
    
    //TODO: Make this async?
    public static EnvObject getUpdatedObjectFromFreedom(EnvObject obj)
    {
    	 ClientResource cr = new ClientResource(Preferences.getEnvironmentURL()+"objects/"+obj.getName());
    	 ObjectResource resourceObject = cr.wrap(ObjectResource.class);
    	 EnvObject envObj = resourceObject.retrieveObject();
    	 return envObj;
    }
    
    
	public void changeBehavior(String object, String behavior, String value)
	{
      String queue = "/queue/app.events.sensors.behavior.request.objects";
      String command ="<it.freedom.reactions.Command>"+
              "   <name>StompClientCommand</name>"+
              "   <delay>0</delay>"+
              "   <timeout>2000</timeout>"+
              "   <hardwareLevel>false</hardwareLevel>"+
              "   <description>test</description>"+
              "   <receiver>app.events.sensors.behavior.request.objects</receiver>"+
              "	<properties>" +
              "	    <properties>" +
              "      		<property name=\"object\" value=\""+object+"\"/>" +
              "	        <property name=\"behavior\" value=\""+behavior+"\"/>" +
              "      		<property name=\"value\" value=\""+value+"\"/>" +
              "	    </properties>" +
              "	</properties>" +                  
              "</it.freedom.reactions.Command>";
      	HashMap header = new HashMap();
      	header.put("transformation", "jms-object-xml");  
		stompClient.send(queue, command,header);
					
	}
	
	public void retrieve() throws Exception
	{		
		ArrayList<EnvObject> temp =(ArrayList<EnvObject>)resourceObjects.retrieveObjects();			
		if (freedomObjects != null)
			freedomObjects = temp; 
			
		freedomObjectsDictionary = new HashMap<String, EnvObject>();			
		for(EnvObject obj: freedomObjects)
		{
			freedomObjectsDictionary.put(obj.getName(), obj);
		}		
	}
	public ArrayList<EnvObject> getObjects() {
		return freedomObjects;		
	}
	
	public EnvObject getObject(int objectNumber)
	{		
		return freedomObjects.get(objectNumber);		
	}
	
	public EnvObject getObject(String objectName)
	{
		
		return freedomObjectsDictionary.get(objectName);
		
	}
	
	public int getObjectNumber(EnvObject obj)
	{
		return freedomObjects.indexOf(obj);
		
	}
	
	//Parsing of Messages from stomp
	//TODO: move to a helper class
	private static Statement statement;
	public static Payload parseMessage(String message)
	{
		final Payload payload = new Payload();
		//final Statement statement = new Statement();
		RootElement root = new RootElement("it.freedom.events.ObjectHasChangedBehavior");	        
	    Element payloadroot =  root.getChild("payload");
	    Element pl = payloadroot.getChild("payload");
	    Element st = pl.getChild("it.freedom.reactions.Statement");
	   	    
	    
	    st.setStartElementListener(new StartElementListener(){
            public void start(Attributes attrs) {
            	statement = new Statement();
            }
        });
	   		   
		st.setEndElementListener(new EndElementListener(){
            public void end() {
            	payload.enqueueStatement(statement);
            }
        });
 
        st.getChild("logical").setEndTextElementListener(new EndTextElementListener(){
                public void end(String body) {
                    statement.setLogical(body);                	
                }
        });
        
        st.getChild("attribute").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                statement.setAttribute(body);                	
            }
        });
        
        st.getChild("operand").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                statement.setOperand(body);                	
            }
        });
        st.getChild("value").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                statement.setValue(body);                	
            }
        });
  
        try {
			Xml.parse(message,root.getContentHandler());
		} catch (SAXException e) {			
			e.printStackTrace();
		}
        return payload;                      	 	
	}

	public int getObjectsNumber() {		
		return freedomObjects.size();
	}
	

	
	
}
