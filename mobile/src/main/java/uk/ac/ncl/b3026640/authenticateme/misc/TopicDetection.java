package uk.ac.ncl.b3026640.authenticateme.misc;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

import org.json.simple.JSONArray;

import uk.ac.ncl.b3026640.authenticateme.R;

/**
 * Given a social media text feed this class will
 * find the topic for each of the text strings.
 *
 * Heavily uses the MonkeyLearn API
 * (http://www.monkeylearn.com/)
 *
 * @author Jonathan Carlton
 */

public class TopicDetection {

    String apiKey;

    public TopicDetection(String key) { this.apiKey = key; }

    /**
     * Takes an array of strings and detects their topics using
     * the monkeylearn api.
     * @param textList  array of text strings to be classified
     * @return  the result of the api call or null
     */
    public JSONArray detectTopics(String[] textList) {
        MonkeyLearn monkeyLearn = new MonkeyLearn(apiKey);
        String moduleId = "cl_WdTKtSjm";
        MonkeyLearnResponse response = null;
        try {
            response = monkeyLearn.classifiers.classify(
                    moduleId,
                    textList,
                    true
            );
        } catch (MonkeyLearnException e) {
            e.printStackTrace();
        }
        return response.arrayResult;
    }

}
