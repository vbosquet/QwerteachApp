package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.SubscriptionEventListener;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.Conversation;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.MessageAdapter;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    Conversation conversation;
    List<Message> messages;
    EditText messageToSendEditText;
    RecyclerView messageRecyclerView;
    RecyclerView.Adapter messageAdapter;
    RecyclerView.LayoutManager messageLayoutManager;
    int scrollPosition, page = 1;
    QwerteachService service;
    User user;
    boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(conversation.getUser().getFirstName());

        service = ApiClient.getClient().create(QwerteachService.class);

        messageToSendEditText = (EditText) findViewById(R.id.message_to_send_edit_text);
        messageRecyclerView = (RecyclerView) findViewById(R.id.message_list_view);
        messages = conversation.getMessages();
        scrollPosition = messages.size() - 1;
        displayMessagesListView();

        PusherOptions options = new PusherOptions();
        options.setCluster("eu");
        Pusher pusher = new Pusher("1e87927ec5fb91180bb0", options);
        pusher.connect();

        Channel channel = pusher.subscribe(String.valueOf(conversation.getConversationId()));
        channel.bind(String.valueOf(conversation.getConversationId()), new SubscriptionEventListener() {
            @Override
            public void onEvent(String channelName, String eventName, final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        Message message = gson.fromJson(data, JsonResponse.class).getLastMessage();
                        String avatar = gson.fromJson(data, JsonResponse.class).getAvatar();
                        message.setAvatar(avatar);
                        addNewMessage(message);
                    }
                });
            }
        });
    }

    public void addNewMessage(Message message) {
        messageToSendEditText.setText("");
        Boolean isMine = false;
        if (Objects.equals(user.getUserId(), message.getSenderId())) {
            isMine = true;
        }

        message.setMine(isMine);
        messages.add(message);
        scrollPosition = messages.size() - 1;
        messageAdapter.notifyDataSetChanged();
        messageRecyclerView.scrollToPosition(scrollPosition);

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MyMessagesActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayMessagesListView() {
        messageAdapter = new MessageAdapter(messages, this);
        messageLayoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(messageLayoutManager);
        messageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        messageRecyclerView.setAdapter(messageAdapter);
        messageRecyclerView.scrollToPosition(scrollPosition);
        messageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (loading) {
                    if (pastVisiblesItems == 0) {
                        loading = false;
                        page += 1;
                        getMoreMessages();
                    }
                }
            }
        });
    }

    public void didTouchSendButton(View view) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("body", messageToSendEditText.getText().toString());

        Call<JsonResponse> call = service.reply(conversation.getConversationId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    public void getMoreMessages() {
        Call<JsonResponse> call = service.getMoreMessages(conversation.getConversationId(), page, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                List<Message> newMessages = response.body().getMessages();
                List<String> avatars = response.body().getAvatars();

                if (newMessages.size() > 0) {
                    for (int i = 0; i < newMessages.size(); i++) {
                        boolean isMine = false;
                        if (Objects.equals(user.getUserId(), newMessages.get(i).getSenderId())) {
                            isMine = true;
                        }
                        newMessages.get(i).setMine(isMine);
                        newMessages.get(i).setAvatar(avatars.get(i));
                        messages.add(0, newMessages.get(i));
                    }

                    loading = true;
                    displayMessagesListView();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
    }
}
