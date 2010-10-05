/*
 *    This file is part of urcRemote. urcRemote turns your mobile phone into
 *    a touch-pad and keyboard for your computer.
 *
 *    Copyright  2010 Carl Lobo <carllobo@gmail.com>
 *
 *    urcRemote is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    urcRemote is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with urcRemote.  If not, see <http://www.gnu.org/licenses/>.
 */
 
package carl.urc.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import carl.urc.android.R;
import carl.urc.android.UrcAndroidApp;
import carl.urc.common.CommonPreferences;
import carl.urc.common.client.ClientPreferences;
import carl.urc.common.network.bluetooth.BluetoothCommonPreferences;

public class WelcomeActivity extends Activity {
	
    private UrcAndroidApp application;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.application = (UrcAndroidApp) getApplication();
        addEventListeners();
        
        setDefaultProperties();
    }
    
    private void addEventListeners() {
    	((Button) findViewById(R.id.connectButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				doConnection();
			}
		});
    	
    	((RadioGroup) findViewById(R.id.RadioGroup01)).setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				setProperties();
			}
		});
    }
    
    private void setDefaultProperties() {
    	((RadioGroup) findViewById(R.id.RadioGroup01)).check(R.id.btspp);
    	setProperties();
    }
    
    private void setProperties() {
    	int sel = ((RadioGroup) findViewById(R.id.RadioGroup01)).getCheckedRadioButtonId();
    	EditText addr = (EditText) findViewById(R.id.addrText);
    	EditText chan = (EditText) findViewById(R.id.channelText);
    	switch(sel) {
    	case R.id.btspp:
    		addr.setText(androidBtAddr(ClientPreferences.preferenceDefaultDevice));
    		chan.setText(BluetoothCommonPreferences.preferenceDefaultChannel);
    		break;
    	case R.id.socket:
    		addr.setText(ClientPreferences.preferenceDefaultIp);
    		chan.setText(CommonPreferences.preferenceDefaultPort);
    		break;
    		
    	}
    }
    
    private String androidBtAddr(String javaBtAddr) {
    	StringBuffer s = new StringBuffer(javaBtAddr);
    	for(int i = javaBtAddr.length() - 2; i > 0; i-=2) s.insert(i, ':');
    	return s.toString().toUpperCase();
    }
    
    private void doConnection() {
    	int selectedProtocol = ((RadioGroup) findViewById(R.id.RadioGroup01)).getCheckedRadioButtonId();
    	String addr = ((EditText) findViewById(R.id.addrText)).getText().toString();
    	String channel = ((EditText) findViewById(R.id.channelText)).getText().toString();
    	application.doConnection(addr, channel, selectedProtocol);
    }
}