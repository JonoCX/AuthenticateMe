package uk.ac.ncl.b3026640.authenticateme.misc;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonathan on 05-Aug-16.
 *
 * The social media feed for the user
 *
 */
@IgnoreExtraProperties
public class Feed {
    private String socialMediaID;
    private List<String> socialFeed;
    private Date dateCollected;

    public Feed() {

    }

    public Feed(String socialMediaID, List<String> socialFeed, Date dateCollected) {
        this.socialMediaID = socialMediaID;
        this.socialFeed = socialFeed;
        this.dateCollected = dateCollected;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("socialMediaID", socialMediaID);
        result.put("socialFeed", socialFeed);
        result.put("dateCollected", dateCollected);
        return result;
    }

    @Override
    public String toString() {
        return "Feed{" +
                "socialMediaID='" + socialMediaID + '\'' +
                ", socialFeed=" + socialFeed +
                ", dateCollected=" + dateCollected +
                '}';
    }

    public Date getDateCollected() {
        return dateCollected;
    }

    public void setDateCollected(Date dateCollected) {
        this.dateCollected = dateCollected;
    }

    public List<String> getSocialFeed() {
        return socialFeed;
    }

    public void setSocialFeed(List<String> socialFeed) {
        this.socialFeed = socialFeed;
    }

    public String getSocialMediaID() {
        return socialMediaID;
    }

    public void setSocialMediaID(String socialMediaID) {
        this.socialMediaID = socialMediaID;
    }
}
