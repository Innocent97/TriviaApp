package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {

    private String URL = "https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";

    ArrayList<Question> fQuestionArrayList = new ArrayList<>();

    public List<Question> getQuestions(final AnswerListAyncTask ayncTask){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        // loop through the json array
                        for ( int i=0; i <response.length(); i++ ){

                            try {

                                Question question = new Question();

                                question.setAnswer(response.getJSONArray(i).get(0).toString());
                                question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));

                                // add each question to the list
                                fQuestionArrayList.add(question);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if ( ayncTask != null )
                            ayncTask.processFinished(fQuestionArrayList);


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return fQuestionArrayList;
    }
}
