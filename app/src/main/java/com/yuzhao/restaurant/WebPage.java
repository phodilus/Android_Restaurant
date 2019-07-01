package com.yuzhao.restaurant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class WebPage
{
	public WebPage() { super(); }
	public WebPage(String addr)
	{
		super();
		InitWithUrl(addr);
	}
	private String page = null;
	public boolean InitWithUrl(String addr)
	{
		try
		{
			// Initialize connection.
			URL url = new URL(addr);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();

			// Optional: set time out.
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);
			//connection.setRequestMethod("GET");???

			// Optional: simulate user agent of Chrome rather than smartphone, to get the original web page.
			//connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko");
			//connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1");
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)");
			//connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36");
			// Optional: set automatic redirection.
			connection.setInstanceFollowRedirects(true);

			// If responsed by HTTP_OK=200, that means the web page has been successfully fetched.
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK)
			{
				//connection.connect();???
				InputStream stream = connection.getInputStream();

				/*/ Option 1: read content to file.
				File file = new File(filename);
				FileOutputStream fos = new FileOutputStream(file);
				int byte;
				while ((byte = stream.read()) != - 1)
					fos.write(byte);
				fos.close();*/

				/*/ Option 2: read content with output stream.
				byte[] buffer = new byte[1024];
				ByteArrayOutputStream ostream = new ByteArrayOutputStream();
				while (stream.read(buffer) != - 1)
					ostream.write(buffer);
				page = new String(ostream.toByteArray(), "gbk");//(or utf-8)
				ostream.close();*/

				// Option 3: read content with buffer reader.
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
				StringBuffer sbuffer = new StringBuffer();
				String temp;
				while ((temp = reader.readLine()) != null)
					sbuffer.append(temp);
				reader.close();

				/*/ Optional: Get the type of current web page. (whether it is a stream file)
				String mime = connection.getContentType();
				boolean isMediaStream = false;
				if (mime.indexOf("audio") == 0 || mime.indexOf("video") == 0)
					isMediaStream = true;*/

				// Convert the web page content.
				page = sbuffer.toString();
				stream.close();
			}
			connection.disconnect();
			return true;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public Bitmap getUrlImage(String addr)
	{
		Bitmap bmp = null;
		try
		{
			URL url = new URL(addr);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//connection.connect();
			InputStream is = connection.getInputStream();
			bmp = BitmapFactory.decodeStream(is);
			//connection.disconnect();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bmp;
	}
	public String getUrlPage()
	{
		return page;
	}
	public String getTagInfo(String context, String prefix, String suffix, int index)
	{
		if (context == null)
			context = page;
		if (context.indexOf(prefix) < 0)
		{
			context = "";
			return context;
		}
		while (index >= 0)
		{
			context = context.substring(context.indexOf(prefix) + prefix.length());
			index --;
		}
		return context.substring(0, context.indexOf(suffix));
	}
}