package com.master.TPM;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import biz.source_code.base64Coder.Base64Coder;

import GeneralClass.Hash;
import Sender_Receiver.Sender;
import Sender_Receiver.ServerListener;
import VKEGeneration.NewKeysetGeneration;
import android.R.integer;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
// Intent service that receives and sends encrypted files to the PC. 
public class ClientService extends IntentService {
	public final static String EXTRA_MESSAGE = "com.Master.MESSAGE";
	static final int MAX_PACKET_SIZE = 256;
	private int echoServPort=2223; /* Echo server port */
	private int echoClientPort=2228; /* Echo client port */
	private int packetsToSend=0; /* Number of packets to send */
	private String echoFile;
	private byte[] echoBytes;
	private int numPackets=0; 
	private String PCIP = "192.168.0.100";
	private ServerListener serverListener = new ServerListener();
	private Sender sender = new Sender();
	DatagramSocket socket=null;
	private boolean fileTransferFinished = false;
	private String echoFileName;
	private String fileReceivedInPC = "NotReceived";
	ArrayList<String> receivedStringArrayList = new ArrayList<String>();
	public String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();

	// Constructor methods. 
    public ClientService() {
        super("ClientService");
    }
    public ClientService(String name) {
        super(name);
    }
    // Toasts to let user know the service has started. 
    @Override
    public void onCreate(){
    	super.onCreate();
    	
    	System.out.println("On Create");
    	try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Toast.makeText(this,"Connecting to PC", Toast.LENGTH_LONG).show();
    }

