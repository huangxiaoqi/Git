package com.huangjiazhong.youlian.http;

import com.huangjiazhong.youlian.entity.Joke;
import com.huangjiazhong.youlian.entity.Result;
import com.huangjiazhong.youlian.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 */
public class JsonParser {


    public static Result getJokes(String jsonString) {
        Result result = new Result();
        int count;
        int currentPage;
        int totalCount;
        List<Joke> jokes = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            count = jsonObject.getInt("count");
            totalCount = jsonObject.getInt("total");
            currentPage = jsonObject.getInt("page");

            JSONArray jsonArray = jsonObject.getJSONArray("items");
            if (jsonArray != null && jsonArray.length() > 0) {
                jokes = new ArrayList<Joke>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Joke joke = new Joke();
                    joke.setImage(jsonArray.getJSONObject(i).getString("image"));
                    joke.setPublished_at(jsonArray.getJSONObject(i).getInt("published_at"));
                    User user = null;
                    Object userObject = jsonArray.getJSONObject(i).get("user");
                    if (userObject != JSONObject.NULL) {
                        JSONObject userJson = jsonArray.getJSONObject(i).getJSONObject("user");
                        if (userJson != null) {
                            user = new User();
                            user.setLogin(userJson.getString("login"));
                            user.setId(userJson.getString("id"));
                            user.setIcon(userJson.getString("icon"));
                        }
                    }
                    joke.setUser(user);
                    joke.setId(jsonArray.getJSONObject(i).getString("id"));
                    joke.setCreated_at(jsonArray.getJSONObject(i).getInt("created_at"));
                    joke.setContent(jsonArray.getJSONObject(i).getString("content"));

                    jokes.add(joke);
                }
            }
            result.setCount(count);
            result.setPage(currentPage);
            result.setTotal(totalCount);
            result.setItems(jokes);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

}
