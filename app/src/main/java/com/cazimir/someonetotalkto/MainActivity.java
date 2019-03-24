package com.cazimir.someonetotalkto;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.bassaer.chatmessageview.model.ChatUser;
import com.github.bassaer.chatmessageview.model.Message;
import com.github.bassaer.chatmessageview.view.ChatView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String ACCESS_TOKEN = "f1573cd929f0411296d9ab5de588fef1";
    private ChatView chatView;
    private ChatUser human;
    private ChatUser agent;
    private Message.Builder messageBuilder;
    private OkHttpClient client = new OkHttpClient();
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

        chatView.setOnClickSendButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                messageBuilder = new Message.Builder();

                Message message = messageBuilder
                        .setUser(human)
                        .setRight(false)
                        .setText(chatView.getInputText())
                        .build();

                chatView.send(message);

                askAgent(chatView.getInputText());

            }
        });
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

    private void askAgent(String query) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.dialogflow.com/v1/query").newBuilder();

        urlBuilder.addQueryParameter("v", "20150910");
        urlBuilder.addQueryParameter("query", query);
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("sessionId", String.valueOf(UUID.randomUUID()));
        urlBuilder.addQueryParameter("timezone", "America/New_York");

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization", "Bearer f1573cd929f0411296d9ab5de588fef1")
                .url(url)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String myResponse = response.body().string();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject json = new JSONObject(myResponse);

                            String text = json.getJSONObject("result").getJSONObject("fulfillment").getString("speech");

                            showAgentResponse(text);
                            speakTTS(text);

                            Log.d(TAG, "received response: " + json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

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
}
