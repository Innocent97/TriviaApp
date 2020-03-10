package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trivia.data.AnswerListAyncTask;
import com.example.trivia.data.QuestionBank;
import com.example.trivia.model.Question;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // global fields
    private TextView fTextViewScore, fTextViewQuestion, fTextViewUserScore, fTextViewHighestScore;
    private int currentQuestionIndex = 0;
    private List<Question> fQuestionList;
    private int fUserCorrectScore;
    private int fUserIncorrectScore;
    private int fTotalScore;
    private String MESSAGES_ID = "message_id";
    private Button btnReload;
    Button buttonFalse;
    Button buttonTrue;
    ImageButton imageButtonPrev;
    ImageButton imageButtonNext;
    CardView cardView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // connect to the xml
        fTextViewQuestion = (TextView)findViewById(R.id.text_view_question);
        fTextViewScore = (TextView) findViewById(R.id. text_view_score);
        buttonFalse = (Button) findViewById(R.id.button_false);
        buttonTrue = (Button) findViewById(R.id.button_true);
        imageButtonPrev = (ImageButton) findViewById(R.id.button_previous);
        imageButtonNext = (ImageButton) findViewById(R.id.button_next);
        fTextViewUserScore = (TextView) findViewById(R.id.tv_user_score);
        fTextViewHighestScore = (TextView) findViewById(R.id.tv_highest_score);
        cardView = (CardView) findViewById(R.id.cardView);
        btnReload = ( Button ) findViewById( R.id.button_reload );

        if ( haveNetworkConnection() )
            hasNetworkCode();
        else
            noNetworkCode();

    }

    private void hasNetworkCode() {

        buttonTrue.setVisibility(View.VISIBLE);
        buttonFalse.setVisibility(View.VISIBLE);
        fTextViewUserScore.setVisibility(View.VISIBLE);
        imageButtonNext.setVisibility(View.VISIBLE);
        imageButtonPrev.setVisibility(View.VISIBLE);
        fTextViewHighestScore.setVisibility(View.VISIBLE);
        fTextViewScore.setVisibility(View.VISIBLE);
        cardView.setVisibility(View.VISIBLE);

        TextView noNetwork = ( TextView ) findViewById( R.id.tv_display_message);
        noNetwork.setVisibility( View.INVISIBLE );
        btnReload.setVisibility( View.INVISIBLE );

        // add the on clicks
        buttonTrue.setOnClickListener(this);
        buttonFalse.setOnClickListener(this);
        imageButtonPrev.setOnClickListener(this);
        imageButtonNext.setOnClickListener(this);

        // get state from the last game
        currentQuestionIndex = getState();

        fQuestionList = new QuestionBank().getQuestions(
                new AnswerListAyncTask() {
                    @Override
                    public void processFinished(List<Question> questionList) {

                        //set first question on text view
                        fTextViewQuestion.setText(questionList.get(currentQuestionIndex).getAnswer());

                        //set current question counter
                        fTextViewScore.setText(currentQuestionIndex + " / " + questionList.size());

                    }
                });

    }

    private void noNetworkCode() {

        buttonTrue.setVisibility(View.INVISIBLE);
        buttonFalse.setVisibility(View.INVISIBLE);
        fTextViewUserScore.setVisibility(View.INVISIBLE);
        imageButtonNext.setVisibility(View.INVISIBLE);
        imageButtonPrev.setVisibility(View.INVISIBLE);
        fTextViewHighestScore.setVisibility(View.INVISIBLE);
        fTextViewScore.setVisibility(View.INVISIBLE);
        cardView.setVisibility(View.INVISIBLE);

        TextView noNetwork = ( TextView ) findViewById( R.id.tv_display_message);
        noNetwork.setVisibility( View.VISIBLE );
        btnReload.setVisibility( View.VISIBLE );
        btnReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( haveNetworkConnection() ){
                    hasNetworkCode();
                }

                else
                    noNetworkCode();
            }
        });

        Toast.makeText(getApplicationContext(), "Please connect to the internet", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_true:
                checkAnswer(true);
                updateQuestion();
                break;

            case R.id.button_false:
                checkAnswer(false);
                updateQuestion();
                break;

            case R.id.button_next:
                currentQuestionIndex = (currentQuestionIndex + 1 ) % fQuestionList.size();
                updateQuestion();
                break;

            case R.id.button_previous:
                setPreviousQuestion();
                break;

        }
    }

    private void checkAnswer(boolean b) {
        // get the actual answer
        boolean correctAnswer = fQuestionList.get(currentQuestionIndex).isAnswerTrue();

        int toastID = 0;

        if ( correctAnswer == b  ) { // compare the two booleans
            fadeView();
            toastID = R.string.correct_answer;
            fUserCorrectScore++;
        }

        else {
            shakeAnimation();
            toastID = R.string.wrong_answer;

            if ( fTotalScore > 0 )
                fUserIncorrectScore++;
        }

        Toast.makeText(getApplicationContext(), toastID, Toast.LENGTH_LONG).show();

    }

    private void setPreviousQuestion() {
        // check the current index
        if ( currentQuestionIndex > 0 )
            currentQuestionIndex = ( currentQuestionIndex - 1 ) % fQuestionList.size();

        updateQuestion();
    }

    private void updateQuestion() {

        String question = fQuestionList.get(currentQuestionIndex).getAnswer();
        fTextViewQuestion.setText(question);
        fTextViewScore.setText(currentQuestionIndex + " / " + fQuestionList.size());
        fTotalScore = fUserCorrectScore - fUserIncorrectScore;
        String finalMessage = "Score: " + (fTotalScore * 10);
        fTextViewUserScore.setText(finalMessage);
    }

    private void shakeAnimation() {
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.anim);
        final CardView cardView  = (CardView) findViewById(R.id.cardView);
        cardView.setAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                currentQuestionIndex = (currentQuestionIndex + 1 ) % fQuestionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeView(){
        final CardView cardView = (CardView) findViewById(R.id.cardView);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        cardView.setAnimation(alphaAnimation);  // set the animation to the cardview

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                currentQuestionIndex = (currentQuestionIndex + 1 ) % fQuestionList.size();
                updateQuestion();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        String message = String.valueOf(fTotalScore);

        SharedPreferences preferences = getSharedPreferences(MESSAGES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit(); //get the editor

        int lastScore = preferences.getInt( "highest_socre", 0); // get the last saved score

        if ( lastScore > fTotalScore ){

            editor.putInt("highest_socre", lastScore);
            editor.apply();
        } else {
            editor.putInt("highest_socre", fTotalScore);
            editor.apply();
        }

        String highest = "Highest score: " + lastScore;
        fTextViewHighestScore.setText(highest);

        setState(currentQuestionIndex);

        //storeHighestScore();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Get the data
        SharedPreferences getData = getSharedPreferences(MESSAGES_ID, MODE_PRIVATE);

        int score  =  ( getData.getInt("highest_socre",0) ) * 10;

        String newMessage = "Highest score: " + score;

        fTextViewHighestScore.setText(newMessage);

    }

    private void storeHighestScore(){
        int total = fTotalScore;

        String highest = "Highest score: " + total;
        fTextViewHighestScore.setText(highest);
    }

    // save our app's current state
    public void setState(int index){
        SharedPreferences preferences = getSharedPreferences(MESSAGES_ID, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("current_question", index );
        editor.apply();
    }

    //get the previous state
    public int getState(){

        SharedPreferences preferences = getSharedPreferences(MESSAGES_ID, MODE_PRIVATE);

        return preferences.getInt( "current_question", 0 );
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if ( haveNetworkConnection() )
            hasNetworkCode();
        else
            noNetworkCode();
    }
}
