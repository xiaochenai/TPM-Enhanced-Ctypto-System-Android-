package com.master.TPM;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

import com.touchmenotapps.widget.radialmenu.menu.v2.RadialMenuItem;
import com.touchmenotapps.widget.radialmenu.menu.v2.RadialMenuRenderer;
import com.touchmenotapps.widget.radialmenu.menu.v2.RadialMenuRenderer.OnRadailMenuClick;

import BackGround.BackGround;
import KeySetGeneration.KeySetGeneration;
import RandomKeyStroke.RandomKeyStroke;
import Sender_Receiver.Sender;
import VariedLengthMappingTable.VariedLengthMappingTable;
import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Arindam Nath
 *
 */
public class RadialMenuActivity extends FragmentActivity {

	//Variable declarations
	private RadialMenuRenderer mRenderer;
	private FrameLayout mHolderLayout;
	public RadialMenuItem firstItem,secondItem,thirdItem,fourthItem,fifthItem,sixthItem,seventhItem,eighthItem,ninthItem;
	private ArrayList<RadialMenuItem> mMenuItems = new ArrayList<RadialMenuItem>(0);
	public RadialMenuItem[] RadialMenuItemArray;
	public EditText myTextBox;
	public String[] STRINGS = {"R6!%`e[b'AI","m@#g=E^Wd1X","5p{ayZ +j.",";z423qK-7sV","Y:)D~/(o}T","\"M8l9>nht|\\",
            "f_v<k0B&O$","*c?iwruLQx","N,FHJSGUCP]"};
    public String[] NEXTROUNDSTRINGS;
	public VariedLengthMappingTable VLMT;
	private RandomKeyStroke RKS;
    private BackGround BG;
    private String[] Processed_UKS;
    private String[] MASK;
    private long localSeed=0;
    private long peerSeed=0;
    private Button send_Button;
    private String ServerIP;
    private String PCIP;
    private ResponseReceiver receiver;
    private Context context ;
    
