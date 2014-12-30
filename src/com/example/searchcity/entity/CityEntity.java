package com.example.searchcity.entity;

public class CityEntity {
	private int id;

	private String cityName;

	private String cityPinyin;

	private String shortName;

	public CityEntity(int id, String cityName, String pinyin, String shortName) {
		this.id = id;
		this.cityName = cityName;
		this.cityPinyin = pinyin;
		this.shortName = shortName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getCityPinyin() {
		return cityPinyin;
	}

	public void setCityPinyin(String cityPinyin) {
		this.cityPinyin = cityPinyin;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	// toString()方法决定显示在Adapter上面的为单个城市名字 ，没有拼音等信息
	@Override
	public String toString() {
		return cityName;
	}

}
