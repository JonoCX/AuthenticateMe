package uk.ac.ncl.b3026640.authenticateme.misc;

import android.os.AsyncTask;
import android.util.Log;

import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

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

    private static final String MONKEY_LEARN_BASE_URL = "https://api.monkeylearn.com/v2/classifiers/cl_5icAVzKR/classify/";
    private static final String MONKEY_LEARN_TRAIN_URL = "https://api.monkeylearn.com/v2/classifiers/cl_WdTKtSjm/train/";


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
    public String detectTopics(String[] textList) throws MonkeyLearnException {
        String response = "";
        try {
            response = new SendPost().execute(Arrays.asList(textList)).get();
            Log.i("response", response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }



    private class SendPost extends AsyncTask<List<String>, Void, String> {

        @Override
        protected String doInBackground(List<String>... strings) {
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL(MONKEY_LEARN_BASE_URL);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                // build request header
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Token " + apiKey);
                connection.setRequestProperty("Content-type", "application/json");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(false);

                // send the request
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(strings[0]);
                jsonObject.put("text_list", jsonArray);
                Log.i("json-object", jsonObject.toJSONString());
                writer.write(jsonObject.toJSONString());
                writer.flush();
                writer.close();

                Log.i("POST", url.toString());
                Log.i("Response code", String.valueOf(connection.getResponseCode()));

                // read input stream
                int code = connection.getResponseCode();
                BufferedReader input;
                if (code == 400 || code == 500)
                    input = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                else
                    input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = input.readLine()) != null)
                    response.append(inputLine);
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

//    private class DetectTopics extends AsyncTask<String[], Void, JSONArray> {
//
//
//        @Override
//        protected JSONArray doInBackground(String[]... strings) {
//            MonkeyLearn ml = new MonkeyLearn("d1244a0d6b9245527414635c362a306f08629e6b");
//            String moduleId = "cl_WdTKtSjm";
//            String[] textList = {"This is a text to test your classifier", "This is some more text"};
//            MonkeyLearnResponse res = null;
//            try {
//                res = ml.classifiers.classify(moduleId, textList, true);
//            } catch (MonkeyLearnException e) {
//                e.printStackTrace();
//            }
//            Log.i("Result", res.arrayResult.toJSONString());
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(JSONArray jsonArray) {
//            super.onPostExecute(jsonArray);
//        }
//    }

}
