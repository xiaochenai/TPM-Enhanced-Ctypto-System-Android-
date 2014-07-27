package Sender_Receiver;

   import java.io.*;
   import java.net.*;
   import java.security.*;
   import java.util.*;
   import javax.crypto.*;
   import javax.crypto.spec.*;	
	
   import IDACS_Connection.*;
   import IDACS_Common.*;
   import biz.source_code.base64Coder.Base64Coder;
   


   public class Sender
   {
   	
	  static final int DEFAULT_SENDER_IN_PORT = 9031;
	  static final int DEFAULT_LISTENER_IN_PORT = 9032;
	
	  static final int NUM_PACKETS_TO_SEND = 2000;
	  static final int PACKET_DATA_LENGTH = 1441;
	
	  private String RmtIP;
	  private int RmtPort;
	  private int LocalPort;
	  private InetAddress RmtC;
	  private IDACSConnection theConnection;

	  public void init(String RmtIP,int RmtPort,int LocalPort) throws UnknownHostException{
		  this.RmtIP = RmtIP;
		  this.LocalPort = LocalPort;
		  this.RmtPort = RmtPort;
		  this.RmtC = InetAddress.getByName(this.RmtIP);
		  this.theConnection = new IDACSConnection( this.RmtC,this.RmtPort,this.LocalPort );
	  }
	  public void init(String RmtIP) throws UnknownHostException{
		  this.RmtIP = RmtIP;
		  this.LocalPort = DEFAULT_SENDER_IN_PORT;
		  this.RmtPort = DEFAULT_LISTENER_IN_PORT;
		  this.RmtC = InetAddress.getByName(this.RmtIP);
		  this.theConnection = new IDACSConnection( this.RmtC,IDACSCommon.DEFAULT_LISTENER_IN_PORT, IDACSCommon.DEFAULT_SENDER_IN_PORT );
		  System.out.println("Finish Initialization");
	  }
	  public boolean SendData(byte[][] data) throws IOException{
		//WriteToF(data,"ciphertext_Alice1.txt");
		boolean success = theConnection.sendData(data);
		if(success == true)
			System.out.println("SEND SUCCESULLY");
		else
			System.out.println("SEND FAILED");
			
		return success;
		  
		  
		  
	  }
	  public void close(){
		  this.theConnection.close();
	  }
	  public boolean isInterrupted(){
		  if(theConnection != null)
		  return theConnection.isInterrupted();
		  else {
			return true;
		}
	  }
	  //args[]={RMTIP,File Size}
//	  public static void main( String[] args) throws NoSuchAlgorithmException
//		
//	  {
//		int Round=0,RoundTarget=2;
//		 
//	     try
//	     {	
//	    	//initialization
//	    	BufferedReader theKeyboard = new BufferedReader( new InputStreamReader( System.in ) );		
//	    	Sender sender = new Sender();
//	        sender.init(args[0]);
//	     	
//	     // run the main program  
//	        do
//	        {	// read in the command to send datagrams or exit the progam
////	        	System.out.println("Press enter to send packets, type 'end' to exit: ");
////	            String input = theKeyboard.readLine();
////	        	byte[] finish = new byte[6];
////	        	FileInputStream fis = new FileInputStream("Finish.txt");
////	        	fis.read(finish);
////	        	String Result= "";
////	        	if(finish != null){
////	        		Result =  new String(finish);
////	        		System.out.println("Finish Flag " + Result);
////	        	}
////	        		
////	        	if(Result.equals("Finish")){
////	        		FileOutputStream fos = new FileOutputStream("Finish.txt");
////	        		Result="Unfini";
////	        		fos.write(Result.getBytes());
////	        		fos.close();
////	        	}
//	        	
//	        	
//	        
////	           if(Result.equals("Unfini")){
//	        	   boolean result = false;
//	        	   SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//	        	   
//	   	        // create a packet of data
//	   			   byte[] filedata = "Hellooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo".getBytes();	 
//	   			  // byte[][] data = sender.PreparePacket(filedata);
//	   			   //send data
//	   			   byte[] testdata = new byte[Integer.parseInt(args[1])];
//	   			   sr.nextBytes(testdata);
//	   			   byte[] DataToReceiver = Base64Coder.encodeLines(filedata).getBytes();
//	   			   MessageDigest hash = MessageDigest.getInstance("SHA-1");
//	   			   hash.update(DataToReceiver);
//	   			   byte[] hashResult = hash.digest();
//	   			   
//	   			   //System.out.println("SIZE : " + DataToReceiver.length);
//	   			   byte[][] data = sender.PreparePacket(DataToReceiver);
//	   			   long start = System.currentTimeMillis();
//	   			   //System.out.println("START : " + start);
//	   			   result = sender.SendData(data);
//	   			   long end = System.currentTimeMillis();
//	   			   String HashInBase64 = Base64Coder.encodeLines(hashResult);
//	   			   String Line = "TransmitTime: " + (end-start)  + " HashValue " + HashInBase64;
//	   			   if(result == true){
//	   				   System.out.print( Line );
//	   			   }
//	   			   FileOutputStream fos = new FileOutputStream("Sender.txt",true);
//	   			   fos.write(Line.getBytes());
//	   			   fos.close();
//	   			   Round++;
//	   			   System.out.println("This is Round " + Round);
//	          // }
//	           
//				  
//			   
//				
//			   
//	        
//	        
//	           
//	     
//	        
//	        }
//	        while(Round<RoundTarget);
//	        System.out.println("Close");
//	        sender.close();
//	        sender = null;
//	     }
//	        catch(UnknownHostException uhe){System.out.println(uhe);}
//	        catch(IOException ioe){System.out.println(ioe);}
//	     
//	  	
//	  }
	  public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
	
		// Get the size of the file
		long length = file.length();
	
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
	
		// Create the byte array to hold the data
		byte[] bytes = new byte[(int)length];
	
		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
			   && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}
	
		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}
	
		// Close the input stream and return bytes
	  		is.close();
	  		return bytes;
	  	}
	   
	  public byte[] PreparePacketContent(String Filename){
		  byte[] filedata={};
		  try{
			  filedata = getBytesFromFile(new File(Filename)); 
		  }
		  catch(IOException ioe) {}
		  System.out.println("Finish Content");
		return filedata;
		  
	  } 
	  public byte[][] PreparePacket(byte[] filedata){
		  byte[][] data = new byte[(filedata.length/1441)+1][PACKET_DATA_LENGTH];
		  int i = 0;
		  while(i< filedata.length){
			data[i/1441][i%1441]=filedata[i];
			i++;
		  }
			  
		  
		  System.out.println("Finish Packet");
		  return data;
	  }
	  public static void WriteToF(byte[][] data,String Filepath) throws IOException{
			FileOutputStream fos = new FileOutputStream(Filepath,true);
			for(int index=0;index<data.length;index++){
				byte[] filedata = new byte[data[index].length];
				System.arraycopy(data[index], 0, filedata, 0, data[index].length);
				byte[] filedata_RemoveNull = findNulls(filedata);
				//fos.write(filedata_RemoveNull);
			}
			fos.close();
	  }
	  public static void WriteToF(byte[] data,String Filepath) throws IOException{
		  FileOutputStream fos = new FileOutputStream(Filepath,true);
		  byte[] filedata = findNulls(data);
		  fos.write(filedata);
		  fos.close();
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
   }