package com.cazimir.someonetotalkto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class AgentEngine {

    private OkHttpClient client = new OkHttpClient();

    public Observable<String> sendMessageToAgent(String messageToSend) {

        return Observable.create(emitter -> {

            client.newCall(createRequest(messageToSend)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    final String myResponse = response.body().string();

                    JSONObject json = null;
                    try {
                        json = new JSONObject(myResponse);
                    } catch (JSONException e) {
                        emitter.onError(e);
                        e.printStackTrace();
                    }

                    try {
                        String text = json.getJSONObject("result").getJSONObject("fulfillment").getString("speech");
                        emitter.onNext(text);
                    } catch (JSONException e) {
                        emitter.onError(e);
                        e.printStackTrace();
                    }

                }
            });
        });
    }

    private Request createRequest(String message) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.dialogflow.com/v1/query").newBuilder();

        urlBuilder.addQueryParameter("v", "20150910");
        urlBuilder.addQueryParameter("query", message);
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("sessionId", String.valueOf(UUID.randomUUID()));
        urlBuilder.addQueryParameter("timezone", "America/New_York");

        String url = urlBuilder.build().toString();

        return new Request.Builder()
                .header("Authorization", "Bearer f1573cd929f0411296d9ab5de588fef1")
                .url(url)
                .build();

    }
}