    private void graphic_keyboard_initializetion() throws IOException, NoSuchAlgorithmException{
    	KeySetGeneration KSG = new KeySetGeneration();
    	KSG.GenerateKeyset();
		String[][] FinalKeySet = KSG.GetFinalKeySet("//sdcard//Keyset.txt");
		KSG.SaveKeySet2File("//sdcard//Keyset1.txt");
		System.out.println("The Final KeySet is : ");
		for(int i=0;i<FinalKeySet.length;i++){
			for(int j=0;j<FinalKeySet[i].length;j++){
				System.out.print(FinalKeySet[i][j] + " ");
			}
			System.out.println();
		}
		this.STRINGS = new String[FinalKeySet.length];
		String[] temp_stringStrings = new String[FinalKeySet.length];
		for(int i=0;i<FinalKeySet.length;i++){
			StringBuilder sb = new StringBuilder();
			for(int j=0;j<FinalKeySet[i].length;j++){
				if(FinalKeySet[i][j] != null)
					sb.append(FinalKeySet[i][j]);
			}
			temp_stringStrings[i] = sb.toString();
		}
		System.arraycopy(temp_stringStrings, 0, STRINGS, 0, temp_stringStrings.length);
		
		KSG.UpdateKeySet();
		KSG.SaveKeySet2File("//sdcard//Keyset2.txt");
		//KSG.SaveKeySet2File("//sdcard//Keyset.txt");
		KeySetGeneration KSG1 = new KeySetGeneration();
		KSG1.GenerateKeyset();
		FinalKeySet = KSG1.GetFinalKeySet("//sdcard//Keyset2.txt");
		//KSG1.UpdateKeySet();
		//KSG1.SaveKeySet2File("Keyset3.txt");
		System.out.println("Next Round The Final KeySet is : ");
		for(int i=0;i<FinalKeySet.length;i++){
			for(int j=0;j<FinalKeySet[i].length;j++){
				System.out.print(FinalKeySet[i][j] + " ");
			}
			System.out.println();
		}
		temp_stringStrings = new String[FinalKeySet.length];
		this.NEXTROUNDSTRINGS = new String[FinalKeySet.length];
		for(int i=0;i<FinalKeySet.length;i++){
			StringBuilder sb = new StringBuilder();
			for(int j=0;j<FinalKeySet[i].length;j++){
				sb.append(FinalKeySet[i][j]);
			}
			temp_stringStrings[i] = sb.toString();
		}
		System.arraycopy(temp_stringStrings, 0, NEXTROUNDSTRINGS, 0, temp_stringStrings.length);
    }
    public void MappingTableInitilization() throws IOException{
    	this.VLMT = new VariedLengthMappingTable();
    	VLMT.initiation();
    	//VLMT.update();
    }
    public void RKS_Initialization() throws NoSuchAlgorithmException, NoSuchProviderException, IOException{
    	long RKS_Character_Determination_Seed = 2;
    	this.RKS = new RandomKeyStroke();
    	RKS.InsertRKSLocalInitiate(peerSeed, localSeed);
    	RKS.RKSGeneration_Preparation(RKS_Character_Determination_Seed);
    }
    public void ini_MenuItem(){
    	mRenderer = new RadialMenuRenderer(mHolderLayout, true, 40, 300);		
		firstItem = new RadialMenuItem(STRINGS[0],STRINGS[0]);
		secondItem = new RadialMenuItem(STRINGS[1],STRINGS[1]);
		thirdItem = new RadialMenuItem(STRINGS[2],STRINGS[2]);
		fourthItem = new RadialMenuItem(STRINGS[3],STRINGS[3]);
		fifthItem = new RadialMenuItem(STRINGS[4],STRINGS[4]);
		sixthItem = new RadialMenuItem(STRINGS[5],STRINGS[5]);
		seventhItem = new RadialMenuItem(STRINGS[6],STRINGS[6]);
		eighthItem = new RadialMenuItem(STRINGS[7],STRINGS[7]);
		ninthItem = new RadialMenuItem(STRINGS[8],STRINGS[8]);
		//Add the menu Items
		mMenuItems.add(seventhItem);
		mMenuItems.add(eighthItem);
		mMenuItems.add(ninthItem);
		mMenuItems.add(firstItem);
		mMenuItems.add(secondItem);
		mMenuItems.add(thirdItem);
		mMenuItems.add(fourthItem);
		mMenuItems.add(fifthItem);
		mMenuItems.add(sixthItem);
    }
    public void setButtonClickListener(){
    	firstItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(firstItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	secondItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(secondItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	thirdItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(thirdItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	fourthItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(fourthItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	fifthItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(fifthItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	sixthItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(sixthItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	seventhItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(seventhItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	eighthItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(eighthItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	ninthItem.setOnRadialMenuClickListener(new OnRadailMenuClick() {
			public void onRadailMenuClickedListener(String id) {
				myTextBox.append("*");
				BG.Background_Key_Pressed_transit(ninthItem.getMenuName());
				try {
					BG.Insert_RKS();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    	
    }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		context = getApplicationContext();
		Intent myIntent= getIntent(); 
		ServerIP = myIntent.getStringExtra("ServerIP"); 
		PCIP = myIntent.getStringExtra("PCIP");
		super.onCreate(savedInstanceState);
		 try {
			 Class.forName("android.os.AsyncTask");
			graphic_keyboard_initializetion();
			MappingTableInitilization();
			RKS_Initialization();
			System.out.println("a mapped to : " + VLMT.LookUpMappingSequence("a"));
			IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
	        filter.addCategory(Intent.CATEGORY_DEFAULT);
	        receiver = new ResponseReceiver();
	        registerReceiver(receiver, filter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
		BG = new BackGround();
		BG.getIP(ServerIP);
        BG.Background_Parameter_trainsit(STRINGS, NEXTROUNDSTRINGS,VLMT, RKS);
		setContentView(R.layout.layout_holder);
		myTextBox = (EditText) findViewById(R.id.editText2);
		//Init the frame layout
		mHolderLayout = (FrameLayout) findViewById(R.id.fragment_container);

		// Init the Radial Menu and menu items
		ini_MenuItem();
		mRenderer.setRadialMenuContent(mMenuItems);
		mHolderLayout.addView(mRenderer.renderView());
		mHolderLayout.setFadingEdgeLength(TRIM_MEMORY_BACKGROUND);
		//Handle the menu item interactions
		setButtonClickListener();
		
	}
	public void send(View view) throws NoSuchAlgorithmException, IOException, InterruptedException, NoSuchProviderException{
		System.out.println("**********************");
		
//		System.out.println("Go to background_onSubmit Service");
		//new Thread(new TestThread(BG)).start();
		String[] authStrings = BG.SubmitClicked();
		Intent verifyIntent = new Intent(this,NetworkService.class);
		verifyIntent.putExtra("auth", authStrings[0]);
		verifyIntent.putExtra("Nauth", authStrings[1]);
		verifyIntent.putExtra("PCIP", PCIP);
		verifyIntent.putExtra("ServerIP", ServerIP);
		startService(verifyIntent);
		
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.key_verify, menu);
		return true;
	}
	public class ResponseReceiver extends BroadcastReceiver {
		   public static final String ACTION_RESP =    
		      "com.mamlambo.intent.action.MESSAGE_PROCESSED";
		    
		   @Override
		    public void onReceive(Context context, Intent intent) {
		       String verification = intent.getStringExtra(NetworkService.verification);
		       String allreceived = intent.getStringExtra(NetworkService.allreceived);
		       if(verification.equals("Verified") || allreceived.equals("allreceived")){
		    	   
		    	  try {
		    		  System.out.println("update BG");
					BG.VerifiedUpdate();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		       }
		       Intent Nintent=new Intent(context,DisplayMessage.class);
		       Nintent.putExtra("PCIP", PCIP);
			   startActivity(Nintent);
		    }
		}

}
