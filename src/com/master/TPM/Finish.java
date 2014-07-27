package com.master.TPM;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;
// Intent service that receives and sends encrypted files to the PC. 
public class Finish extends IntentService {
	public EditText editText1;
	public String text;
	public String pass;
	public int i = 0;
	private static String servIP = null;
    private static int servPort = 2228;
    private static InetAddress serverIPAddress;
	// Constructor methods. 
    public Finish() {
        super("ClientService");
    }
    public Finish(String name) {
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
    	//editText1=(EditText)findViewById(R.id.editText1);
    	Toast.makeText(this,"Connecting...", Toast.LENGTH_LONG).show();
    }

   // Once the service is started it calls the Client code to start passing in the Server port, IP, the Client port.
    @Override
    public void onHandleIntent(Intent intent){
    	System.out.println(intent.toString());
    	servIP=intent.getStringExtra("IP");
    			finished();
				
				//text=intent.getStringExtra("text");
  
    }
   
    //tell server password input is finished and wait for the verification result
    public void finished(){
		String finish = "finished2!";
		try {
			serverIPAddress =  InetAddress.getByName(servIP);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(2223);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] outData = finish.getBytes();
		byte[] inData = new byte[1400];
		int inPacketLength;
   /*construct packet to send*/
		DatagramPacket outPacket = new DatagramPacket(outData, outData.length, serverIPAddress, servPort);
		/*send UDP datagram through socket*/
		try {
			socket.send(outPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*recieve packet from server*/
		DatagramPacket inPacket = new DatagramPacket(inData, inData.length);
		try {
			socket.receive(inPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		inPacketLength = inPacket.getLength();
		
		socket.close();
   /*return the payload in the packet in String*/
		 String reply = new String(inPacket.getData(), 0, inPacketLength);
		 if(reply.equals("Correct!")){
				Context context = getApplicationContext();
				CharSequence text = "Password Verified!";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				Intent intent=new Intent(this, DisplayMessage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    	startActivity(intent);
			}else{
				Context context = getApplicationContext();
				CharSequence text = "Error!";
				int duration = Toast.LENGTH_SHORT;

				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				
			}
		 
	}

	}