package com.master.TPM;

//This activity displays the app's file directory and action bar for Encrypt/Decrypt/Reset options
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.master.TPM.R;

import GeneralClass.RND;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DisplayMessage extends ListActivity  {
	public final static String EXTRA_MESSAGE = "com.master.MESSAGE";
	private Menu menu;
	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private String noFiles="There are no files saved to the device.";
	private String PCIP;
	Intent intent=null;
	ArrayAdapter<String> fileList =null;
	byte[] key=null;
	
	// In the onCreate method of the activity, generate the session key to be sent to the PC and populate the ListView that shows the current stored files.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        
        System.out.println(Environment.getExternalStorageState());
        System.out.println(Environment.getExternalStorageDirectory().getPath());
        root = "//sdcard//DCIM//mp";
        
        getDir(root);
        Intent myIntent= getIntent(); 
		PCIP = myIntent.getStringExtra("PCIP");
    }
    
    // The getDir function returns the app's directory of encrypted files and shows them in the Activity. 
    private void getDir(String dirPath)
    {
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{
    		item.add(root);
    		path.add(root);
    		item.add("../");
    		path.add(f.getParent());
    	}
    	
    	for(int i=0; i < files.length; i++)
    	{
    		File file = files[i];
    		
    		if(!file.isHidden() && file.canRead()){
    			path.add(file.getPath());
        		if(file.isDirectory()){
        			item.add(file.getName() + "/");
        		}else{
        			item.add(file.getName());
        		}
        		
    		}	
    	}
    	if(files.length==0){
			item.add(noFiles);
		}

    	fileList = new ArrayAdapter<String>(this, R.layout.row, item);
    	setListAdapter(fileList);	
    }
// This method sets what to do when a file is clicked, currently an alert dialog appears showing the file name. 
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		File file = new File(path.get(position));
		if(file.exists()){
		if (file.isDirectory())
		{
			if(file.canRead()){
				getDir(path.get(position));
			}else{
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "] folder can't be read!")
					.setPositiveButton("OK", null).show();	
			}	
		}else {
			new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "]")
					.setPositiveButton("OK", null).show();

		  }
		}
	}
    
	// This inflates the options menu on the UI.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.display_message, menu);
        
        this.menu=menu;
        return true;
    }

//   This method declares what functions to call when an action bar button is selected.  
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

            case R.id.action_settings:
			try {
				startClient();
				statusChange();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				return true;
            case R.id.decrypt:
            	decrypt();
                return true;
            case R.id.reset:
            	reset();
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
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
            extracted(file);
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
    
    //checks that the entire file was read
    private static void extracted(File file) throws IOException {
    	throw new IOException("Could not completely read file "+file.getName());
    }
        
   
    
    
  // This changes status tab on the action bar from disconnected to connected.
   private void statusChange(){
	   MenuItem status=menu.findItem(R.id.status);
	   status.setTitle("Status: Connected");
	
   }
  // This method starts the Client IntentService and passes the key, the message to encrypt, and the handler that updates the UI. 
    public void startClient() throws NoSuchAlgorithmException{
    	
    	String seed=getResources().getString(R.string.seed);
        RND keygen=new RND();//RND(seed);
        String state =  Environment.getExternalStorageState();
        String path = Environment.getExternalStorageDirectory().toString();
        System.out.println(state);
        System.out.println(path);
        try {
	     key=keygen.getRandom();
	     System.out.print("Key is : " );
	     System.out.println(new String(key));
	     File sessionKey=new File("//sdcard//key.txt");
	      sessionKey.createNewFile();
			FileOutputStream fos = null;
			fos = new FileOutputStream(sessionKey);
			fos.write(key);
			fos.flush();
			fos.close();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			e.toString();
		}
    	
    intent=new Intent(this,ClientService.class);
    String key1 = new String(key);
    System.out.println(key1);
    intent.putExtra("PCIP", PCIP);
    intent.putExtra("key",key);
    intent.putExtra("crypto", "encrypt");
    intent.putExtra("messenger", new Messenger(handler));
    System.out.println(intent.toString());
    System.out.println("Go to Client Service");
	startService(intent);
	
}
    // Starts the Client IntentService and passes the key, the message to decrypt, and the handler that updates the UI. 
   public void decrypt(){
	   byte[] key=null;
	   //key = null;
	   File sessionKey=new File("//sdcard//key.txt");
	   try {
		key = getBytesFromFile(sessionKey);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	   
	   intent=new Intent(this,ClientService.class);
	   intent.putExtra("key",key);
	   intent.putExtra("PCIP", PCIP);
	   intent.putExtra("crypto", "decrypt");
	   intent.putExtra("messenger", new Messenger(handler));
	   startService(intent);
   }
   // When the reset button is pressed on the action bar, this function is called to delete files in the app directory
   // and stop the ClientService and current Activity and go back to the log in activity. 
   public void reset(){
	    File dir=new File("//sdcard//");
		File[] files=dir.listFiles();
		for(int i=0;i<files.length;i++){
		files[i].delete();
		}
		dir = new File("//sdcard//key.txt");
		dir.delete();
		if(intent!=null){
		stopService(intent);
		}
		finish();
		Intent login=new Intent(this,LogIn.class);
		startActivity(login);
   }
  // This is the handler that updates the UI when the message is received from the Client IntentService. 
   Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	            Bundle reply = msg.getData();
	                if(reply.containsKey("Update")){
	                	getDir(root);
	                }
	            }
	};

  }

