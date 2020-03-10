package com.example.trivia.data;

import com.example.trivia.model.Question;

import java.util.List;

public interface AnswerListAyncTask {

    void processFinished(List<Question> questionList);

}
