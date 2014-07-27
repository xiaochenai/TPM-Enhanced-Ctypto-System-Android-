package BackGround;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import com.master.TPM.NetworkService;



import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import biz.source_code.base64Coder.Base64Coder;

import KeySetGeneration.KeySetGeneration;
import MaskGeneration.Candidates;
import RandomKeyStroke.RandomKeyStroke;
import Sender_Receiver.Sender;
import Sender_Receiver.ServerListener;
import VKEGeneration.KeySet;
import VariedLengthMappingTable.VariedLengthMappingTable;

public class BackGround implements Parcelable  {
	private String[] oldKeySet;
	private String[] newKeySet;
	private String[][] OldKeySet;
	private String[][] NewKeySet;
	private VariedLengthMappingTable VLMT;
	private RandomKeyStroke RKS;
	private int[] UKS;
	private String[] MASK;
	private String[] Processed_UKS;
	private ArrayList<Integer> uKS_ArrayList = new ArrayList<Integer>();
	private Candidates candidates;
	private String[] oldMask;
	private String[] old_Processed_UKS;
	private long LocalSeed;
	private long PeerSeed;
	private int uKS_Null_Counter=0;
	private ArrayList<String[]> rKSArrayList = new ArrayList<String[]>(); 
	private String ServerIP = "192.168.0.106";
	private String replyFromServer = null;
	private boolean Verified=false;
	private boolean AllReceived = false;
	private boolean isAuthentication=false;
	private Intent intent;
	public void getIP(String ServerIP){
		this.ServerIP = ServerIP;
	}
	public void Background_Parameter_trainsit(String[] OldKeySet, String[] NewKeySet, VariedLengthMappingTable VLMT,RandomKeyStroke RKS){
		this.oldKeySet = OldKeySet;
		this.newKeySet = NewKeySet;
		this.VLMT = VLMT;
		this.RKS = RKS;
	}
	public void Background_Key_Pressed_transit(String keyset){
		System.out.println("Mouse Clicked, keystring is : " + keyset);
		System.out.println("INDEX is : " + this.GetIndex(keyset, oldKeySet));
		uKS_ArrayList.add(this.GetIndex(keyset, oldKeySet));
		System.out.println("CURRENT ARRAYLIST");
		for(Integer i:uKS_ArrayList){
			System.out.println(i);
		}
	}
	public void Recover_old_processed_UKS(){
		
	}
	public void getIntent(Intent intent){
		this.intent=intent;
	}


