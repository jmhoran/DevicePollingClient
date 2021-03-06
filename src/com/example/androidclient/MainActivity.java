package com.example.androidclient;



import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText editTextRegistrationServerIPAddress;
	EditText editTextRegistrationServerPort;
	Context mContext;
	MonitorWorker mMonitorWorker;
	TextView textViewDeviceInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		editTextRegistrationServerIPAddress = (EditText)findViewById(R.id.editTextRegistrationServerIPAddress);
		//editTextRegistrationServerIPAddress.setText("10.10.70.");
		
		
		editTextRegistrationServerPort = (EditText)findViewById(R.id.editTextRegistrationServerPort);
		mContext = this;
		
		textViewDeviceInfo = (TextView)findViewById(R.id.textViewDeviceInfo);
		
		mMonitorWorker = new MonitorWorker(this);
		mMonitorWorker.start();
		
	}
	
	
	
	public void registerDeviceWithServer(View v){
		


		
		final String deviceipAddress = Net.getIPAddress(true);
		
		//final Button b = (Button)v;
		//b.setText(deviceipAddress);
		
		final String serverIpAddress = editTextRegistrationServerIPAddress.getText()+"";
		final int serverPortNumber = Integer.parseInt(editTextRegistrationServerPort.getText()+"");
		
		

		WifiManager wm = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		final String uniqueDeviceId =  wm.getConnectionInfo().getMacAddress();
		
		
		
		Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		        Socket socketServer = null;
				PrintWriter out = null;
				
				try{
					socketServer = new Socket(serverIpAddress,serverPortNumber);
				     out = new PrintWriter(socketServer.getOutputStream(), 
				                 true);
				   } catch (UnknownHostException e) {
					   Toast.makeText(mContext, "Unknown host" + e.getMessage(), Toast.LENGTH_SHORT).show();
				   } catch  (IOException e) {
				     Toast.makeText(mContext, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
				   }
			
				
				
				
			
				out.println(deviceipAddress);
				out.println(mMonitorWorker.getPortNumber());
				out.println(uniqueDeviceId);

				out.flush();
				out.close();
				
				
				
				try {
					socketServer.close();
				} catch (IOException e) {
				}
				
				
				textViewDeviceInfo.post(new Runnable() {
			        public void run() {
			        	textViewDeviceInfo.setText("Device ID: " +uniqueDeviceId + "\n" + "IP Address: " +deviceipAddress + "\n" + "Port number: " +mMonitorWorker.getPortNumber() );
			        }
			    });
			}
				
		    
		};

		thread.start();
		

		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMonitorWorker.cleanUp();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
