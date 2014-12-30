package com.example.searchcity.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import com.example.searchcity.R;
import com.example.searchcity.dao.CityDao;
import com.example.searchcity.entity.CityEntity;
import com.example.searchcity.util.PinYinUtil;
import com.example.searchcity.util.StreamUtil;

public class MainActivity extends Activity {

	private CityDao cityDao;
	private PinYinUtil pyUtil;

	private AlertDialog waitDialog;
	private static final int MESSAGE_CITY_LOADED = 1;
	ArrayList<CityEntity> citys = new ArrayList<CityEntity>();
	ArrayList<CityEntity> mAllCitys = null;

	/** 获取外部数据库中的城市列表，并将其存入新表中（如果有服务端支持，应该将获取城市计算拼音的逻辑放入服务端，并提供获取城市列表及对应拼音的接口） */
	@SuppressLint("HandlerLeak")
	private void initCityList() {
		cityDao = new CityDao(this);
		if (cityDao.cityIsExists()) {
			mAllCitys = cityDao.getAllCity();
			return;
		}
		waitDialog = new AlertDialog.Builder(this)
				.setTitle("提醒")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setMessage(
						"正在加载数据，请稍后\n初次加载比较耗时，需要2-4分钟左右。再次启动就不会有任何问题。请亲耐心等待哦")
				.setCancelable(false).create();
		waitDialog.show();
		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == MESSAGE_CITY_LOADED) {
					waitDialog.cancel();
				}
			};
		};
		// 初次加载比较耗时，所以需要新启动线程完成加载，并通过对话框告知用户。这段逻辑最好放到服务端，这里只是为了实现功能
		new Thread() {
			public void run() {
				pyUtil = new PinYinUtil();

				String dbFileName = "weather_info";
				File file = new File("data/data/" + getPackageName()
						+ "/databases/" + dbFileName);
				try {
					// 从外部资源文件夹中获取城市数据库，并将其保存到数据库文件夹中
					StreamUtil.getInputStream(getAssets().open(dbFileName),
							new FileOutputStream(file));
					SQLiteDatabase db = openOrCreateDatabase(dbFileName,
							Context.MODE_PRIVATE, null);
					Cursor c = db.query("city_info", new String[] { "_id",
							"city_name" }, null, null, null, null, null);
					if (c != null) {
						// 查询外部数据库中的城市数据，并在新构建的数据库中将原始数据和对应的拼音/首字母也保存到数据库中，待后续查询使用
						while (c.moveToNext()) {
							int id = c.getInt(c.getColumnIndex("_id"));
							String cityName = c.getString(c
									.getColumnIndex("city_name"));
							citys.add(new CityEntity(id, cityName, pyUtil
									.getStringPinYin(cityName), pyUtil
									.getFirstSpell(cityName)));
						}
						c.close();
					}
					db.close();
					cityDao.addCity(citys);
					citys.clear();
					handler.sendEmptyMessage(MESSAGE_CITY_LOADED);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	private SearchView sv;
	private ListView lv;

	private void initView() {
		sv = (SearchView) findViewById(R.id.searchview);
		lv = (ListView) findViewById(R.id.listview);
	}

	private ArrayAdapter<CityEntity> cityAdapter;

	private void setAdapter() {
		// 通过集合citys中每个元素CityEntity的toString()方法决定文本显示内容
		cityAdapter = new ArrayAdapter<CityEntity>(this,
				android.R.layout.simple_list_item_1, citys);
		lv.setAdapter(cityAdapter);
	}

	private void addListener() {

		// 为搜索框添加搜索文字的监听器
		sv.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {

				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				if (newText == null || newText.length() == 0) {
					ArrayList<CityEntity> allCitys = cityDao.getAllCity();
					update(allCitys);
				} else {
					ArrayList<CityEntity> newCitys = cityDao
							.getCitysForName(newText);
					update(newCitys);
				}
				return true;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(MainActivity.this,
						"选中城市为:" + citys.get(position).getCityName(),
						Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void update(ArrayList<CityEntity> newCitys) {
		citys.clear();
		if (newCitys != null && newCitys.size() != 0) {
			citys.addAll(newCitys);
		}
		if (cityAdapter == null)
			setAdapter();
		cityAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initCityList();
		initView();
		setAdapter();
		addListener();
		if (mAllCitys != null)
			update(mAllCitys);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
