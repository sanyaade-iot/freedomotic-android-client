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

import net.ser1.stomp.Client;
import net.ser1.stomp.Listener;
import es.gpulido.freedomotic.ui.preferences.Preferences;

//class to manage the onreal time interaction between Freedomotic and the client
public class FreedomoticController {

	private static FreedomoticController INSTANCE = null; // Singleton reference
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
		if (INSTANCE == null)
			createInstance();
		return INSTANCE;
	}

	public int initStompClient() {
		try {
			// stompClient= new Client("192.168.1.20", 61666, "", "" );
			stompClient = new Client(Preferences.getBrokerIP(),
					Preferences.getBrokerPort(), "", "");
			// Init the listener
			Map header = new HashMap();
			header.put("transformation", "jms-object-xml");
			String queue = "/topic/VirtualTopic.app.event.sensor.object.behavior.change";
			stompClient.subscribe(queue, myStompListener, header);
			stompClient.addErrorListener(myStompErrorListener);

		} catch (LoginException e) {
			System.out.println("Stomp error Login: " + e.getMessage());
			return EnvironmentController.STOMP_LOGIN_ERROR;
		} catch (Exception e) {
			System.out.println("Stomp exception: " + e.getMessage());
			return EnvironmentController.STOMP_CONNECT_FAILED_ERROR;
		}
		return EnvironmentController.CONNECTED;

	}

	public static void changeBehavior(String object, String behavior,
			String value) {
		String queue = "/queue/app.events.sensors.behavior.request.objects";
		String command = "<it.freedomotic.reactions.Command>"
				+ "   <name>StompClientCommand</name>"
				+ "   <delay>0</delay>"
				+ "   <timeout>2000</timeout>"
				+ "   <hardwareLevel>false</hardwareLevel>"
				+ "   <description>test</description>"
				+ "   <receiver>app.events.sensors.behavior.request.objects</receiver>"
				+ "	<properties>" + "	    <properties>"
				+ "      		<property name=\"object\" value=\"" + object
				+ "\"/>" + "	        <property name=\"behavior\" value=\""
				+ behavior + "\"/>"
				+ "      		<property name=\"value\" value=\"" + value + "\"/>"
				+ "	    </properties>" + "	</properties>"
				+ "</it.freedomotic.reactions.Command>";
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("transformation", "jms-object-xml");
		if (stompClient != null)
			stompClient.send(queue, command, header);
		// TODO: what if the client is null!!

	}

	private Listener myStompErrorListener = new Listener() {
		public void message(Map header, String message) {
			System.out.println(message);
		}

	};

	private Listener myStompListener = new Listener(){
		
		public void message(Map header, String message) {
			Payload payload = FreedomoticStompHelper.parseMessage(message);
			EnvironmentController.getInstance().updateObject(payload);
		}		
	};


}
//	private Listener myStompListener = new Listener() {
//		
//		public void message(Map header, String message) {
//			Payload payload = FreedomoticStompHelper.parseMessage(message);
//			EnvironmentController.getInstance().updateObject(payload);
//			// DELETE:
//			// EnvObject obj =
//			// EnvironmentController.getInstance().getObject(payload.getStatements("object.name").get(0).getValue());
//			//
//			// Iterator it = payload.iterator();
//			// while (it.hasNext()) {
//			// Statement st = (Statement) it.next();
//			// if
//			// (st.getAttribute().equalsIgnoreCase("object.currentRepresentation"))
//			// {
//			// if (obj.getCurrentRepresentationIndex() !=
//			// Integer.parseInt(st.getValue()))
//			// {
//			// obj.setCurrentRepresentation(Integer.parseInt(st.getValue()));
//			// setChanged();
//			// }
//			// }
//			// else if (!st.getAttribute().equalsIgnoreCase("object.name"))
//			// {
//			// Behavior bh = obj.getBehavior(st.getAttribute());
//			// if (bh instanceof BooleanBehavior)
//			// {
//			// boolean bl = Boolean.parseBoolean(st.getValue());
//			// if (bl !=((BooleanBehavior)bh).getValue())
//			// {
//			// ((BooleanBehavior) bh).setValue(bl);
//			// setChanged();
//			// }
//			//
//			// }
//			// else if (bh instanceof RangedIntBehavior)
//			// {
//			// int val = Integer.parseInt(st.getValue());
//			// if (val !=((RangedIntBehavior)bh).getValue())
//			// {
//			// ((RangedIntBehavior) bh).setValue(val);
//			// setChanged();
//			// }
//			// }
//			// else if (bh instanceof ListBehavior)
//			// {
//			// String val = st.getValue();
//			// if (!val.equals(((ListBehavior)bh).getSelected()))
//			// ((ListBehavior)bh).setSelected(val);
//			// setChanged();
//			// }
//			// }
//			// }
//			// if (hasChanged())
//			// {
//			// notifyObservers(obj);
//			// }
//		}
//	};