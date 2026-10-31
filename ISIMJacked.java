package ISIMJacked;

import javacard.framework.*;
import sim.toolkit.*;
import sim.access.*;

public class ISIMJacked extends Applet implements ToolkitInterface, ToolkitConstants {
	
	private byte helloMenuItem;
	private byte menuItemPlayTone;
	private byte menuItemSetupCall;

	static byte[] welcomeMsg = new byte[] { 'I', 'S', 'I', 'M', 'J', 'a', 'c', 'k', 'e', 'd', ' ', 'v', '0', '.', '1', ' ', 'P', 'o', 'w', 'e', 'r', 'e', 'd', ' ', 'b', 'y', ' ', 's', 't', 'u', 't', 'm', ',', ' ', 'S', 'e', 'n', 's', 'e', 'P', 'o', 's', 't' };

	static byte[] msg = new byte[] {'P', 'l', 'a', 'y', ' ', 'm', 'e'};

	static byte[] menuItemText = new byte[] { 'A', 'b', 'o', 'u', 't'};

	static byte[] menuItemText2 = new byte[] { 'P', 'l', 'a', 'y', ' ', 'T', 'o', 'n', 'e' };

	static byte[] menuItemText3 = new byte[] { 'S', 'e', 't', 'u', 'p', ' ', 'a', ' ', 'C', 'a', 'l', 'l'};

	static byte[] ALPHA_MSG = new byte[] { 'C', 'a', 'l', 'l', ' ', 'n', 'o', 'w'};

	static byte[] NUMBER_TO_CALL = new byte[] { '9', '1', '1'};


	private ISIMJacked() {
		ToolkitRegistry reg = ToolkitRegistry.getEntry();
		helloMenuItem = reg.initMenuEntry(menuItemText, (short)0, (short)menuItemText.length, PRO_CMD_SELECT_ITEM, false, (byte)0, (short)0);
		menuItemPlayTone = reg.initMenuEntry(menuItemText2, (short)0, (short)menuItemText2.length, PRO_CMD_SELECT_ITEM, false, (byte)0, (short)0);
		menuItemSetupCall = reg.initMenuEntry(menuItemText3, (short)0, (short)menuItemText3.length, PRO_CMD_SELECT_ITEM, false, (byte)0, (short)0);
		reg.setEvent(EVENT_EVENT_DOWNLOAD_LOCATION_STATUS);
	}

	public static void install(byte[] bArray, short bOffset, byte bLength) {
		ISIMJacked applet = new ISIMJacked();
		applet.register();
	}

	public void process(APDU arg0) throws ISOException {
		if (selectingApplet()){
			return;
		}		
	}

	public void processToolkit(byte event) throws ToolkitException {
		EnvelopeHandler handler = EnvelopeHandler.getTheHandler();
		if (event == EVENT_MENU_SELECTION) {
			byte selectedItemId = handler.getItemIdentifier();
			if (selectedItemId == helloMenuItem) {
				Hello();
			} else if (selectedItemId == menuItemSetupCall) {
				setupCall();
			} else if (selectedItemId == menuItemPlayTone) {
				displayText(msg);
				for(short x=0; x<0x16; x++) { 
					playTone((byte)x, (byte)0x02); 
				}
				
				for(short x=0x30; x<0x48; x++) { 
					playTone((byte)x, (byte)0x02); 
				}
			}
		}
	}

	private void Hello() {
		ProactiveHandler handler = ProactiveHandler.getTheHandler();
		handler.initDisplayText((byte)0, DCS_8_BIT_DATA, welcomeMsg, (short)0, (short)(welcomeMsg.length));
		handler.send();

		return;
	}

	private void displayText(byte[] msg) {
		ProactiveHandler handler = ProactiveHandler.getTheHandler();
		handler.initDisplayText((byte)0, DCS_8_BIT_DATA, msg, (short)0, (short)(msg.length));
		handler.send();
		
		return;
	}

	private void playTone(byte tone, byte seconds) {
		ProactiveHandler handler = ProactiveHandler.getTheHandler();
		handler.init(PRO_CMD_PLAY_TONE, (byte)0x01, DEV_ID_EARPIECE);
		handler.appendTLV(TAG_DURATION, (byte)0x01, seconds);
		handler.appendTLV(TAG_TONE, tone);
		handler.send();

		return;
	}

	private void setupCall(){
		ProactiveHandler handler = ProactiveHandler.getTheHandler();
		handler.init(PRO_CMD_SET_UP_CALL, (byte)0x00, DEV_ID_NETWORK);
		handler.appendTLV(TAG_ALPHA_IDENTIFIER, ALPHA_MSG, (short)0, (short)ALPHA_MSG.length);
		handler.appendTLV(TAG_ADDRESS, NUMBER_TO_CALL, (short)0,(short)NUMBER_TO_CALL.length);
		handler.send();

		return;
	}
}