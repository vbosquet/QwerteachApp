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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.messaging.RemoteMessage;
import com.pusher.android.PusherAndroid;
import com.pusher.android.notifications.ManifestValidator;
import com.pusher.android.notifications.PushNotificationRegistration;
import com.pusher.android.notifications.fcm.FCMPushNotificationReceivedListener;
import com.pusher.android.notifications.interests.InterestSubscriptionChangeListener;
import com.pusher.android.notifications.tokens.PushNotificationRegistrationListener;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.Conversation;
import com.qwerteach.wivi.qwerteachapp.models.ConversationAdapter;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.LessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyMessagesActivity extends AppCompatActivity implements ConversationAdapter.Callback {

    String email, token, userId;
    List<Conversation> conversationsList;
    List<User> usersList;
    QwerteachService service;
    RecyclerView conversationRecyclerView;
    RecyclerView.Adapter conversationAdapter;
    RecyclerView.LayoutManager conversationLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        conversationRecyclerView = (RecyclerView) findViewById(R.id.conversation_list_view);
        service = ApiClient.getClient().create(QwerteachService.class);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        getAllConversations();

    }

    public void getAllConversations() {
        Call<JsonResponse> call = service.getConversations(email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                List<Message> messages = response.body().getMessages();
                conversationsList = response.body().getConversations();
                usersList = response.body().getRecipients();

                for (int i = 0; i < conversationsList.size(); i++) {
                    for (int j = 0; j < messages.size(); j++) {
                        boolean isMine = false;
                        if (userId.equals(String.valueOf(messages.get(j).getSenderId()))) {
                            isMine = true;
                        }
                        messages.get(j).setMine(isMine);

                        if (Objects.equals(messages.get(j).getConversationId(), conversationsList.get(i).getConversationId())) {
                            conversationsList.get(i).addMessageToConverstaion(messages.get(j));
                        }

                        conversationsList.get(i).setUser(usersList.get(i));
                        int conversationId = conversationsList.get(i).getConversationId();

                        if (conversationId == conversationsList.get(conversationsList.size() - 1).getConversationId()) {
                            displayConversationListView();
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_messages_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, DashboardActivity.class);
                startActivity(intent);
                return true;
            case R.id.new_conversation_button:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayConversationListView() {
        conversationAdapter = new ConversationAdapter(conversationsList, this);
        conversationRecyclerView.setHasFixedSize(true);
        conversationLayoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(conversationLayoutManager);
        conversationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        conversationRecyclerView.setAdapter(conversationAdapter);

    }

    public void didTouchConversation(int index) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("conversation", conversationsList.get(index));
        startActivity(intent);
    }
}
