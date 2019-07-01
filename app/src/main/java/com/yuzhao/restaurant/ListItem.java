package com.yuzhao.restaurant;

import android.graphics.drawable.Drawable;

public class ListItem
{
	public Drawable ilogo;
	public String logo;
	public String name;
	public String type;
	public String cost;
	public String addr;
	public String phone;
	public Drawable iscore;
	public String score;
	public String rating;

	public ListItem() { super(); }
	public ListItem(String logo, String name, String type, String cost, String addr, String phone, String score, String rating)
	{
		super();
		this.logo = logo;
		this.name = name;
		this.type = type;
		this.cost = cost;
		this.addr = addr;
		this.phone = phone;
		this.score = score;
		this.rating = rating;
	}
}