   // Once the service is started it calls the Client code to start passing in the Server port, IP, the Client port.
    @Override
    public void onHandleIntent(Intent intent){
    	System.out.println(intent.toString());
    	Log.v("Android GUI Connecting", "connect to PC");
    			try {
    				PCIP=intent.getStringExtra("PCIP");
					System.out.println(PCIP);
					Client(echoServPort,PCIP,echoClientPort,intent);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
  
    }
    // This function retrieves the key, and the message to decrypt or encrypt and calls the appropriate functions.
    public void Client(int echoServport,String servIP,int echoClientPort,Intent intent) throws InterruptedException{
    	try {
    		int i=0;
    		byte[] key=intent.getByteArrayExtra("key");
    		String key1 = new String(key);
    	    System.out.println(key1);
    	    PCIP = intent.getStringExtra("PCIP");
    	    if(sender.isInterrupted()){
    			sender.init(PCIP);
    		}else {
				sender.close();
				sender.init(PCIP);
			}
			if(serverListener.isInterrupted()){
				serverListener.init();
			}
			else {
				serverListener.close();
				serverListener.init();
			}
    	    String message=intent.getStringExtra("crypto");
    	    System.out.println("Message from last activity: " + message);
    	    Log.v("message from last activity", message);
    		byte[] buffer = new byte[MAX_PACKET_SIZE];
			byte[] buff = key;
			System.out.println("key" );
			System.out.println(new String(buff));
			
			//Send the cryptographic key bytes to PC
			Log.v("Connect to PC", "send key to PC");
//			socket = new DatagramSocket(echoClientPort);
//			DatagramPacket connect = new DatagramPacket(buff,buff.length,InetAddress.getByName(servIP),echoServPort);
//			socket.send(connect);
			System.out.println("send out key :  " + (Base64Coder.encodeLines(("key:"+new String(buff)).getBytes())));
			sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(("key:"+new String(buff)).getBytes()).getBytes()));
			
//			DatagramPacket expectedPacket = new DatagramPacket(buffer, buffer.length);
//			socket.receive(expectedPacket);
			
			
			byte[] repliedBytes;
			String repliedString;
//			byte[] repliedBytes = serverListener.ReadData();
//			String repliedString = new String(Base64Coder.decodeLines(new String(repliedBytes)));
			System.out.println("About to listen!");
			//System.out.println("Message from server " + new String(expectedPacket.getData()));
			Log.v("Message from server", "message : " + message);
			//send the file pieces back to PC
			if(message.equals("decrypt"))
			{				
				Log.v("decrypt", "decrypt");
				File dir=new File("//sdcard//DCIM//mp//");
				File[] files=dir.listFiles();
				System.out.println("Num Files:"+files.length);
				for(i=0;i<files.length;i++)
				{
					String fileName=files[i].getName();
					System.out.println("File Name : " + fileName);
					connect(fileName);
					files[i].delete();
					
				}
				
				buff= new byte[MAX_PACKET_SIZE];
//			    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			    DataOutputStream dos = new DataOutputStream(baos);
//				dos.writeBytes("Finished");
//			    dos.close();
//				buff = baos.toByteArray();
//				DatagramPacket finishPack=new DatagramPacket(buff,buff.length,InetAddress.getByName(servIP),echoServPort);
//				socket.send(finishPack);
				
			    
			    System.out.println("Socket Closed");
	    	}
			else{
				Log.v("not decrypt", "not decrypt");
				while(fileTransferFinished == false){
					//receive message from PC
					Log.v("UnFinished", "Unfinished" + message);
//					DatagramPacket mess=new DatagramPacket(buffer,buffer.length);
//					socket.receive(mess);
//					repliedBytes = serverListener.ReadData();
//				    repliedString = new String(Base64Coder.decodeLines(new String(repliedBytes)));
//				    message = repliedString;
					Log.v("Encrypt message from server", "message : " + message);
					System.out.println("Message:"+message);
//					DatagramPacket messr=new DatagramPacket(buffer,buffer.length, mess.getAddress(), mess.getPort());
//					socket.send(messr);
//					sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(buff).getBytes()));
//					System.out.println("Sent messr : " + new String(buffer));
					if(!message.contains("Finished"))
					{
						System.out.println("Go to Listen");
						listen_new();
						System.out.println("Finished with listen.");
					}    
				}
    			
    		}
			
			//sender.SendData(sender.PreparePacket(Base64Coder.encodeLines("finishreceived".getBytes()).getBytes()));
			receivedStringArrayList.clear();
			System.out.println("Finished");
			Bundle bundle = intent.getExtras();
		    if (bundle != null) {
		        Messenger messenger = (Messenger) bundle.get("messenger");
		        Message msg = Message.obtain();
		        Bundle data=new Bundle(1);
		        data.putString("Update","Update");
		        msg.setData(data); //put the data here
		        try {
		            messenger.send(msg);
		        } catch (RemoteException e) {
		            Log.i("error", "error");
		        }
		    }
		    if(fileReceivedInPC.equals("allReceived") || fileTransferFinished == true){
		    	sender.close();
			    serverListener.close();
		    }
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    // The listen function receives files from the PC and saves them to the app directory. 
    public void listen()
	{
    	try
		{
    		
			byte[] buffer = new byte[MAX_PACKET_SIZE];
			
			//get the number of packets to be sent
//			DatagramPacket numPackets=new DatagramPacket(buffer,buffer.length);
//			System.out.println("Getting number of packets now");
//			socket.receive(numPackets);
			System.out.println("in Listener waiting for packets");
			byte[] receivedBytes = serverListener.ReadData();
			String receivedStrings  = new String(Base64Coder.decodeLines(new String(receivedBytes)));
//			DatagramPacket reply = new DatagramPacket(numPackets.getData(), numPackets.getLength(), numPackets.getAddress(), numPackets.getPort());
//			socket.send(reply);
			System.out.println("received String : " + receivedStrings);
			while(!receivedStrings.contains("PN")){
				receivedBytes = serverListener.ReadData();
				receivedStrings  = new String(Base64Coder.decodeLines(new String(receivedBytes)));
				System.out.println("READOUT STRING : " + receivedStrings);
			}
			//reply to PC single core
			sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(receivedStrings.getBytes()).getBytes()));
//			ByteArrayInputStream bais = new ByteArrayInputStream(numPackets.getData());
//			DataInputStream dis = new DataInputStream(bais);
//			int packNum = dis.readInt();
			System.out.println("received numPackets: " + receivedStrings.split(":")[1]);
			int packNum = Integer.parseInt(receivedStrings.split(":")[1]);
			//get the filename of the packets being sent
			String fileName = "";
			buffer = new byte[MAX_PACKET_SIZE];
//			DatagramPacket arrivalPacket = new DatagramPacket(buffer,buffer.length);
//			socket.receive(arrivalPacket);
//			fileName = new String(arrivalPacket.getData(),"us-ASCII");
			receivedBytes = serverListener.ReadData();
			receivedStrings = new String(Base64Coder.decodeLines(new String(receivedBytes)));
			while(!receivedStrings.contains("FN")){
				receivedBytes = serverListener.ReadData();
				receivedStrings  = new String(Base64Coder.decodeLines(new String(receivedBytes)));
				System.out.println("READOUT STRING : " + receivedStrings);
			}
			System.out.println("Filename is:"+receivedStrings);
			fileName = receivedStrings.split(":")[1];
//			reply = new DatagramPacket(arrivalPacket.getData(), arrivalPacket.getLength(), arrivalPacket.getAddress(), arrivalPacket.getPort());
//			socket.send(reply);
			//replay to PC
			
			
			byte[]	data=new byte[MAX_PACKET_SIZE*packNum];
			int dataIndex=0;
			System.out.println("receiving file content");
			
			//get the contents of the file being sent
			while(receivedStringArrayList.size()<packNum)
			{	
				System.out.println("packNum : " + receivedStringArrayList.size());
				buffer = new byte[MAX_PACKET_SIZE];
//				arrivalPacket = new DatagramPacket(buffer,buffer.length);
//				socket.receive(arrivalPacket);
				
				byte[] receivedFileBytes = serverListener.ReadData();
				String receivedFileStrings = new String(Base64Coder.decodeLines(new String(receivedFileBytes)));
				System.out.println("received Strings : " + receivedFileStrings);
				if(receivedFileStrings.contains("FC")){
					System.out.println("received file content");
					if(!receivedStringArrayList.contains(receivedFileStrings.split(":")[1])){
						System.out.println("received Contents : " + receivedFileStrings.split(":")[2]);
						byte[] decodedcontent = Base64Coder.decodeLines(receivedFileStrings.split(":")[2]);
						
							receivedStringArrayList.add(receivedFileStrings.split(":")[1]);
							for(int j=0;j<MAX_PACKET_SIZE&&j<decodedcontent.length;j++){
								data[dataIndex]=decodedcontent[j];
								dataIndex++;
							}
							//single core
						sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(receivedFileStrings.getBytes()).getBytes()));
						System.out.println("send out strings : " + receivedFileStrings);
					}
				}
				
