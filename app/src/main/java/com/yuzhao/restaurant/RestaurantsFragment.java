package com.yuzhao.restaurant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.net.Uri;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// A placeholder fragment containing a simple view.
public class RestaurantsFragment extends Fragment
{
	private final String SEARCH_RADIUS = "https://www.restaurant.com/listing?searchradius=";
	private final String SEARCH_ZIP = "&loc=";
	private final String SEARCH_PAGE = "&page=";
	private final String COUNT_ZERO = "No matches found.";
	private final String COUNT_PREFIX = "<span class=\"totalRestaurants\" id=\"lblRestaurantCount\"> ";
	private final String COUNT_SUFFIX = " </span>";
	private final String PAGE_PREFIX = "<label>View restaurants per page: </label>                    <strong>";
	private final String PAGE_SUFFIX = "</strong>";
	private final String NAME_PREFIX = "name=\"RestaurantName\" type=\"hidden\" value=\"";
	private final String NAME_SUFFIX = "\" />";
	private final String TYPE_PREFIX0 = "<div class=\"restaurantImage\">";
	private final String TYPE_SUFFIX0 = "</div>";
	private final String TYPE_PREFIX1 = "<label>        ";
	private final String TYPE_SUFFIX1 = "    </label>";
	private final String LOGO_PREFIX0 = "<div class=\"restaurantImage\">";
	private final String LOGO_SUFFIX0 = "</div>";
	private final String LOGO_PREFIX1 = "<img src=\"";
	private final String LOGO_SUFFIX1 = "\" /></a>";
	private final String ADDR_PREFIX0 = "<div class=\"details\">";
	private final String ADDR_SUFFIX0 = "</p>";
	private final String ADDR_PREFIX1 = "<p>            ";
	private final String ADDR_SUFFIX1 = "<br/>";
	private final String PHONE_PREFIX = "=\"Zip\"/>                <input type=\"hidden\" value=\"";
	private final String PHONE_SUFFIX = "\" name=\"Phone\"/>";
	private final String SCORE_PREFIX = "<span class=\"ratings\">                    <img src=\"";
	private final String SCORE_SUFFIX = "\" alt=\"Restaurant Rating\"";
	private final String RATING_PREFIX0 = "<p class=\"ratingsReviews\">";
	private final String RATING_SUFFIX0 = "</p>";
	private final String RATING_PREFIX1 = "<span class=\"rating\">";
	private final String RATING_SUFFIX1 = "</span>";
	private final String RATING_PREFIX2 = "<span class=\"reviewsoon\">";
	private final String RATING_SUFFIX2 = "</span>";

	public  final int SHOW_PREFERENCES = 1;
	private final String PREF_CHECK_SEARCH = "PREF_CHECK_SEARCH";
	private final String PREF_LIST_RADIUS = "PREF_LIST_RADIUS";
	private final int MESSAGE_INIT_SUCCESS = 6;
	private final int MESSAGE_INIT_ERROR = 7;
	private final int MESSAGE_INIT_NORESULT = 8;
	private WebPage WEB_PAGE;
	private List<ListItem> LIST_DATA;

	// Key for an item which will be put into the instance state Bundle
	private final String KEY_RECOVER_ZIP = "KEY_RECOVER_ZIP";
	private final String KEY_RECOVER_RADIUS = "KEY_RECOVER_RADIUS";
	private final String KEY_RECOVER_SEARCH = "KEY_RECOVER_SEARCH";
	private final String DEFAULT_RADIUS = "5";
	private final String DEFAULT_ZIP = "90025";
	private final int MAX_RESULT_COUNT = 50;
	private String SETTING_RADIUS;
	private String SETTING_ZIP;
	private boolean SETTING_SEARCH;
	ProgressDialog WAIT_DIALOG;

	public RestaurantsFragment()
	{
	}
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		//PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);

