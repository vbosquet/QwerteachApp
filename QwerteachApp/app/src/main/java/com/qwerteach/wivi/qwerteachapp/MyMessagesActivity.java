package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.LocaleDisplayNames;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
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

    List<Conversation> conversationsList;
    List<User> usersList;
    QwerteachService service;
    RecyclerView conversationRecyclerView;
    RecyclerView.Adapter conversationAdapter;
    RecyclerView.LayoutManager conversationLayoutManager;
    User user;
    TextView emptyMailboxTitle, emptyMailboxMessage;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        emptyMailboxTitle = (TextView) findViewById(R.id.empty_mailbox_title);
        emptyMailboxMessage = (TextView) findViewById(R.id.empty_mailbox_message);
        conversationRecyclerView = (RecyclerView) findViewById(R.id.conversation_list_view);
        service = ApiClient.getClient().create(QwerteachService.class);
        progressDialog = new ProgressDialog(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        getAllConversations();

    }

    public void getAllConversations() {
        startProgressDialog();
        Call<JsonResponse> call = service.getConversations(user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                conversationsList = response.body().getConversations();
                usersList = response.body().getRecipients();
                List<Message> messages = response.body().getMessages();
                List<String> participantAvatar = response.body().getParticipantAvatars();

                if (conversationsList.size() > 0) {
                    for (int i = 0; i < conversationsList.size(); i++) {
                        int conversationId = conversationsList.get(i).getConversationId();
                        conversationsList.get(i).setUser(usersList.get(i));
                        conversationsList.get(i).getUser().setAvatarUrl(participantAvatar.get(i));

                        for (int j = 0; j < messages.size(); j++) {
                            if (Objects.equals(messages.get(j).getConversationId(), conversationId)) {
                                conversationsList.get(i).setLastMessage(messages.get(j));
                            }
                        }

                        if (conversationId == conversationsList.get(conversationsList.size() - 1).getConversationId()) {
                            progressDialog.dismiss();
                            displayConversationListView();
                        }
                    }
                    
                } else {
                    progressDialog.dismiss();
                    emptyMailboxTitle.setVisibility(View.VISIBLE);
                    emptyMailboxMessage.setVisibility(View.VISIBLE);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayConversationListView() {
        conversationAdapter = new ConversationAdapter(this, conversationsList, this);
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

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
