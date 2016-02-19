package com.parse.anywall;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.avos.avoscloud.AVException;

public class ConfigHelper {


  public void fetchConfigIfNeeded() {
    
  }

  public List<Float> getSearchDistanceAvailableOptions() {
    final List<Float> defaultOptions = Arrays.asList(250.0f, 1000.0f, 2000.0f, 5000.0f);

    return defaultOptions; 
  }

  public int getPostMaxCharacterCount () {
//    int value = config.getInt("postMaxCharacterCount", 140);
//    return value;
	  return 140;
  }
}
