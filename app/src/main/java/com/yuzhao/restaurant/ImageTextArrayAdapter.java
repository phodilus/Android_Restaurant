package com.yuzhao.restaurant;

import android.content.Context;
import java.util.List;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageTextArrayAdapter extends ArrayAdapter
{
	private final static String m_tag = "ImageTextArrayAdapter";
	private int m_resourceid;
	private Context m_context;
	private List<ListItem> m_data;
	private LayoutInflater m_inflater;//method2

	public ImageTextArrayAdapter(Context context, int layoutResourceId, List<ListItem> data)
	{
		super(context, layoutResourceId, data);
		this.m_resourceid = layoutResourceId;
		this.m_context = context;
		this.m_data = data;
		m_inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//method2
	}
	//method2
	private class ItemHolder
	{
		ImageView logo;
		TextView name;
		TextView type;
		TextView addr;
		TextView phone;
		ImageView score;
		TextView rating;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ItemHolder holder = null;
		if (convertView == null)
		{
			Log.d(m_tag, "getView: rowView null: position make new holder" + position);
			convertView = m_inflater.inflate(m_resourceid, parent, false);
			holder = new ItemHolder();
			holder.logo = (ImageView)convertView.findViewById(R.id.IMG_LOGO);
			holder.name = (TextView)convertView.findViewById(R.id.TXT_NAME);
			holder.type = (TextView)convertView.findViewById(R.id.TXT_TYPE);
			holder.addr = (TextView)convertView.findViewById(R.id.TXT_ADDR);
			holder.phone = (TextView)convertView.findViewById(R.id.TXT_PHONE);
			holder.score = (ImageView)convertView.findViewById(R.id.IMG_SCORE);
			holder.rating = (TextView)convertView.findViewById(R.id.TXT_RATING);
			// Tags can be used to store data within a view.
			convertView.setTag(holder);
		}
		else
		{
			Log.d(m_tag, "getView: rowView !null - reuse holder: position " + position);
			holder = (ItemHolder)convertView.getTag();
		}
		// Display the information for that item.
		ListItem item = m_data.get(position);
		holder.logo.setImageDrawable(item.ilogo);
		holder.name.setText(item.name);
		holder.type.setText(item.type);
		holder.addr.setText(item.addr);
		holder.phone.setText(item.phone);
		holder.score.setImageDrawable(item.iscore);
		holder.rating.setText(item.rating);
		return convertView;
	}
	/*method1
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inflater = (LayoutInflater)m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(m_resourceid, parent, false);
		Log.d(m_tag, "getView: rowView null: position " + position);
		ImageView vlogo = (ImageView)rowView.findViewById(R.id.IMG_LOGO);
		TextView vname = (TextView)rowView.findViewById(R.id.TXT_NAME);
		TextView vtype = (TextView)rowView.findViewById(R.id.TXT_TYPE);
		TextView vaddr = (TextView)rowView.findViewById(R.id.TXT_ADDR);
		TextView vphone = (TextView)rowView.findViewById(R.id.TXT_PHONE);
		ImageView vrating = (ImageView)rowView.findViewById(R.id.IMG_RATING);
		TextView vscore = (TextView)rowView.findViewById(R.id.TXT_SCORE);
		vlogo.setImageDrawable(m_data.get(position).logo);
		vname.setText(m_data.get(position).name);
		vtype.setText(m_data.get(position).type);
		vaddr.setText(m_data.get(position).addr);
		vphone.setText(m_data.get(position).phone);
		vrating.setImageDrawable(m_data.get(position).score);
		vscore.setText(m_data.get(position).rating);
		return rowView;
	}*/
}