				// create and send response packet
//				reply = new DatagramPacket(arrivalPacket.getData(), arrivalPacket.getLength(), arrivalPacket.getAddress(), arrivalPacket.getPort());
//				socket.send(arrivalPacket);
				
			}
			System.out.println("finish receing file content");
			System.out.println("Filename:"+fileName);
			//display message saying file received
			fileName = fileName.replaceAll("\\..*","");
			fileName+=".piece";
			System.out.println("FileName changed is:"+fileName);
		    Hash hash = new Hash();
		    byte[] temp_hash = hash.GetHash(findNulls(data), 1);
		    System.out.println("Hash value of received file is " + Base64Coder.encodeLines(temp_hash));
			File folder=new File("//sdcard//DCIM//mp//"+fileName);
			folder.createNewFile();
			FileOutputStream fos = null;
			fos = new FileOutputStream(folder);
			fos.write(data);
			fos.flush();
			fos.close();
			Toast.makeText(this,"File Received:"+ fileName, Toast.LENGTH_LONG).show();
			fileTransferFinished = true;
			
			
		}
		catch (SocketException e)
		{
			System.err.println("Error creating the server socket: " + e.getMessage());
		}
		catch (IOException e)
		{
			System.err.println("Server socket Input/Output error: " + e.getMessage());
		}
	}
    public void listen_new() throws IOException{
    	byte[] buffer = new byte[MAX_PACKET_SIZE];
    	ServerSocket Server = new ServerSocket(4444);
		Socket client = Server.accept();
		BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
		

		System.out.println("in Listener waiting for packets");
		byte[] receivedBytes = serverListener.ReadData();
		String receivedStrings  = new String(Base64Coder.decodeLines(new String(receivedBytes)));
		while(!receivedStrings.contains("FileName")){
			receivedBytes = serverListener.ReadData();
			receivedStrings  = new String(Base64Coder.decodeLines(new String(receivedBytes)));
		}
		System.out.println("receivedStrings : " + receivedStrings);
		String[] receivedStringArray = receivedStrings.split(":split:");
		String fileName = receivedStringArray[1];
		String fileContentinBase64 = receivedStringArray[2];
		
		System.out.println("Filename:"+fileName);
		//display message saying file received
		fileName = fileName.replaceAll("\\..*","");
		fileName+=".piece";
		System.out.println("FileName changed is:"+fileName);
	    Hash hash = new Hash();
	    byte[] temp_hash = hash.GetHash(fileContentinBase64.getBytes(), 1);
	    System.out.println("Hash value of received file is " + Base64Coder.encodeLines(temp_hash));
		File folder=new File("//sdcard//DCIM//mp//"+fileName);
		folder.createNewFile();
		FileOutputStream fos = null;
		fos = new FileOutputStream(folder);
		fos.write(Base64Coder.decodeLines(fileContentinBase64));
		fos.flush();
		fos.close();
		Toast.makeText(this,"File Received:"+ fileName, Toast.LENGTH_LONG).show();
		String FSO = buf.readLine();
		if (FSO.equals("File send out"));
			fileTransferFinished = true;
		Server.close();
		
    }
    private static int findLastMeaningfulByte(byte[] array)
	{
		//System.out.println("Attempting to find the last meaningful byte of " + asHex(array));
		int index=0;

		for (index=(array.length - 1); index>0; index--) {
		//System.out.println("testing index " + index + ". Value: " + array[index]);
		if (array[index] != (byte)(0)) {
		//System.out.println("Last meaningful byte found at index " + index);
		return index;
		}
		}
		System.out.println("No meaningful bytes found.  Perhaps this is an array full of nulls...");
		return index;
	}
  
