package com.parse.anywall;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;

/**
 * Data model for a post.
 */
@AVClassName("Posts")
public class AnywallPost extends AVObject {
  public String getText() {
    return getString("text");
  }

  public void setText(String value) {
    put("text", value);
  }

  public AVUser getUser() {
    return getAVUser("user");
  }

  public void setUser(AVUser value) {
    put("user", value);
  }

  public AVGeoPoint getLocation() {
    return getAVGeoPoint("location");
  }

  public void setLocation(AVGeoPoint value) {
    put("location", value);
  }

  public static AVQuery<AnywallPost> getQuery() {
    return AVQuery.getQuery(AnywallPost.class);
  }
}
