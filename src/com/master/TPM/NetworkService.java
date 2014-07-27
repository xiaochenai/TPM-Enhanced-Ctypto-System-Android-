package com.master.TPM;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.http.auth.AUTH;

import com.master.TPM.RadialMenuActivity.ResponseReceiver;



import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
// Intent service that receives and sends encrypted files to the PC. 
public class NetworkService extends IntentService {
	public EditText editText1;
	public String text;
	public String pass;
	public int i = 0;
	private static String ServerIP = null;
    private static int ServerPort = 1234;
    private static String PCIP = null;
    private static String auth = null;
    private static String nauth = null;
    public static String verification = "False";
    public static String allreceived = "False";
	// Constructor methods. 
    public NetworkService() {
        super("networkservice");
    }
    public NetworkService(String name) {
        super(name);
    }
    
    // Toasts to let user know the service has started. 
    @Override
    public void onCreate(){
    	super.onCreate();
    	try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Toast.makeText(this,"Connecting...", Toast.LENGTH_LONG).show();
    }

   // Once the service is started it calls the Client code to start passing in the Server port, IP, the Client port.
    @Override
    public void onHandleIntent(Intent intent){
    	System.out.println(intent.toString());
    			try {
    				ServerIP=intent.getStringExtra("ServerIP");
    				PCIP = intent.getStringExtra("PCIP");
    				auth = intent.getStringExtra("auth");
    				nauth = intent.getStringExtra("Nauth");
					System.out.println(ServerIP);
					System.out.println(PCIP);
					System.out.println(auth);
					System.out.println(nauth);
					sends();
					Intent broadcastIntent = new Intent();
					broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
					broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
					broadcastIntent.putExtra(verification, verification);
					broadcastIntent.putExtra(allreceived, allreceived);
					sendBroadcast(broadcastIntent);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  
    }
   
    //transform password digits into random number sequence and send them to server for verification
public void sends() throws IOException{
		
	Socket client = new Socket(ServerIP, 1234);
	System.out.println("init socket 2");
	PrintStream out = new PrintStream(client.getOutputStream());
	BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
	//send sentence to server
	System.out.println("init socket 3");
	out.println(auth);
	String reply = buf.readLine();
	System.out.println(reply);
	client.close();
	client = new Socket(ServerIP, 1234);
	System.out.println("init socket 2");
	out = new PrintStream(client.getOutputStream());
	buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
	if(reply.equals("Verified")){
		verification = "Verified";
		System.out.println("send nauth");
		out.println(nauth);
		System.out.println("send nauth");
		reply = buf.readLine();
		System.out.println(reply);
		allreceived = "allreceived";
	}else{
		System.exit(0);
	}
	client.close();
	}

class TestThread implements Runnable {
	String sentence;
	TestThread(String s) {
		sentence = s;
	}
	
	@Override
	public void run() {
		try {
			Socket client = new Socket(ServerIP, 1234);
			System.out.println("init socket 2");
			PrintStream out = new PrintStream(client.getOutputStream());
			BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
			//send sentence to server
			System.out.println("init socket 3");
			out.print(auth);
			
		}
		catch (IOException e)
		{
			System.out.println("An Error Occurs!");
		}
	}

}

//convert the keystroke character to random string;
		
    	}