		if (savedInstanceState != null)
		{
			SETTING_ZIP	 = savedInstanceState.getString(KEY_RECOVER_ZIP, DEFAULT_ZIP);
			SETTING_SEARCH = savedInstanceState.getBoolean(KEY_RECOVER_SEARCH, true);
			SETTING_RADIUS = savedInstanceState.getString(KEY_RECOVER_RADIUS, DEFAULT_RADIUS);
		}
		else
		{
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
			SETTING_ZIP	 = sp.getString(KEY_RECOVER_ZIP, DEFAULT_ZIP);
			SETTING_SEARCH = sp.getBoolean(PREF_CHECK_SEARCH, true);
			SETTING_RADIUS = sp.getString(PREF_LIST_RADIUS, DEFAULT_RADIUS);
		}
	}
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putString(KEY_RECOVER_ZIP, SETTING_ZIP);
		outState.putBoolean(KEY_RECOVER_SEARCH, SETTING_SEARCH);
		outState.putString(KEY_RECOVER_RADIUS, SETTING_RADIUS);
	}
	@Override
	public void onDestroy()
	{
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		SharedPreferences.Editor ed = sp.edit();
		ed.putString(KEY_RECOVER_ZIP, SETTING_ZIP);
		ed.apply();
		super.onDestroy();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_restaurants, container, false);
	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		// Add menu items to the app bar if it is present.
		inflater.inflate(R.menu.menu_settings, menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.MENU_SEARCH:
				boolean visible = getActivity().findViewById(R.id.CAPTION).getVisibility() == View.VISIBLE;
				getActivity().findViewById(R.id.CAPTION).setVisibility(visible ? View.GONE : View.VISIBLE);
				return true;
			case R.id.MENU_SETTINGS:
				startActivityForResult(new Intent(getActivity(), Settings.class), SHOW_PREFERENCES);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	class InitListRunnable implements Runnable
	{
		@Override
		public void run()
		{
			Message msg = new Message();
			if (WEB_PAGE.InitWithUrl(SEARCH_RADIUS + SETTING_RADIUS + SEARCH_ZIP + SETTING_ZIP) == false)
			{
				msg.what = MESSAGE_INIT_ERROR;
			}
			else if (WEB_PAGE.getUrlPage().indexOf(COUNT_ZERO) >= 0)
			{
				msg.what = MESSAGE_INIT_NORESULT;
			}
			else // Init the list after checking connection.
			{
				LIST_DATA = InitList();
				if (LIST_DATA != null)
					msg.what = MESSAGE_INIT_SUCCESS;
				else
					msg.what = MESSAGE_INIT_ERROR;
			}
			handler.sendMessage(msg);
		}
	}
	private List<ListItem> InitList()
	{
		// Get the total count and result count in every page.
		int count = Integer.parseInt(WEB_PAGE.getTagInfo(null, COUNT_PREFIX, COUNT_SUFFIX, 0));
		if (count > MAX_RESULT_COUNT)
			count = MAX_RESULT_COUNT;
		int psize = Integer.parseInt(WEB_PAGE.getTagInfo(null, PAGE_PREFIX, PAGE_SUFFIX, 0));
		ListItem[] items = new ListItem[count];
		int page = 0;
		for (int i = 0; i < count; i ++)
		{
			items[i] = new ListItem();
			items[i].logo = WEB_PAGE.getTagInfo(WEB_PAGE.getTagInfo(null, LOGO_PREFIX0, LOGO_SUFFIX0, i - psize * page), LOGO_PREFIX1, LOGO_SUFFIX1, 0);
			items[i].name = WEB_PAGE.getTagInfo(null, NAME_PREFIX, NAME_SUFFIX, i - psize * page);
			items[i].type = WEB_PAGE.getTagInfo(WEB_PAGE.getTagInfo(null, TYPE_PREFIX0, TYPE_SUFFIX0, i - psize * page), TYPE_PREFIX1, TYPE_SUFFIX1, 0);
			items[i].addr = WEB_PAGE.getTagInfo(WEB_PAGE.getTagInfo(null, ADDR_PREFIX0, ADDR_SUFFIX0, i - psize * page), ADDR_PREFIX1, ADDR_SUFFIX1, 0);
			items[i].phone = WEB_PAGE.getTagInfo(null, PHONE_PREFIX, PHONE_SUFFIX, i - psize * page);
			items[i].score = WEB_PAGE.getTagInfo(null, SCORE_PREFIX, SCORE_SUFFIX, i - psize * page);
			items[i].rating = WEB_PAGE.getTagInfo(WEB_PAGE.getTagInfo(null, RATING_PREFIX0, RATING_SUFFIX0, i - psize * page), RATING_PREFIX1, RATING_SUFFIX1, 0);
			if (items[i].rating.isEmpty() == true)
				items[i].rating = WEB_PAGE.getTagInfo(WEB_PAGE.getTagInfo(null, RATING_PREFIX0, RATING_SUFFIX0, i - psize * page), RATING_PREFIX2, RATING_SUFFIX2, 0);
			items[i].ilogo = new BitmapDrawable(getActivity().getResources(), WEB_PAGE.getUrlImage(items[i].logo));
			items[i].iscore = new BitmapDrawable(getActivity().getResources(), WEB_PAGE.getUrlImage(items[i].score));
			// Fetch the next web page.
			if (i - psize * page == psize - 1 &&
				WEB_PAGE.InitWithUrl(SEARCH_RADIUS + SETTING_RADIUS + SEARCH_ZIP + SETTING_ZIP + SEARCH_PAGE + (++ page + 1)) == false)
				return null;
		}
		// Convert array to List.
		return new ArrayList<>(Arrays.asList(items));
	}
	private final Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			WAIT_DIALOG.dismiss();
			if (msg.what == MESSAGE_INIT_SUCCESS)
			{
				ListView listView = (ListView)getActivity().findViewById(R.id.LIST_ITEMS);
				if (listView.getAdapter() == null)
				{
					// Set adapter for the list.
					ImageTextArrayAdapter adapter = new ImageTextArrayAdapter(getActivity(), R.layout.listview_layout2, LIST_DATA);
					listView.setAdapter(adapter);
				}
				else
				{
					// Update the list content.
					((ImageTextArrayAdapter)listView.getAdapter()).clear();
					((ImageTextArrayAdapter)listView.getAdapter()).addAll(LIST_DATA);
					((ImageTextArrayAdapter)listView.getAdapter()).notifyDataSetChanged();
				}
			}
			else if (msg.what == MESSAGE_INIT_ERROR)
			{
				Toast.makeText(getActivity(), "Please check the connection.", Toast.LENGTH_SHORT).show();
			}
			else if (msg.what == MESSAGE_INIT_NORESULT)
			{
				Toast.makeText(getActivity(), "No result was found in this area.", Toast.LENGTH_SHORT).show();
			}
		}
	};
	public void hideKeyBoard(View v)
	{
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		WEB_PAGE = new WebPage();

		// Set onclick listener to dial phone number.
		((ListView)getActivity().findViewById(R.id.LIST_ITEMS)).setOnItemClickListener(
			new AdapterView.OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long arg3)
				{
					Intent intent = new Intent();
					String number = "tel:" + LIST_DATA.get(position).phone;
					intent.setAction(Intent.ACTION_DIAL);
					intent.setData(Uri.parse(number));
					startActivity(intent);
				}
			}
		);
		// Set onclick listener to start new search.
		((EditText)getActivity().findViewById(R.id.EDIT_ZIP)).setText(SETTING_ZIP);
		getActivity().findViewById(R.id.CAPTION).setVisibility(SETTING_SEARCH == false ? View.GONE : View.VISIBLE);
		((Button)getActivity().findViewById(R.id.BTN_SEARCH)).setOnClickListener(
			new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					SETTING_ZIP = ((EditText)getActivity().findViewById(R.id.EDIT_ZIP)).getText().toString();
					WAIT_DIALOG = ProgressDialog.show(getActivity(), "", "Loading data……");
					new Thread(new InitListRunnable()).start();
				}
			}
		);
		// Start a search with initial values.
		((Button)getActivity().findViewById(R.id.BTN_SEARCH)).callOnClick();
	}
	@Override
	public void onActivityResult(int reqCode, int resCode, Intent data)
	{
		super.onActivityResult(reqCode, resCode, data);
		switch (reqCode)
		{
			case SHOW_PREFERENCES:
				// Get value of pref controlling show/hide of search bar.
				SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SETTING_SEARCH = sp.getBoolean(PREF_CHECK_SEARCH, SETTING_SEARCH);
				SETTING_RADIUS = sp.getString(PREF_LIST_RADIUS, SETTING_RADIUS);
				getActivity().findViewById(R.id.CAPTION).setVisibility(SETTING_SEARCH == false ? View.GONE : View.VISIBLE);
				getActivity().findViewById(R.id.BTN_SEARCH).callOnClick();
				break;
		}
	}
}