	private int GetIndex(String keyset,String[] keysets){
		for(int i=0;i<keysets.length;i++){
			if(keyset.endsWith(keysets[i])){
				return i;
			}
			
		}
		return -1;
	}
	private void generateOldRKS(String[] RUKS){
		int Index=0;
		for(int i=0;i<rKSArrayList.size();i++){
			if(rKSArrayList.get(i) == null){
				System.out.print("NULL");
			}else {
				RKS.UpdateRKS_with_UKS(RUKS[Index]);
				Index++;
				if(Index == RUKS.length)
					Index=0;
				String[] tempRKS = RKS.RKS_Determination();
				rKSArrayList.set(i, tempRKS);
				for(int j=0;j<rKSArrayList.get(i).length;j++){
					System.out.print(rKSArrayList.get(i)[j] + " " );
				}
				
			}
			System.out.println();
		}
	}
	private void generateNewRKS(String[] PUKS){
		int Index=0;
		for(int i=0;i<rKSArrayList.size();i++){
			if(rKSArrayList.get(i) == null){
				System.out.print("NULL");
			}else {
				RKS.UpdateRKS_with_UKS(PUKS[Index]);
				Index++;
				if(Index == PUKS.length)
					Index=0;
				String[] tempRKS = RKS.RKS_Determination();
				rKSArrayList.set(i, tempRKS);
				for(int j=0;j<rKSArrayList.get(i).length;j++){
					System.out.print(rKSArrayList.get(i)[j] + " " );
				}
				
			}
			System.out.println();
		}
	}
	public void VerifiedUpdate() throws IOException{
		System.gc();
		File f2 = new File("//sdcard//Keyset2.txt");
		File f1 = new File("//sdcard//Keyset.txt");
		if(f1.exists()){
			if(f1.delete())
				System.out.println("fi deleted");
		}
		if(f2.exists()){
			if(f2.renameTo(new File("//sdcard//Keyset.txt")))
				System.out.println("f2 renamed");
		}
		File file = new File("//sdcard//RNDArray.txt");
		if(file.exists()){
			System.out.println("RND exist");
		}else {
			System.out.println("RND does not exist");
		}
		file = new File("//sdcard//MASK.txt");
		if(file.exists()){
			System.out.println("MASK exist");
		}else {
			System.out.println("mask does not exist");
		}
		candidates.SaveMask2File("//sdcard//MASK.txt");
		candidates.SaveProcessedUKS2File("//sdcard//Processed_UKS.txt");
	}
	public String[] SubmitClicked() throws IOException, NoSuchAlgorithmException, NoSuchProviderException{
		//this need to be changed
		System.out.println("enter android submitClicked");
		String authString = "";
		String nauthString="";
		this.UKS = new int[uKS_ArrayList.size()-uKS_Null_Counter];
		for(int i=0;i<uKS_ArrayList.size();i++){
			if(uKS_ArrayList.get(i) != -1)
				UKS[i] = uKS_ArrayList.get(i);
		}
		for(int a:UKS){
			System.out.println(a);
		}
		candidates_pre_process();
		File file;
		
		candidates = new Candidates(OldKeySet, NewKeySet, UKS,VLMT);
		//now we are going to generate the UKS for authentication
		file = new File("//sdcard//MASK.txt");
		if(file.exists()){
			int MaskLength = candidates.LoadExistMask("//sdcard//MASK.txt");
			if(MaskLength != UKS.length){
				System.out.println("AAuth Authentication Failed");
				System.exit(0);
			}
			
			String[] RUKS = candidates.doReverseMASK();
			System.out.println("Strings in RUKS");
			for(String s:RUKS){
				System.out.println(s);
			}
			Save2File("//sdcard//RUKS.txt", RUKS);
			String[] SUKS = this.ReadProcessedUKS("//sdcard//Processed_UKS.txt");
			String[] RRUKSBASE64 = new String[RUKS.length];
			for(int i=0;i<RUKS.length;i++){
				RRUKSBASE64[i] = Base64Coder.encodeString(RUKS[i]);
			}
			//System.out.println("Check RUKS");
			for(int i=0;i<RUKS.length;i++){
				//System.out.println("****************");
				//System.out.println(RRUKSBASE64[i] + "\r\n" + SUKS[i]);
				//System.out.println("****************");
				System.out.println(RRUKSBASE64[i] + "\r\n" + SUKS[i]);
				if(RRUKSBASE64[i].equals(SUKS[i])){
					System.out.println("same");
				}else {
					System.out.println("different");
				}
			}
			
			generateOldRKS(RUKS);
			System.out.println();
			for(int i=0;i<rKSArrayList.size();i++){
				System.out.print("This Round RKS : ");
				if(rKSArrayList.get(i) == null){
					System.out.print("NULL");
				}else {
					for(int j=0;j<rKSArrayList.get(i).length;j++){
						System.out.print(rKSArrayList.get(i)[j] + " " );
					}
					System.out.print( " " + VLMT.LookUpMappingSequence(rKSArrayList.get(i)[0]));
				}
				System.out.println();
			}
			Log.v("Auth", "Auth");
			System.out.println("Go to Authentication");
			authString = Authentication(RUKS);
		}
		else{
			authString = "AAuth:First Time Authentication";
			
		}
		System.out.println("waiting for reply");
		//replyFromServer = "Verified";
		System.out.println("reply from server : " + replyFromServer);
		//generate Mask and Processed_UKS
		
		
		
		//if(true){
			VLMT.update();
			VLMT.initiation();
			Log.v("Verified", "verified");
			
			candidates = new Candidates(OldKeySet, NewKeySet, UKS,VLMT);
			candidates.Generate_Tree();
			candidates.Generate_Mask();//Mask should be save to a file for next round verification
			candidates.Generate_Processed_UKS();//if this round verification passed, UKS should be send to server
			MASK = candidates.Get_MASK();
			Processed_UKS = candidates.Get_Processed_UKS();
			System.out.println("processed uks");
			for(String s:Processed_UKS){
				System.out.println(s);
			}
			candidates.SaveMask2File("//sdcard//tempMASK.txt");
			
			candidates.LoadExistMask("//sdcard//tempMASK.txt");
			candidates.MASKCHECK();
			
			

			rKSArrayList.clear();
			RKS = new RandomKeyStroke();
			RKS.InsertRKSLocalInitiate(0, 0);
	    	RKS.RKSGeneration_Preparation(2);
	    	for(int i=0;i<Processed_UKS.length;i++){
	    		Insert_RKS();
	    	}
	    	generateNewRKS(Processed_UKS);
	    	System.out.println("Next Round RKS");
			for(int i=0;i<rKSArrayList.size();i++){
				System.out.print("RKS : ");
				if(rKSArrayList.get(i) == null){
					System.out.print("NULL");
				}else {
					for(int j=0;j<rKSArrayList.get(i).length;j++){
						System.out.print(rKSArrayList.get(i)[j] + " " );
					}
					System.out.print( " " + VLMT.LookUpMappingSequence(rKSArrayList.get(i)[0]));
				}
				System.out.println();
			}
			Verified = true;
	    	nauthString = NextRoundAuthentication(Processed_UKS);
	    String[] authStrings = {authString,nauthString};
		return authStrings;
		
		
	}
	public boolean isVeriried(){
		return (Verified);
	}
	public boolean isAuthenticationFinished(){
		return AllReceived;
	}
	private String[] ReadProcessedUKS(String FileName) throws IOException{
		BufferedReader fin = new BufferedReader(new FileReader(FileName));
		String aString = null;
		ArrayList<String> tempAL = new ArrayList<String>();
		while((aString = fin.readLine())!=null){
			
			//System.out.println("READ IN :" + aString);
			tempAL.add(aString);
		}
		fin.close();
		String[] aUKS = new String[tempAL.size()];
		for(int i=0;i<tempAL.size();i++){
			aUKS[i] = tempAL.get(i);
		}
		return aUKS;
	}
	public void Save2File(String FileName,String[] Content) throws IOException{
		FileOutputStream fos = new FileOutputStream(new File(FileName));
		for(int i=0;i<Content.length;i++){
			fos.write(Base64Coder.encodeString(Content[i]).getBytes());
			fos.write("\n".getBytes());
		}
		fos.close();
	}
	public void Generate_This_Round_Processed_UKS(){
		
	}
	public String[] GetMASK(){
		return MASK;
	}
	public String[] GetProcessedUKS(){
		return Processed_UKS;
	}
	private void candidates_pre_process(){
		OldKeySet = new String[oldKeySet.length][];
		for(int i=0;i<oldKeySet.length;i++){
			OldKeySet[i] = new String[oldKeySet[i].length()];
			for(int j=0;j<oldKeySet[i].length();j++){
				OldKeySet[i][j] = oldKeySet[i].charAt(j)+"";
			}
		}
		NewKeySet = new String[newKeySet.length][];
		for(int i=0;i<oldKeySet.length;i++){
			NewKeySet[i] = new String[newKeySet[i].length()];
			for(int j=0;j<newKeySet[i].length();j++){
				NewKeySet[i][j] = newKeySet[i].charAt(j)+"";
			}
		}
	}
	public void UpdateRKS(String keyset){
		this.RKS.UpdateRKS_with_UKS(keyset);
	}
	public boolean Insert_RKS() throws NoSuchAlgorithmException{
		if(RKS.InsertRKSLocal() == true){
			String[] tempRKS = RKS.RKS_Determination();
			rKSArrayList.add(tempRKS);
			return true;
		}else {
			rKSArrayList.add(null);
			return false;
		}
		
	}
	public void CheckFriendDeviceStatuBuffer() throws NoSuchAlgorithmException{
		int[] statusBuffer = GetPeerStatusBuffer();
		if(statusBuffer != null){
			for(int i=0;i<statusBuffer.length;i++){
				if((RKS.InsertRKSLocal() == true) && (statusBuffer[i] == -2)){
					String[] tempRKS = RKS.RKS_Determination();
					rKSArrayList.add(tempRKS);
					System.out.println("Key in Peer, RKS insert Local");
				}else if((RKS.InsertRKSLocal() == false) && (statusBuffer[i] == -1)){
					rKSArrayList.add(null);
					System.out.println("Key in Peer, RKS insert Peer");
				}
				uKS_ArrayList.add(-1);
				uKS_Null_Counter++;
			}
		}
	}
	private int[] GetPeerStatusBuffer(){
		int[] status = {};
		return status;
	}

