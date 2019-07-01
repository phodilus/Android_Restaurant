package com.yuzhao.restaurant;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Restaurants extends AppCompatActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurants);
		Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
				}
			}
		);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			//It gets into the above IF-BLOCK if anywhere the screen is touched.
			View view = getCurrentFocus();
			if (view instanceof EditText)
			{
				// Now, it gets into the above IF-BLOCK if an EditText is already in focus, and you tap somewhere else
				// to take the focus away from that particular EditText. It could have 2 cases after tapping:
				// 1. No EditText has focus
				// 2. Focus is just shifted to the other EditText
				Rect rt = new Rect();
				view.getGlobalVisibleRect(rt);
				if (!rt.contains((int)event.getRawX(), (int)event.getRawY()))
				{
					view.clearFocus();
					InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		}
		return super.dispatchTouchEvent(event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.menu_restaurants, menu);
		//getMenuInflater().inflate(R.menu.menu_items, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will automatically handle clicks on
		// the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
