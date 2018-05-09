package com.linzb.activity;

import java.security.MessageDigest;

import com.linzb.database.DBHelper;
import com.linzb.database.DBManager;
import com.linzb.model.User;
import com.linzb.udaimaitaskapp.R;
import com.linzb.util.StringUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private Button login;
	private EditText username;
	private EditText password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题拦
		setContentView(R.layout.login);
		
		//添加登录按钮监听
        login = (Button)findViewById(R.id.login_button);
        login.setOnClickListener(ocl);
	}

	
	 //创建按钮监听器对象
    OnClickListener ocl = new OnClickListener(){
		public void onClick(View arg0) {
			username = (EditText)findViewById(R.id.login_account);
			password = (EditText)findViewById(R.id.login_password);
			String loginName = username.getText().toString().trim();
			String pwd = password.getText().toString().trim();
			pwd = StringUtil.md5(pwd);
			
			boolean isValidLoginName = false;
			if("linzb".equals(loginName) && "fae0b27c451c728867a567e8c1bb4e53".equalsIgnoreCase(pwd)){
				isValidLoginName = true;
			}else if("tiger".equals(loginName) && "fae0b27c451c728867a567e8c1bb4e53".equalsIgnoreCase(pwd)){
				isValidLoginName = true;
			}else if("lion".equals(loginName) && "0a113ef6b61820daa5611c870ed8d5ee".equalsIgnoreCase(pwd)){
				isValidLoginName = true;
			}
			
			if(isValidLoginName){
//				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//				intent.putExtra("loginName", username.getText().toString());
				
				User userCond = new User();
				userCond.setLoginName(loginName);
				DBHelper dbh = new DBHelper(LoginActivity.this);
				User tem = DBManager.getInstance(LoginActivity.this).insertUserCond(dbh, userCond);
				if(tem == null){
					Toast.makeText(LoginActivity.this,"保存数据失败" , Toast.LENGTH_SHORT).show();
					return;
				}
				Toast.makeText(LoginActivity.this,"登录成功" , Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(LoginActivity.this, WebActivity.class);
				startActivity(intent);
			}else{
				Toast.makeText(LoginActivity.this,"用户登录信息错误" , Toast.LENGTH_SHORT).show();
			}
		}
    };
	
    
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
}
