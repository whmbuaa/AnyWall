package com.parse.anywall;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.tencent.mapsdk.raster.model.BitmapDescriptorFactory;
import com.tencent.mapsdk.raster.model.GeoPoint;
import com.tencent.mapsdk.raster.model.LatLng;
import com.tencent.mapsdk.raster.model.Marker;
import com.tencent.mapsdk.raster.model.MarkerOptions;
import com.tencent.tencentmap.mapsdk.map.MapView;

public class MainActivityWhm extends FragmentActivity {
	
	// Maximum results returned from a Parse query
    private static final int MAX_POST_SEARCH_RESULTS = 20;

    // Maximum post search radius for map in kilometers
    private static final int MAX_POST_SEARCH_DISTANCE = 100;
    
    private static final int SEARCH_RADIUS = 100;  // 100 KILOMETERS
	  
	  
	private MapView mMapView;
	private Button mBtnPost;
	
	
	//internal state
	private LatLng  mCurrentLocaiotn;
	private LatLng  mLastLocation = new LatLng(39.907937 , 116.398647);
	
	private final Map<String, Marker> mapMarkers = new HashMap<String, Marker>();
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.activity_main_whm);
		mMapView = (MapView)findViewById(R.id.mapview);
		mMapView.onCreate(arg0);
		
		mBtnPost = (Button)findViewById(R.id.post_button);
		mBtnPost.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 2
//			      Location myLoc = (currentLocation == null) ? lastLocation : currentLocation;
//			      if (myLoc == null) {
//			        Toast.makeText(MainActivity.this,
//			            "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
//			        return;
//			      }
				
				
				// whm: test code
			
				  // 3
				  LatLng myLoc = (mCurrentLocaiotn == null)? mLastLocation : mCurrentLocaiotn ;
				  if(myLoc == null){
					  Toast.makeText(MainActivityWhm.this,
					            "Please try again after your location appears on the map.", Toast.LENGTH_LONG).show();
					  return; 
				  }
				
			      Intent intent = new Intent(MainActivityWhm.this, PostActivity.class);
			      Bundle bundle = new Bundle();  
			      bundle.putDouble("longitude", myLoc.getLongitude());
			      bundle.putDouble("latitude", myLoc.getLatitude()); 
			      intent.putExtra(Application.INTENT_EXTRA_LOCATION, bundle);
			      startActivity(intent);
			}
		});
	}
	
	
	
	 /*
	   * Set up the query to update the map view
	   */
	  private void doMapQuery() {
	   
		 LatLng myLoc = (mCurrentLocaiotn == null)? mLastLocation : mCurrentLocaiotn ;
		  
	    // If location info isn't available, clean up any existing markers
	    if (myLoc == null) {
	      cleanUpMarkers(new HashSet<String>());
	      return;
	    }
	    final ParseGeoPoint myPoint = new ParseGeoPoint(myLoc.getLatitude(),myLoc.getLongitude());
	    // Create the map Parse query
	    ParseQuery<AnywallPost> mapQuery = AnywallPost.getQuery();
	    // Set up additional query filters
	    mapQuery.whereWithinKilometers("location", myPoint, MAX_POST_SEARCH_DISTANCE);
	    mapQuery.include("user");
	    mapQuery.orderByDescending("createdAt");
	    mapQuery.setLimit(MAX_POST_SEARCH_RESULTS);
	    // Kick off the query in the background
	    mapQuery.findInBackground(new FindCallback<AnywallPost>() {
	      @Override
	      public void done(List<AnywallPost> objects, ParseException e) {
	        if (e != null) {
	          if (Application.APPDEBUG) {
	            Log.d(Application.APPTAG, "An error occurred while querying for map posts.", e);
	          }
	          return;
	        }
	      
	        // Posts to show on the map
	        Set<String> toKeep = new HashSet<String>();
	        // Loop through the results of the search
	        for (AnywallPost post : objects) {
	          // Add this post to the list of map pins to keep
	          toKeep.add(post.getObjectId());
	          // Check for an existing marker for this post
	          Marker oldMarker = mapMarkers.get(post.getObjectId());
	          // Set up the map marker's location
	          MarkerOptions markerOpts =
	              new MarkerOptions().position(new LatLng(post.getLocation().getLatitude(), post
	                  .getLocation().getLongitude()));
	          // Set up the marker properties based on if it is within the search radius
	          if (post.getLocation().distanceInKilometersTo(myPoint) > SEARCH_RADIUS) {
	            // Check for an existing out of range marker
	            if (oldMarker != null) {
	              if (oldMarker.getSnippet() == null) {
	                // Out of range marker already exists, skip adding it
	                continue;
	              } else {
	                // Marker now out of range, needs to be refreshed
	                oldMarker.remove();
	              }
	            }
	            // Display a red marker with a predefined title and no snippet
	            markerOpts =
	                markerOpts.title(getResources().getString(R.string.post_out_of_range)).icon(
	                    BitmapDescriptorFactory.defaultMarker());
	          } else {
	            // Check for an existing in range marker
	            if (oldMarker != null) {
	              if (oldMarker.getSnippet() != null) {
	                // In range marker already exists, skip adding it
	                continue;
	              } else {
	                // Marker now in range, needs to be refreshed
	                oldMarker.remove();
	              }
	            }
	            // Display a green marker with the post information
	            markerOpts =
	                markerOpts.title(post.getText()).snippet(post.getUser().getUsername())
	                    .icon(BitmapDescriptorFactory.defaultMarker());
	          }
	          // Add a new marker
	          Marker marker = mMapView.addMarker(markerOpts);
	          mapMarkers.put(post.getObjectId(), marker);
	          
	          
//	          if (post.getObjectId().equals(selectedPostObjectId)) {
//	            marker.showInfoWindow();
//	            selectedPostObjectId = null;
//	          }
	        }
	        // Clean up old markers.
	        cleanUpMarkers(toKeep);
	      }
	    });
	  }

	  /*
	   * Helper method to clean up old markers
	   */
	  private void cleanUpMarkers(Set<String> markersToKeep) {
	    for (String objId : new HashSet<String>(mapMarkers.keySet())) {
	      if (!markersToKeep.contains(objId)) {
	        Marker marker = mapMarkers.get(objId);
	        marker.remove();
	        mapMarkers.get(objId).remove();
	        mapMarkers.remove(objId);
	      }
	    }
	  }
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.onDestroy();
		super.onDestroy();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mMapView.onResume();
		super.onResume();
		
		doMapQuery();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mMapView.onPause();
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		mMapView.onStop();
		super.onStop();
	}
}
