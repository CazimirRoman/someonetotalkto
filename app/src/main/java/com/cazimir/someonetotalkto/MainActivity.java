package com.cazimir.someonetotalkto;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.model.ChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;
import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.Locale;

import io.reactivex.Observable;

public class MainActivity extends MviActivity<IMainActivity, MainPresenter> implements IMainActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ACCESS_TOKEN = "f1573cd929f0411296d9ab5de588fef1";
    private ChatView chatView;
    private ChatUser human;
    private ChatUser agent;
    private Message.Builder messageBuilder;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatView = findViewById(R.id.my_chat_view);
        initializeTts();

        human = new ChatUser(1, "You", BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_account_circle));

        agent = new ChatUser(2, "Sara", BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_account_circle));

        messageBuilder = new Message.Builder();

    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(new AgentInteractor(new AgentEngine()));
    }

    private void initializeTts() {
        textToSpeech = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int ttsLang = textToSpeech.setLanguage(Locale.US);

                if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                        || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "The Language is not supported!");
                } else {
                    Log.i("TTS", "Language Supported.");
                }
                Log.i("TTS", "Initialization success.");

            } else {
                Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void speakTTS(String text) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    private void showAgentResponse(String response) {

        messageBuilder = new Message.Builder();

        Message message = messageBuilder
                .setUser(agent)
                .setRight(true)
                .setText(response)
                .build();

        chatView.send(message);
    }

    @Override
    public Observable<String> sendMessageToAgentIntent() {

        return RxView.clicks(chatView.findViewById(R.id.sendButton))
                .switchMap(obsolete -> buildMessage());
    }

    private Observable<String> buildMessage() {

        messageBuilder = new Message.Builder();

        Message message = messageBuilder
                .setUser(human)
                .setRight(false)
                .setText(chatView.getInputText())
                .build();

        chatView.send(message);

        return Observable.just(chatView.getInputText());
    }

    @Override
    public void render(AgentViewState agentState) {

        if(agentState instanceof AgentViewState.AgentReponse){
            renderDataState(agentState);
        } else if(agentState instanceof AgentViewState.Loading){
            renderLoadingState();
        } else{
            renderErrorState(agentState);
        }

    }

    private void renderErrorState(AgentViewState agentState) {
        //display error message
    }

    private void renderLoadingState() {
        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        //show some kind of progressbar. perhaps a 3 dot loading indicator
    }

    private void renderDataState(AgentViewState agentState) {
        AgentViewState.AgentReponse reponse = (AgentViewState.AgentReponse) agentState;
        showAgentResponse(reponse.getResult());


        //get string from agent and show it in chat view
    }
}
