package com.example.searchcity.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.searchcity.entity.CityEntity;

public class CityDao {

	private CityDatabaseOpenHelper helper;

	private static final String ID = "_id";
	private static final String CITY_NAME = "city_name";
	private static final String CITY_PINYIN = "city_pinyin";
	private static final String CITY_SHORT = "city_short";

	private static final String TABLE_NAME = "city_info";

	public CityDao(Context context) {
		helper = new CityDatabaseOpenHelper(context, 1);
	}

	public void addCity(ArrayList<CityEntity> citys) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		for (CityEntity entity : citys) {
			values.clear();
			values.put(ID, entity.getId());
			values.put(CITY_NAME, entity.getCityName());
			values.put(CITY_PINYIN, entity.getCityPinyin());
			values.put(CITY_SHORT, entity.getShortName());
			db.insert(TABLE_NAME, null, values);
		}
		db.close();
	}

	public ArrayList<CityEntity> getCitysForName(String name) {
		ArrayList<CityEntity> citys = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = null;

		c = db.query(TABLE_NAME, new String[] { ID, CITY_NAME, CITY_PINYIN,
				CITY_SHORT }, CITY_NAME + " like '" + name + "%' or "
				+ CITY_PINYIN + " like '" + name + "%' or " + CITY_SHORT
				+ " like '" + name + "%'", null, null, null, null);
		if (c != null) {
			citys = new ArrayList<CityEntity>();
			while (c.moveToNext()) {
				citys.add(new CityEntity(c.getInt(c.getColumnIndex(ID)), c
						.getString(c.getColumnIndex(CITY_NAME)), c.getString(c
						.getColumnIndex(CITY_PINYIN)), c.getString(c
						.getColumnIndex(CITY_SHORT))));
			}
			c.close();
		}
		db.close();
		return citys;
	}

	public ArrayList<CityEntity> getAllCity() {
		ArrayList<CityEntity> citys = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = null;

		c = db.query(TABLE_NAME, new String[] { ID, CITY_NAME, CITY_PINYIN,
				CITY_SHORT }, null, null, null, null, null);
		if (c != null) {
			citys = new ArrayList<CityEntity>();
			while (c.moveToNext()) {
				citys.add(new CityEntity(c.getInt(c.getColumnIndex(ID)), c
						.getString(c.getColumnIndex(CITY_NAME)), c.getString(c
						.getColumnIndex(CITY_PINYIN)), c.getString(c
						.getColumnIndex(CITY_SHORT))));
			}
			c.close();
		}
		db.close();
		return citys;
	}

	public boolean cityIsExists() {
		boolean isExists = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.query(TABLE_NAME, new String[] { "_id" }, null, null,
				null, null, "1");
		if (c != null && c.moveToFirst()) {
			isExists = true;
		}
		if (c != null) {
			c.close();
		}
		db.close();
		return isExists;
	}
}
