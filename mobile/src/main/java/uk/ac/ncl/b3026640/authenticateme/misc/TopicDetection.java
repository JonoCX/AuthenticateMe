package uk.ac.ncl.b3026640.authenticateme.misc;

import android.os.AsyncTask;
import android.util.Log;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

import org.json.simple.JSONArray;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import uk.ac.ncl.b3026640.authenticateme.R;

/**
 * Given a social media text feed this class will
 * find the topic for each of the text strings.
 * <p>
 * Heavily uses the MonkeyLearn API
 * (http://www.monkeylearn.com/)
 *
 * @author Jonathan Carlton
 */

public class TopicDetection {

    String apiKey;

    public TopicDetection(String key) {
        this.apiKey = key;
    }

    /**
     * Takes an array of strings and detects their topics using
     * the monkey learn api.
     *
     * @param textList array of text strings to be classified
     * @return the result of the api call or null
     */
    public JSONArray detectTopics(String[] textList) {
        try {
            new DetectTopics().execute(textList).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        try {
//            MonkeyLearn ml = new MonkeyLearn("d1244a0d6b9245527414635c362a306f08629e6b");
//            String moduleId = "cl_WdTKtSjm";
//            String[] text = {"This is a text to test your classifier", "This is some more text"};
//            MonkeyLearnResponse res = ml.classifiers.classify(moduleId, textList, true);
//            Log.i("Result", res.arrayResult.toJSONString());
////            MonkeyLearn monkeyLearn = new MonkeyLearn(apiKey);
////            Log.i("key", apiKey);
////            String moduleId = "cl_WdTKtSjm";
////            Log.i("textList", "" + textList.length);
////            Log.i("textList", Arrays.toString(textList));
////            MonkeyLearnResponse response = monkeyLearn.classifiers.classify(moduleId, textList, true);
////            return response.arrayResult;
//        } catch (MonkeyLearnException e) {
//            e.printStackTrace();
//        }
        return new JSONArray();
    }

    private class DetectTopics extends AsyncTask<String[], Void, JSONArray> {


        @Override
        protected JSONArray doInBackground(String[]... strings) {
            MonkeyLearn ml = new MonkeyLearn("d1244a0d6b9245527414635c362a306f08629e6b");
            String moduleId = "cl_WdTKtSjm";
            String[] textList = {"This is a text to test your classifier", "This is some more text"};
            MonkeyLearnResponse res = null;
            try {
                res = ml.classifiers.classify(moduleId, textList, true);
            } catch (MonkeyLearnException e) {
                e.printStackTrace();
            }
            Log.i("Result", res.arrayResult.toJSONString());
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
        }
    }

}
