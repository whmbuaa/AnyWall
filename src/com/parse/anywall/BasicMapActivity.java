package com.parse.anywall;

import android.os.Bundle;

import com.tencent.tencentmap.mapsdk.map.MapActivity;
import com.tencent.tencentmap.mapsdk.map.MapView;

public class BasicMapActivity extends MapActivity {
	MapView mMapView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basic_map);
		mMapView = (MapView)findViewById(R.id.map);
		//mMapView.onCreate(savedInstanceState);//如果没有继承MapActivity，则必须调用
	}
}
