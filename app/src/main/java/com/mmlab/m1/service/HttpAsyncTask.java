package com.mmlab.m1.service;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.mmlab.m1.MainActivity;
import com.mmlab.m1.MyApplication;
import com.mmlab.m1.R;
import com.mmlab.m1.network.ProxyService;
import com.mmlab.m1.widget.AboutDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

public class HttpAsyncTask extends AsyncTask<String, Integer, String> {
	private TaskCompleted mCallback;
	private String mType,account,password,AToken;
	private int mWhat;
	private HttpURLConnection connection;
	private int count;
	private String desireUrl;
//	private MaterialDialog mProgress;

	public HttpAsyncTask(TaskCompleted callback, String type,int what) {
		this.mType = type;
		this.mCallback = callback;
		this.mWhat = what;
	}
	public HttpAsyncTask(TaskCompleted callback,String type,int what,String account,String password,String AToken){
		this.mType = type;
		this.mCallback = callback;
		this.mWhat = what;
		this.account = account;
		this.password = password;
		this.AToken = AToken;
	}public HttpAsyncTask(TaskCompleted callback, String type,int what,String AToken) {
		this.mType = type;
		this.mCallback = callback;
		this.mWhat = what;
		this.AToken = AToken;
	}
	public HttpAsyncTask(String type,int what,String AToken) {
		this.mType = type;
		this.mWhat = what;
		this.AToken = AToken;
	}

	@Override
	public void onPreExecute() {
//		mProgress = new MaterialDialog.Builder(mContext)
//				.content(R.string.please_wait)
//				.progress(true, 0)
//				.progressIndeterminateStyle(false)
//				.cancelable(false)
//				.show();
	}

	@Override
	protected void onProgressUpdate(Integer... params) {
		super.onProgressUpdate(params);


	}

	@Override
	protected String doInBackground(String... params) {
		String response = null;
			try {
				response = doHttpUrlConnectionAction(params[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return response;
	}

	private String doHttpUrlConnectionAction(String desiredUrl)
			throws Exception
	{
		desireUrl = desiredUrl;
		BufferedReader reader = null;
		StringBuilder stringBuilder;
		//count = count +1;

		try
		{
			//if(count < 2) {// create the HttpURLConnection
				URL url = new URL(desiredUrl);
				connection = (HttpURLConnection) url.openConnection();

				// just want to do an HTTP GET here
				if (mWhat == 2) {
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					String mb5pw = md5(password);
					String postdata = "{\"username\": \"" + account + "\",\"password\": \"" + mb5pw + "\"}";
					connection.setRequestProperty("Content-length", postdata.getBytes().length + "");
					connection.setRequestProperty
							("Authorization", "Token " + AToken);
					byte[] outputInBytes = postdata.getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write(outputInBytes);
					os.close();
				} else if (mWhat == 3) {
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					String mb5pw = md5(password);
					String postdata = "{\"username\": \"" + account + "\",\"password\": \"" + mb5pw + "\"}";
					connection.setRequestProperty("Content-length", postdata.getBytes().length + "");
					connection.setRequestProperty
							("Authorization", "Token " + AToken);
					byte[] outputInBytes = postdata.getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write(outputInBytes);
					os.close();
				} else if (mWhat == 10) {
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Type", "application/json");
					account = "mmlabtest";
					password = "mmlabtest";
					String mb5pw = md5(password);
					String postdata = "{\"username\": \"" + account + "\",\"password\": \"" + mb5pw + "\"}";
					connection.setRequestProperty("Content-length", postdata.getBytes().length + "");
					byte[] outputInBytes = postdata.getBytes("UTF-8");
					OutputStream os = connection.getOutputStream();
					os.write(outputInBytes);
					os.close();
				} else {
					connection.setRequestMethod("GET");
					//connection.setRequestProperty("Content-Type", "application/json");
					connection.setRequestProperty
							("Authorization", "Token " + AToken);
				}
			//}
			// uncomment this if you want to write output to this url
			//connection.setDoOutput(true);

			// give it 6 seconds to respond
			connection.setReadTimeout(6*1000);
			connection.setConnectTimeout(3000);

			Log.d("count", "" + count);
			connection.connect();
			Log.d("count2", "" + count);
			//if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {  // read the output from the server
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				//count = 0;
			//}

				stringBuilder = new StringBuilder();

				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line).append("\n");
				}

				return stringBuilder.toString();

		}
		catch (SocketTimeoutException e)
		{
			Log.d("ex", "connectexception");
			connection.disconnect();
			//if(count > 4 ){
				ProxyService proxyService = new ProxyService();
				proxyService.sendException();
				/*count = 0;
			} else
				doHttpUrlConnectionAction(desireUrl);*/
			throw e;
		}
		catch (IOException e)
		{
			Log.d("ex", "readexception");
			connection.disconnect();
			//if(count > 4 ){
				ProxyService proxyService = new ProxyService();
				proxyService.sendException();
				/*count = 0;
			} else
				doHttpUrlConnectionAction(desireUrl);*/
			throw  e;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
		finally
		{
			// close the reader; this can throw an exception too, so
			// wrap it in another try/catch block.
			if (reader != null)
			{
				try {
					reader.close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}

	/*public void retransmit() throws Exception{
		globalVariable.setCount(globalVariable.getCount() + 1);
		connection.disconnect();
		try{
			connection.connect();
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {  // read the output from the server
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				globalVariable.setCount(0);
			}
			stringBuilder = new StringBuilder();


			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line).append("\n");
			}

			return stringBuilder.toString();
		} catch (SocketTimeoutException e)
		{
			retransmit();
			throw e;
		}
	}*/

	@Override
	protected void onPostExecute(String response) {
//		mProgress.dismiss();
		if (response != null){
			Log.d("response",response);
			mCallback.onTaskComplete(response, mType);}

	}
	public static final String md5(final String s) {
		final String MD5 = "MD5";
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance(MD5);
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuilder hexString = new StringBuilder();
			for (byte aMessageDigest : messageDigest) {
				String h = Integer.toHexString(0xFF & aMessageDigest);
				while (h.length() < 2)
					h = "0" + h;
				hexString.append(h);
			}
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