//remove non meaningful bytes from byte[] buffer	
  private static byte[] findNulls(byte[] buffer)
	{
		int terminationPoint = findLastMeaningfulByte(buffer);
		byte[] output;
		output = new byte[terminationPoint + 1];
		System.arraycopy(buffer, 0, output, 0, terminationPoint + 1);
		return output;
	}
    // This function sets the filename, the Client/Server ports, and the Server IP and calculates how many packets are in the 
    // data to be sent to the PC. 
	public void Client(int echoServPort, String PCIP, int echoClientPort, File fileName) 
	{


		this.echoFileName = fileName.getName();

		this.numPackets = 0;

			System.out.println("filename: "+echoFileName);
			//gets the contents of the file
			System.out.println("File Path : " + fileName.getPath());
			this.echoBytes = read(fileName.getPath());
			System.out.println("contents(bytes): " + echoBytes.length);
		
		//determines the number of packets needed to send to Android
		if(echoBytes.length%MAX_PACKET_SIZE==0)
		{
			numPackets = echoBytes.length/MAX_PACKET_SIZE;
		}
		else
		{
			numPackets = (echoBytes.length/MAX_PACKET_SIZE)+1;
		}
    }

    		/**
    		* Sends packets to the client PC and reads the responses.
    		 * @throws IOException 
    		*/
			public void send_new() throws IOException{
				ServerSocket Server = new ServerSocket(4444);
				Socket client = Server.accept();
				PrintStream out = new PrintStream(client.getOutputStream());
				BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
				System.out.println("wait for key reply");
				String Treply = buf.readLine();
				System.out.println("key replied : " + Treply);
				// to be continued ...... send file content to PC
				String fileContentString = Base64Coder.encodeLines(echoBytes);
				String echoStringinBase64 = "FileName" + ":split:" + echoFileName + ":split:" + fileContentString;
						
				
				sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(echoStringinBase64.getBytes()).getBytes()));
				fileReceivedInPC = buf.readLine();
			}
    		public void send()
    		{
    			
    		
    			try
    			{
    				byte[] buffer = new byte[MAX_PACKET_SIZE];

    				if (numPackets >= 1) 
    				{
    					int c=0;
    					//sends the number of packets being sent single core
////    					DatagramPacket numPacks = new DatagramPacket(buff,buff.length,InetAddress.getByName(clientIP),echoClientPort);
    					System.out.println("wait for key reply");
    					byte[] receiveFileContent = serverListener.ReadData();
						String receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
//						System.out.println("received Strings : " +receiveFileStrings);
//    					socketServer.send(numPacks);
    					sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(("PN:"+numPackets+"").getBytes()).getBytes()));
    					System.out.println("send Num of Packs : " + numPackets);
    					//receive response
//    					DatagramPacket expectedPacket = new DatagramPacket(buffer, buffer.length);
//    					socketServer.receive(expectedPacket);
    					//single core
    					receiveFileContent = serverListener.ReadData();
						receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
						System.out.println("received Strings : " +receiveFileStrings);
						while(!receiveFileStrings.equals("PN:"+numPackets)){
							receiveFileContent = serverListener.ReadData();
							receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
							System.out.println("received Strings : " +receiveFileStrings);
						}
    					//sends the name of the file
//    					DatagramPacket nameFile = new DatagramPacket(fileName2,fileName2.length,InetAddress.getByName(clientIP),echoClientPort);
//    					socketServer.send(nameFile);
//    					System.out.println("send out namefile");
//    					expectedPacket = new DatagramPacket(buffer, buffer.length);
//    					socketServer.receive(expectedPacket);
//    					System.out.println(new String(expectedPacket.getData(),"US-ASCII"));
    					System.out.println("send out file name : " + echoFileName);
    					sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(("FN:"+echoFileName).getBytes()).getBytes()));
    					//single core
    					receiveFileContent = serverListener.ReadData();
						receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
						System.out.println("received Strings : " +receiveFileStrings);
						while(!receiveFileStrings.equals("FN:"+echoFileName)){
							receiveFileContent = serverListener.ReadData();
							receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
							System.out.println("received Strings : " +receiveFileStrings);
						}
    					//sends the contents of the file
    					Hash hash = new Hash();
    					byte[] temp_hash = hash.GetHash(echoBytes,1);
    					System.out.println("content hash value : " + Base64Coder.encodeLines(temp_hash));
    					int n=0;
    					for(int i=0;i<echoBytes.length;i++){
    						buffer[c] = echoBytes[i];
    						c++;
    						if(c==MAX_PACKET_SIZE){
    							System.out.println("packNum : " + n);
    							
//    							DatagramPacket requestPacket = new DatagramPacket(buffer,buffer.length,InetAddress.getByName(clientIP),echoClientPort);
//    							socketServer.send(requestPacket);
    							String buffinBase64 = Base64Coder.encodeLines(buffer);
    							sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(("FC:"+n+":"+buffinBase64).getBytes()).getBytes()));
    							System.out.println("send request packets" + "FC:"+n+":"+buffinBase64);
    							System.out.println("send out contnet : " + buffinBase64);
    							buffer = new byte[MAX_PACKET_SIZE];
    							c=0;
    							
    							//receive respongse from server
