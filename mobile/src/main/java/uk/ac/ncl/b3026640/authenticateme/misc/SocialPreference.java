package uk.ac.ncl.b3026640.authenticateme.misc;

/**
 * Created by jonathan on 12/08/2016.
 */

public class SocialPreference {

    private long socialId;
    private String method;

    public SocialPreference(String method, long socialId) {
        this.method = method;
        this.socialId = socialId;
    }

    public long getSocialId() {
        return socialId;
    }

    public void setSocialId(long socialId) {
        this.socialId = socialId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SocialPreference that = (SocialPreference) o;

        return socialId == that.socialId && method.equals(that.method);

    }

    @Override
    public int hashCode() {
        int result = (int) (socialId ^ (socialId >>> 32));
        result = 31 * result + method.hashCode();
        return result;
    }
}
