package hugo.alberto.community.Model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.UUID;

/**
 * Data model for a post.
 */

@ParseClassName("Posts")
public class CommunityPost extends ParseObject {
  public String getText() {
    return getString("text");
  }

  public void setText(String value) {
    put("text", value);
  }

  public ParseUser getUser() {
    return getParseUser("user");
  }

  public void setUser(ParseUser value) {
    put("user", value);
  }

  public ParseGeoPoint getLocation() {
    return getParseGeoPoint("location");
  }

  public void setLocation(ParseGeoPoint value) {
    put("location", value);
  }

  public void setAddress(String value) {
    put("address", value);
  }

  public String getAddress() {
    return getString("address");
  }

  public String getFeeling() {
    return getString("feeling");
  }

  public void setFeeling(String feeling) {
    put("feeling", feeling);
  }

  public void setDate(Date date) {
    put("date",date);
  }

  public Date getDate() {
    return getDate("date");
  }

  public void setUuidString() {
    UUID uuid = UUID.randomUUID();
    put("uuid", uuid.toString());
  }

  public java.lang.String getUuidString() {
    return getString("uuid");
  }

  public static ParseQuery<CommunityPost> getQuery() {
    return ParseQuery.getQuery(CommunityPost.class);
  }

}