//    							expectedPacket = new DatagramPacket(buffer, buffer.length);
//    							socketServer.receive(expectedPacket);
    							System.out.println("waiting for reply");
    							//single core
    							receiveFileContent = serverListener.ReadData();
    							receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
    							System.out.println("received Strings : " + receiveFileStrings);
    							while(!receiveFileStrings.equals("FC:"+n+":"+buffinBase64)){
    								receiveFileContent = serverListener.ReadData();
        							receiveFileStrings = new String(Base64Coder.decodeLines((new String(receiveFileContent))));
        							System.out.println("received Strings : " + receiveFileStrings);
    							}
    							
    							buffer = new byte[MAX_PACKET_SIZE];
    							n++;
    						}
    					}
    					
    					//sends the last bit of contents if there are any
//    					DatagramPacket requestPacket = new DatagramPacket(buffer,buffer.length,InetAddress.getByName(clientIP),echoClientPort);
//    					socketServer.send(requestPacket);
    					if(findNulls(buffer) != null && !(findNulls(buffer)[0] == 0 && findNulls(buffer).length == 1)){
        					System.out.println("send out last bits of contents");
        					String buffinBase64 = Base64Coder.encodeLines(findNulls(buffer));
        					sender.SendData(sender.PreparePacket(Base64Coder.encodeLines(("FC:"+n+":"+buffinBase64).getBytes()).getBytes()));
        					System.out.println("send request packets" + "FC:"+n+":"+buffinBase64);
        					System.out.println("send out contnet : " + buffinBase64);
        					//single core
        					byte[] receivedata = serverListener.ReadData();
        					String receiveString = new String(Base64Coder.decodeLines((new String(receivedata))));
        					System.out.println("received strings : " + receiveString);
        					System.out.println("Finish sending content");
    					}