	private String Authentication(String[] RUKS) throws IOException{
		System.out.println("RKS length : " + rKSArrayList.size() + " UKS size : " + uKS_ArrayList.size());
		int RUKS_Index=0;
		ArrayList<String> data2ServerArrayList = new ArrayList<String>();
		//push all ProcessedUKS and RKS to a ArrayList
		for(int i=0;i<rKSArrayList.size();i++){
			if(rKSArrayList.get(i) == null){
				if(uKS_ArrayList.get(i) == -1){
					
				}else {
					data2ServerArrayList.add(RUKS[RUKS_Index]);
					RUKS_Index++;
				}
			}else {
				if(uKS_ArrayList.get(i) == -1){
					for(String s:rKSArrayList.get(i)){
						data2ServerArrayList.add(VLMT.LookUpMappingSequence(s));
					}
					
				}else {
					data2ServerArrayList.add(RUKS[RUKS_Index]);
					for(String s:rKSArrayList.get(i)){
						data2ServerArrayList.add(VLMT.LookUpMappingSequence(s));
					}
					RUKS_Index++;
				}
			}
		}
		
		String sendString = "AAuth:";
		for(int i=0;i<data2ServerArrayList.size() -1;i++){
			sendString = sendString + Base64Coder.encodeString(data2ServerArrayList.get(i)) + ":";
		}
		sendString = sendString + Base64Coder.encodeString(data2ServerArrayList.get(data2ServerArrayList.size()-1));
		return sendString;
	
		
	}
	private String NextRoundAuthentication(String[] Processed_UKS) throws IOException{
		System.out.println("RKS length : " + rKSArrayList.size() + " UKS size : " + uKS_ArrayList.size());
		int RUKS_Index=0;
		ArrayList<String> data2ServerArrayList = new ArrayList<String>();
		//push all ProcessedUKS and RKS to a ArrayList
		for(int i=0;i<rKSArrayList.size();i++){
			if(rKSArrayList.get(i) == null){
				if(uKS_ArrayList.get(i) == -1){
					
				}else {
					data2ServerArrayList.add(Processed_UKS[RUKS_Index]);
					RUKS_Index++;
				}
			}else {
				if(uKS_ArrayList.get(i) == -1){
					for(String s:rKSArrayList.get(i)){
						data2ServerArrayList.add(VLMT.LookUpMappingSequence(s));
					}
					
				}else {
					data2ServerArrayList.add(Processed_UKS[RUKS_Index]);
					for(String s:rKSArrayList.get(i)){
						data2ServerArrayList.add(VLMT.LookUpMappingSequence(s));
					}
					RUKS_Index++;
				}
			}
		}
//		sender.init(ServerIP);
		String sendString = "ANAuth:";
		for(int i=0;i<data2ServerArrayList.size() -1;i++){
			sendString = sendString + Base64Coder.encodeString(data2ServerArrayList.get(i)) + ":";
		}
		sendString = sendString + Base64Coder.encodeString(data2ServerArrayList.get(data2ServerArrayList.size()-1));
		
		return sendString;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(ServerIP);
	}
	class TestThread implements Runnable {
		String Sendout;

		TestThread(String s) {
			Sendout = s;
		}
		
		@Override
		public void run() {
			try {
				//start socket programming
				System.out.println("init socket");
				System.out.println("ServerIP : " + ServerIP);
				Socket client = new Socket(ServerIP, 1234);
				System.out.println("init socket 2");
				PrintStream out = new PrintStream(client.getOutputStream());
				BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
				//send sentence to server
				System.out.println("init socket 3");
				out.print(Sendout);
				System.out.println("init socket 4");
				replyFromServer = buf.readLine();
				client.close();
				

			}
			catch (IOException e)
			{
				System.out.println("An Error Occurs!");
			}
		}

	}

	
}
