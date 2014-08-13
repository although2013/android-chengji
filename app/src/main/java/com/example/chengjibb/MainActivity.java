package com.example.chengjibb;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setTheLastTimeUsernamePassword();
		
	}

	
	public void saveUsernamePassword(String zjh, String mm){
		SharedPreferences sharedPreferences = getSharedPreferences("usernamepassword", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("zjh", zjh);
		editor.putString("mm", mm);
		editor.commit();
	}
	
	public void setTheLastTimeUsernamePassword(){
		EditText xuehao = (EditText)findViewById(R.id.xuehao);
		EditText password = (EditText)findViewById(R.id.password);
		
		SharedPreferences sharedPref = getSharedPreferences("usernamepassword", Context.MODE_PRIVATE);

		String zjh = sharedPref.getString("zjh", "");
		String mm  = sharedPref.getString("mm", ""); 
		xuehao.setText(zjh);
		password.setText(mm);
	}
	
	
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		Bundle bundle = new Bundle();  
		
		EditText ed1 = (EditText)findViewById(R.id.xuehao);
		String zjh = ed1.getText().toString();
		
		EditText ed2 = (EditText)findViewById(R.id.password);
		String mm = ed2.getText().toString();
		
		saveUsernamePassword(zjh,mm);
		

		bundle.putString("zjh", zjh);
		bundle.putString("mm", mm);
		  
		intent.putExtras(bundle);
		  
		startActivity(intent);
		
	}

}