//    					expectedPacket = new DatagramPacket(buffer, buffer.length);
//    					socketServer.receive(expectedPacket);
    					
    					//deletes the piece that was sent
    					

    				}
    			}
    			catch (SocketException e)
    			{
    				System.err.println("Error creating the client socket: " + e.getMessage());
    			}
    			catch (IOException e)
    			{
    				System.err.println("Client socket Input/Output error: " + e.getMessage());
    			}

    		}
    		// Reads the contents of files into a byte array to be sent to the PC in datagram packets. 
    		public static byte[] read(String aInputFileName){
    		    File file = new File(aInputFileName);
    		    
    		    byte[] result = new byte[(int)file.length()];
    		    try {
    		      InputStream input = null;
    		     
    		        int totalBytesRead = 0;
    		        input = new BufferedInputStream(new FileInputStream(file));
    		        while(totalBytesRead < result.length){
    		          int bytesRemaining = result.length - totalBytesRead;
    		          //input.read() returns -1, 0, or more :
    		          int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
    		          if (bytesRead > 0){
    		            totalBytesRead = totalBytesRead + bytesRead;
    		          
    		        }
    		        /*
    		         the above style is a bit tricky: it places bytes into the 'result' array; 
    		         'result' is an output parameter;
    		         the while loop usually has a single iteration only.
    		        */
    		      
    		        }
    		        input.close();
    		      
    		    } catch (FileNotFoundException ex) {
    		    } catch (IOException ex) {
    		    }
    		    return result;
    		  }
    		
// Connect function sets file name and calls client and sending functions. 
    		public void connect(String fileName) throws IOException
    		{
    		
    			File _echoFile =new File("//sdcard//DCIM//mp//"+fileName); /* String to send to echo server */
    			Client(echoServPort, PCIP, echoClientPort, _echoFile);
    			System.out.println("About to send...");
    			send_new();

    		}
    	
    	}