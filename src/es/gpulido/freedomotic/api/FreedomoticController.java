package es.gpulido.freedomotic.api;

import it.freedomotic.model.object.Behavior;
import it.freedomotic.model.object.BooleanBehavior;
import it.freedomotic.model.object.EnvObject;
import it.freedomotic.model.object.ListBehavior;
import it.freedomotic.model.object.RangedIntBehavior;
import it.freedomotic.reactions.Payload;
import it.freedomotic.reactions.Statement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;

import javax.security.auth.login.LoginException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

import es.gpulido.freedomotic.ui.preferences.Preferences;
import net.ser1.stomp.Client;
import net.ser1.stomp.Listener;

//class to manage the onreal time interaction between Freedomotic and the client
public class FreedomoticController extends Observable{

	private static FreedomoticController INSTANCE=null;  //Singleton reference
	private static Client stompClient;
	 // Private constructor suppresses 
    private FreedomoticController() {    		
    }
 
    // Sync creator to avoid multi-thread problems
    private static void createInstance() {
        if (INSTANCE == null) { 
            INSTANCE = new FreedomoticController();
        }
    }
 
    public static FreedomoticController getInstance() {
        if (INSTANCE == null) createInstance();
        return INSTANCE;
    }
    
	public boolean initStompClient() {
		try {
			// stompClient= new Client("192.168.1.20", 61666, "", "" );
			stompClient = new Client(Preferences.getBrokerIP(),
					Preferences.getBrokerPort(), "", "");
			// Init the listener
			String queue = "/topic/VirtualTopic.app.event.sensor.object.behavior.change";
			stompClient.subscribe(queue, myStompListener);

		} catch (LoginException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;

	}
	
	
    private Listener myStompListener = new Listener()
    {	            
    	public void message(Map header, String message) {    		
    		Payload payload = FreedomoticController.parseMessage(message);
    		EnvObject obj = EnvironmentController.getInstance().getObject(payload.getStatement("object.name").getValue());    				 
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
				notifyObservers();
			}
    	}
    };
    
	public void changeBehavior(String object, String behavior, String value)
	{
      String queue = "/queue/app.events.sensors.behavior.request.objects";
      String command ="<it.freedomotic.reactions.Command>"+
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
              "</it.freedomotic.reactions.Command>";
      	HashMap header = new HashMap();
      	header.put("transformation", "jms-object-xml");  
		if (stompClient!=null)
			stompClient.send(queue, command,header);
		//TODO: what if the client is  null!!
			
		
	}
	
	//Parsing of Messages from stomp
	//TODO: move to a helper class
	private static Statement statement;
	public static Payload parseMessage(String message)
	{
		final Payload payload = new Payload();		
		RootElement root = new RootElement("it.freedomotic.events.ObjectHasChangedBehavior");	        
	    Element payloadroot =  root.getChild("payload");
	    Element pl = payloadroot.getChild("payload");
	    Element entry = pl.getChild("entry");
	    Element st = entry.getChild("it.freedomotic.reactions.Statement");
	   	    
	    
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
	

}
