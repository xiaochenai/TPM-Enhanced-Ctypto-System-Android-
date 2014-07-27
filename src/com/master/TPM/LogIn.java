package com.master.TPM;

import com.master.TPM.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LogIn extends Activity {

	public EditText editText1;
	public EditText editText2;
	public String text1;
	public String text2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log_in);
		editText1=(EditText)findViewById(R.id.editText1);
		editText2=(EditText)findViewById(R.id.editText2);
		
	}
	
	//get server IP
	public void getIP(View view){
		text1 = editText1.getText().toString();
		System.out.println(text1);
		text2 = editText2.getText().toString();
		System.out.println(text2);
		Intent intent=new Intent(this,RadialMenuActivity.class);
		   intent.putExtra("ServerIP",text1);
		   intent.putExtra("PCIP",text2);
		   startActivity(intent);
		   finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.log_in, menu);
		return true;
	}

	
	

}
