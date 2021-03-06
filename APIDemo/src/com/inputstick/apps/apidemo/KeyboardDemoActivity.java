package com.inputstick.apps.apidemo;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.inputstick.api.ConnectionManager;
import com.inputstick.api.InputStickDataListener;
import com.inputstick.api.InputStickKeyboardListener;
import com.inputstick.api.InputStickStateListener;
import com.inputstick.api.basic.InputStickHID;
import com.inputstick.api.basic.InputStickKeyboard;
import com.inputstick.api.hid.HIDKeycodes;
import com.inputstick.api.layout.KeyboardLayout;

public class KeyboardDemoActivity extends Activity implements InputStickStateListener, InputStickKeyboardListener {
	
	private EditText editText;
	private Spinner spinnerLayout;
	private Spinner spinnerSpeed;
	
	private Button buttonTypeASCII;
	private Button buttonTypeLayout;
	private Button buttonPressEnter;
	private Button buttonPressTab;
	private Button buttonPressEsc;
	private Button buttonCtrlAltDel;
	private Button buttonPressA;
	private Button buttonReleaseAll;
	private Button buttonNumLock;
	private Button buttonCapsLock;
	private Button buttonScrollLock;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard_demo);
		
		editText = (EditText)findViewById(R.id.editText);
				
		spinnerLayout = (Spinner)findViewById(R.id.spinnerLayout);
		//add all currently available keyboard layouts, include native names (example: German -> Deutsch)
		CharSequence[] layoutNames = KeyboardLayout.getLayoutNames(true);
		ArrayAdapter<CharSequence> adapterLayout = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, layoutNames);
		spinnerLayout.setAdapter(adapterLayout);		
		//select English (US) as an initial value		
		List<CharSequence> list = Arrays.asList(KeyboardLayout.getLayoutCodes()); 
		//note: items returned by getLayoutCodes() always match respective values returned by getLayoutNames, example: "German (DE)" and "de-DE" will have the same index
		spinnerLayout.setSelection(list.indexOf("en-US"));		
		
		
		spinnerSpeed = (Spinner)findViewById(R.id.spinnerSpeed);
		//add typing speed options
		String[] speedOptions = {"Fastest", "Normal", "50%", "33%", "25", "20%", "17%", "13%", "11%", "10%"};
		ArrayAdapter<String> adapterSpeed = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, speedOptions);
		spinnerSpeed.setAdapter(adapterSpeed);		
		spinnerSpeed.setSelection(1);		
		
		/* Note: since buttons are disabled when InputStick is not ready,
		 * connection state is not checked again, after user clicks a button
		 */
		
		buttonTypeASCII = (Button)findViewById(R.id.buttonTypeASCII);
		buttonTypeASCII.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//type text assuming that USB host uses en-US keyboard layout:
				InputStickKeyboard.typeASCII(editText.getText().toString());		
			}
		});
		buttonTypeLayout = (Button)findViewById(R.id.buttonTypeLayout);
		buttonTypeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//get layout code, (examples: "de-DE", "en-US", "pl-PL") which matches language selected using spinner
				//full list: http://inputstick.com/index.php/developers/keyboard-layouts								
				CharSequence[] layoutCodes = KeyboardLayout.getLayoutCodes();
				String layoutCode = layoutCodes[spinnerLayout.getSelectedItemPosition()].toString();   
				
				//get typing speed
				//0 - fastest possible typing. Warning: may not work on some configurations! be sure to test it first
				//1 - normal typing speed, default value
				//n - for values greater than one typing speed = 100%/n
				//be careful, for high values you may get duplicated keys (depends on key repeat rate set in OS)
				int typingSpeed = spinnerSpeed.getSelectedItemPosition();
				
				
				/* it is recommended to always use this way of typing text
				 * keyboard layout should always match the one used by USB host
				 * otherwise invalid characters will appear, example:
				 * USB host uses German keyboard layout,
				 * typeASCII("a[abc123XYZ]"); is called
				 * instead of expected result: a[abc123XYZ]
				 * appears: a�ABC123XZY+y
				 *  
				 *  since it is not possible to learn what layout is used by USB host
				 *  using USB interface, such information must be provided by user
				 */
				
				/*
				//previous API version:
				KeyboardLayout layout;
				layout = KeyboardLayout.getLayout(layoutName); //example: "de-DE"	
				//now all you can use all characters available for de-DE (German) layout will be accepted, example:
				//layout.type("���");
				layout.type(editText.getText().toString());
				*/
								
				//updated API:
				InputStickKeyboard.type(editText.getText().toString(), layoutCode, typingSpeed);
				//note: '\n' and '\t' characters are supported		
			}
		});
		buttonPressEnter = (Button)findViewById(R.id.buttonPressEnter);
		buttonPressEnter.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//press and release ENTER key
				InputStickKeyboard.pressAndRelease(HIDKeycodes.NONE, HIDKeycodes.KEY_ENTER);				
			}
		});
		buttonPressTab = (Button)findViewById(R.id.buttonPressTab);
		buttonPressTab.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//press and release TAB key
				InputStickKeyboard.pressAndRelease(HIDKeycodes.NONE, HIDKeycodes.KEY_TAB);				
			}
		});
		buttonPressEsc = (Button)findViewById(R.id.buttonPressEsc);
		buttonPressEsc.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//press and release ESC key
				InputStickKeyboard.pressAndRelease(HIDKeycodes.NONE, HIDKeycodes.KEY_ESCAPE);				
			}
		});
		buttonCtrlAltDel = (Button)findViewById(R.id.buttonCtrlAltDel);
		buttonCtrlAltDel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//press and release CTRL + ALT + DELETE key combination
				InputStickKeyboard.pressAndRelease((byte)(HIDKeycodes.CTRL_LEFT | HIDKeycodes.ALT_LEFT), HIDKeycodes.KEY_DELETE);				
			}
		});
		buttonPressA = (Button)findViewById(R.id.buttonPressA);
		buttonPressA.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//press "A" key, in this case key will NOT be released
				InputStickKeyboard.customReport((byte)0, HIDKeycodes.KEY_A, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0);		
			}
		});
		buttonReleaseAll = (Button)findViewById(R.id.buttonReleaseAll);
		buttonReleaseAll.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//release all keys
				InputStickKeyboard.customReport((byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0);	
			}
		});
		buttonNumLock = (Button)findViewById(R.id.buttonNumLock);
		buttonNumLock.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//request USB host to change state of NumLock
				InputStickKeyboard.toggleNumLock();	
			}
		});
		buttonCapsLock = (Button)findViewById(R.id.buttonCapsLock);
		buttonCapsLock.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//request USB host to change state of CapsLock
				InputStickKeyboard.toggleCapsLock();		
			}
		});
		buttonScrollLock = (Button)findViewById(R.id.buttonScrollLock);
		buttonScrollLock.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				//request USB host to change state of ScrollLock
				InputStickKeyboard.toggleScrollLock();			
			}
		});
		
		
	}
	
	@Override
	public void onPause() {	    
		//remove all listeners:
		InputStickHID.removeStateListener(this);		
		InputStickKeyboard.removeKeyboardListener(this);
		super.onPause();  
	}	
	
	@Override
	public void onResume() {
	    super.onResume(); 
	    //callback will occur when connection state changes
		InputStickHID.addStateListener(this);
	    //callback will occur when USB host changes state of NumLock, CapsLock or ScrollLock LEDs 
	    InputStickKeyboard.addKeyboardListener(this);
	    
	    //get current connection state and adjust UI accordingly:
	    manageUI(InputStickHID.getState());	   
	    //get state of keyboard LEDs
	    setLEDs(InputStickKeyboard.isNumLock(), InputStickKeyboard.isCapsLock(), InputStickKeyboard.isScrollLock());
	}	
	
	@Override
	public void onLEDsChanged(boolean numLock, boolean capsLock, boolean scrollLock) {
		//set new state of keyboard LEDs
		setLEDs(numLock, capsLock, scrollLock);
	}
	
	@Override
	public void onStateChanged(int state) {
		manageUI(state);	
	}
	
	private void manageUI(int state) {
		if (state == ConnectionManager.STATE_READY) {
			//if InputStick is ready to accept keyboard reports, enable buttons
			enableUI(true);
		} else {
			//InputStick is not ready, do not allow to send any USB reports
			enableUI(false);
		}
	}

	private void enableUI(boolean enabled) {
		//disabling UI will prevent sending keyboard and mouse actions when not connected or USB host is not ready
		buttonTypeASCII.setEnabled(enabled);
		buttonTypeLayout.setEnabled(enabled);
		buttonPressEnter.setEnabled(enabled);
		buttonPressTab.setEnabled(enabled);
		buttonPressEsc.setEnabled(enabled);
		buttonCtrlAltDel.setEnabled(enabled);
		buttonPressA.setEnabled(enabled);
		buttonReleaseAll.setEnabled(enabled);
		buttonNumLock.setEnabled(enabled);
		buttonCapsLock.setEnabled(enabled);
		buttonScrollLock.setEnabled(enabled);				
	}	
	
	private void setLEDs(boolean numLock, boolean capsLock, boolean scrollLock) {
		if (numLock) {
			buttonNumLock.setTextColor(Color.GREEN);
		} else {
			buttonNumLock.setTextColor(Color.BLACK);
		}
		if (capsLock) {
			buttonCapsLock.setTextColor(Color.GREEN);
		} else {
			buttonCapsLock.setTextColor(Color.BLACK);
		}
		if (scrollLock) {
			buttonScrollLock.setTextColor(Color.GREEN);
		} else {
			buttonScrollLock.setTextColor(Color.BLACK);
		}
	}

	
}
