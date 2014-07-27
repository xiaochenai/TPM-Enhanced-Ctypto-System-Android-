package com.master.TPM;

import java.io.IOException;

import com.master.TPM.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class KeyVerify extends Activity {
	
	public EditText editText1;
	public String text;
	public String pass;
	public int i = 0;
	private static String servIP = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_key_verify);
		editText1=(EditText)findViewById(R.id.editText1);
		
		Intent myIntent= getIntent(); 
		servIP = myIntent.getStringExtra("IP"); 
		System.out.println(servIP);
	}

	//send button action: get the password digits and call NetworkService intent to process password and send to server
	public void send(View view) throws IOException{
		text=editText1.getText().toString();
		Intent intent=new Intent(this,NetworkService.class);
		System.out.println(text);
	    intent.putExtra("IP",servIP);
	    intent.putExtra("text", text);
	    intent.putExtra("i", i);
	    System.out.println(intent.toString());
		startService(intent);
		i++;
	}
	
	//finish button action: call Finish intent to tell server password has all been sent and wait for the verification result
	public void finish(View view){
		
        Intent intent=new Intent(this,Finish.class);
	    
	    intent.putExtra("IP",servIP);
	    System.out.println(intent.toString());
		startService(intent);		 
	}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.key_verify, menu);
		return true;
	}
}

