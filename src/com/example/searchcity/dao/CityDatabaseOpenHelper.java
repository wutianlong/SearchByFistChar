package com.example.searchcity.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityDatabaseOpenHelper extends SQLiteOpenHelper {

	private static final String CREATE_CITY = "create table city_info(_id integer primary key,city_name text,city_pinyin text,city_short text)";

	public CityDatabaseOpenHelper(Context context, int version) {
		super(context, "city_info", null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CITY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
