package com.master.TPM;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import BackGround.BackGround;
import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class background_onSubmit extends IntentService{
	private BackGround bg;
	public background_onSubmit(){
		super("background_onSubmit");
		
	}
	public background_onSubmit(String name) {
        super(name);
    }
	public void onCreate(){
    	super.onCreate();
    	System.out.println("create service background_onSubmit");
    	try {
			Class.forName("android.os.AsyncTask");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//editText1=(EditText)findViewById(R.id.editText1);
    	Toast.makeText(this,"Connecting...", Toast.LENGTH_LONG).show();
    }
	@Override
	public void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("Enter background_onSubmit Service");
		BackGround BG = (BackGround)intent.getParcelableExtra("BackGround");
		System.out.println("Get BG");
		try {
			bg.SubmitClicked();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
