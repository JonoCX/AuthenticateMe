package uk.ac.ncl.b3026640.authenticateme.misc;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

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
     * From the parameter, query the Monkey Learn classification
     * server to find out the topic for each member of the parameter
     * array.
     * @param textList  Array of Strings that are sourced from the users social media
     *                  account
     * @return          A map of the original String and a JSON array of the potential
     *                  topics.
     */
    public Map<String, JSONArray> detectTopics(String[] textList) {
        Map<String, JSONArray> response = new HashMap<>();
        try {
            response = new SendPost().execute(Arrays.asList(textList)).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     *
     * @param map
     * @return
     */
    public Map<String, Integer> mostFrequentTopics(Map<String, JSONArray> map) {
        Map<String, Integer> result = new HashMap<>();

        for (Map.Entry<String, JSONArray> m: map.entrySet()) {
            String currentKey = m.getKey();
            JSONArray arr = m.getValue();
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = (JSONObject) arr.get(i);
                String label = (String) obj.get("label");
                if (result.containsKey(label)) {
                    result.put(label, result.get(label) + 1);
                }
                else {
                    result.put(label, 1);
                }
            }
        }
        return MapSorter.valueAscending(result);
    }



    private class SendPost extends AsyncTask<List<String>, Void, Map<String, JSONArray>> {

        @Override
        protected Map<String, JSONArray> doInBackground(List<String>... strings) {
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


                // read input stream
                int code = connection.getResponseCode();
                BufferedReader input;
                if (code >= 400 && code <= 500)
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
            return processResponse(response.toString(), (String[]) strings[0].toArray());
        }

        @Override
        protected void onPostExecute(Map<String, JSONArray> result) {
            super.onPostExecute(result);
        }

        /**
         * Process the result from the Monkey Learn API to include
         * the Tweet + the category information into on data object.
         *
         * @param response JSON String response from the API
         * @param feed     Array of Tweets that have been classified
         * @return Tweet -> classification info
         */
        private Map<String, JSONArray> processResponse(String response, String[] feed) {
            Map<String, JSONArray> result = new HashMap<>();

            try {
                JSONParser parser = new JSONParser();
                JSONObject parsedObject = (JSONObject) parser.parse(response);

                // get the json array's of json array's
                JSONArray resultArr = (JSONArray) parsedObject.get("result");

                for (int i = 0; i < resultArr.size(); i++) {
                    JSONArray inner = (JSONArray) resultArr.get(i);
                    result.put(feed[i], inner);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            return result;
        }
    }


}
