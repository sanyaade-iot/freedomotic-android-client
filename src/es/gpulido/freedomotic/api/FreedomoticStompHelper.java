package es.gpulido.freedomotic.api;

import it.freedomotic.reactions.Payload;
import it.freedomotic.reactions.Statement;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.util.Xml;

public class FreedomoticStompHelper {
	//Parsing of Messages from stomp	
	private static Statement statement;
	public static Payload parseMessage(String message)
	{
		final Payload payload = new Payload();		
		RootElement root = new RootElement("it.freedomotic.events.ObjectHasChangedBehavior");	        
	    Element payloadroot =  root.getChild("payload");
	    Element pl = payloadroot.getChild("payload");
	    //Element entry = pl.getChild("entry");
	    Element st = pl.getChild("it.freedomotic.reactions.Statement");
	   	    
	    
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
