package com.example.chengjibb;


import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
	

	private InputStream GetPage(String urlString,String myCookie) throws IOException {
		InputStream in = null;
		
		int response = -1;
		               
		URL url = new URL(urlString); 
		URLConnection conn = url.openConnection();
		                 
		if (!(conn instanceof HttpURLConnection))                     
		    throw new IOException("Not an HTTP connection");        
		    try{
		        HttpURLConnection httpConn = (HttpURLConnection) conn;
		        httpConn.setAllowUserInteraction(false);
		        httpConn.setInstanceFollowRedirects(true);
		        httpConn.setRequestMethod("GET");
		        httpConn.setRequestProperty("Cookie", myCookie);
		        
		        httpConn.connect();
		        response = httpConn.getResponseCode();
		        if (response == HttpURLConnection.HTTP_OK) {
		            in = httpConn.getInputStream(); 
		            
		        }                     
	         }
	          catch (Exception ex)
		      {
		        
		        throw new IOException("Error connecting");
		      }
		return in;     
	}
	
	private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "GB2312"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "GB2312"));
	    }

	    return result.toString();
	}
	
	private String post_get_cookie(String urlString,String zjh, String mm) throws IOException {
		String cookie = null;
		URL url = new URL(urlString); 
		        
		    try{
	    		
	    		HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
	    		
	    		List<NameValuePair> params = new ArrayList<NameValuePair>();
	    		params.add(new BasicNameValuePair("zjh", zjh));
	    		params.add(new BasicNameValuePair("mm", mm));
	            
	            httpURLConnection.setConnectTimeout(12000);
	            httpURLConnection.setDoInput(true);        
	            httpURLConnection.setDoOutput(true);        
	            httpURLConnection.setRequestMethod("POST");   
	            httpURLConnection.setUseCaches(false);            
	            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	            httpURLConnection.setAllowUserInteraction(false);
	            httpURLConnection.setInstanceFollowRedirects(true);
	            
	            httpURLConnection.connect();
	            
	            OutputStream outputStream = httpURLConnection.getOutputStream();
	            
	            BufferedWriter writer = new BufferedWriter(
	            new OutputStreamWriter(outputStream, "GB2312"));
	            writer.write(getQuery(params));
	            writer.flush();
	            writer.close();
	            outputStream.close();
	            
	            cookie = httpURLConnection.getHeaderField("Set-Cookie");
	            //InputStream in = httpURLConnection.getInputStream();
		        
		        httpURLConnection.disconnect();
		                       
	          } catch (Exception ex)
	    		{    
	    		    throw new IOException("Error connecting");
	    		}

	return cookie;     
	}
	
	
	public static String InputStreamTOString(InputStream in) throws Exception{  
		int BUFFER_SIZE = 4096;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        byte[] data = new byte[BUFFER_SIZE];  
        int count = -1;  
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
            outStream.write(data, 0, count);  
        data = null;  
        return new String(outStream.toByteArray(),"GB2312"); //ISO-8859-1 
    }  
	
	
	
	public String DealHtml(String str){
		ProgressBar mProgress = (ProgressBar)findViewById(R.id.myProgressBar);
		mProgress.setProgress(37);
		str = str.replaceAll("\\s","");
		//next line delete useless info of html head&tail
        if (str.length() > 1426 && str.length() < 3433){
            return null;
        }
        int useful_end = str.indexOf("</TABLE>");
		str = str.substring(1425, useful_end);
		//System.out.println(str);

		mProgress.setProgress(49);
		str = str.replaceAll("<tdalign=\"center\">","");
		str = str.replaceAll("</td>",",");
		mProgress.setProgress(52);
		str = str.replaceAll("<trclass=\"odd\"onMouseOut=\"this.className='even';\"onMouseOver=\"this.className='evenfocus';\">","");
		str = str.replaceAll("</tr>","");
		mProgress.setProgress(55);

		int count = 1;
        Log.w("cccc", str);
		String[] buf = str.split(",");
		StringBuffer sb = new StringBuffer();
		mProgress.setProgress(57);
        if (buf.length % 7 != 0 ){
            str = str.concat("N");
            buf = str.split(",");
            Log.w("cccc++++++", str);
        }
        Log.w("cccc", String.valueOf(buf.length));
		for(int i=0;i<buf.length;i++){
            Log.w("ccc-single","No."+Integer.toString(i)+" buf: "+buf[i]);
			mProgress.setProgress(60+i/5);
			if (count == 8){count = 1;}
			if (count == 3){
				sb.append(buf[i]);
				sb.append(" ");
			}
			if (count == 7){
                if (buf[i].length() < 1) {
                    sb.append("N");
                }else {
                    sb.append(buf[i]);
                }
				sb.append(" ");
			}
			count++;
		}
		str = sb.toString();
		return str;
	}
	
	public String getRealName(String cookie){
		String str = null;
		try {
			InputStream page_realname = GetPage(getString(R.string.get_real_name), cookie);
			str = InputStreamTOString(page_realname);
		} catch(Exception e){

		}

		str = str.replaceAll("\\s","");
		str = str.replaceAll(".*欢迎光临&nbsp;","");
		str = str.replaceAll("&nbsp;\\|&nbsp;.*","");

		return str;
	}
	
	public class DownloadTextTask extends AsyncTask<String, Void, String>{

		protected String doInBackground(String... parms) {
			ProgressBar mProgress = (ProgressBar)findViewById(R.id.myProgressBar);
			
			String cookie = null;
			String marks_str = null;
			
			String marks_value = null;
			String realName = null;
			try {
				cookie = post_get_cookie(getString(R.string.the_login_address),parms[0],parms[1]);
					mProgress.setProgress(15);
				InputStream page_marks = GetPage(getString(R.string.this_term_marks_address), cookie);
					mProgress.setProgress(25);
				marks_str = InputStreamTOString(page_marks);
                    mProgress.setProgress(32);
                if (marks_str.length() > 280 && marks_str.length() < 295) {
                    return "passworderror";
                }
				marks_value = DealHtml(marks_str);
				if (marks_str == null || marks_value == null) {
					return "fetcherror";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			realName = getRealName(cookie);
				mProgress.setProgress(87);
	
			String marks_and_realname = marks_value + "," + realName;
			return marks_and_realname;
		
		}

		public void onPostExecute(String result) {
			/*>>>>>>>>>>>>>>>>>>>>>>>>>>>*/
			if (result == "fetcherror") {
				fetchErrorDialog();
				
			} else if (result == "passworderror") { /*Wrong password*/
				wrongPasswordDialog();

			/*>>>>>>>>>>>>>>>>>>>>>>>>>>>*/
			} else {
				String[] arr = result.split(",");
                Log.w("cccc", result);
				String realName = arr[1];
                Log.w("cccc",realName);
				ProgressBar mProgress = (ProgressBar)findViewById(R.id.myProgressBar);
				mProgress.setVisibility(View.GONE);
				ListView list = (ListView) findViewById(R.id.listView);
				TextView textView_name = (TextView)findViewById(R.id.realname);

				textView_name.setText(realName);
				textView_name.setBackgroundColor(0x231111);
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
                String[] sa = arr[0].split(" ");

                int sa_length = sa.length;
                for(int i = 0; i < sa_length; i = i + 2)
                {
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put("ItemTitle",sa[i]);
                    map.put("ItemText",sa[i+1]);

                    mylist.add(map);
                }


                Log.w("cccc","ishere? 222");
				SimpleAdapter mSchedule = new SimpleAdapter(DisplayMessageActivity.this, mylist, R.layout.listitem, new String[] {"ItemTitle", "ItemText"}, new int[] {R.id.ItemTitle,R.id.ItemText});  
				list.setAdapter(mSchedule);
                Log.w("cccc","ishere? 333");
			}
		}

	}
	
	
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		
		ProgressBar mProgress = (ProgressBar)findViewById(R.id.myProgressBar);
		mProgress.setIndeterminate(false);

		if ( isNetworkAvailable(this) ) {
			mProgress.setVisibility(View.VISIBLE);
			mProgress.setProgress(0);
			
			Intent intent = getIntent();
	        Bundle bundle = intent.getExtras();
	          
	        String zjh = bundle.getString("zjh");
	        String mm = bundle.getString("mm");
	        
	        DownloadTextTask task = new DownloadTextTask();
			task.execute(zjh, mm);

		} else {
			//Network Unavailable, pop a notice
			dialog();
		}
	}
	
	
	
	protected void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("网络异常，请确认网络连接后重试。");
		builder.setTitle("网络连接异常");
		builder.setPositiveButton("确认", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DisplayMessageActivity.this.finish();
			}
		});
		builder.create().show();
	}
	
	protected void fetchErrorDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("很抱歉，无法您的获取成绩。");
		builder.setTitle("错误");
		builder.setPositiveButton("确认", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DisplayMessageActivity.this.finish();
			}
		});
		builder.create().show();
	}
	protected void wrongPasswordDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage("您输入的账号或密码错误，请确认后重试。");
		builder.setTitle("账号或密码有误");
		builder.setPositiveButton("确认", new OnClickListener() {	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				DisplayMessageActivity.this.finish();
			}
		});
		builder.create().show();
	}	

}
