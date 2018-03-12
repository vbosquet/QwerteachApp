package com.qwerteach.wivi.qwerteachapp;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.EndlessRecyclerViewScrollListener;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.Conversation;
import com.qwerteach.wivi.qwerteachapp.models.ConversationAdapter;
import com.qwerteach.wivi.qwerteachapp.models.HistoryLessonsAdapter;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.User;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
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
    EndlessRecyclerViewScrollListener scrollListener;

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
        conversationsList = new ArrayList<>();
        usersList = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        displayConversationListView();
        getAllConversations(1);

    }

    public void getAllConversations(final int pageNumber) {
        startProgressDialog();
        Call<JsonResponse> call = service.getConversations(pageNumber, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                if (response.isSuccessful()) {
                    List<Conversation> newConversationsList = response.body().getConversations();
                    List<User> newUsersList = response.body().getRecipients();
                    List<Message> messages = response.body().getMessages();
                    List<String> avatars = response.body().getParticipantAvatars();

                    progressDialog.dismiss();

                    if (newConversationsList.size() == 0 && pageNumber == 1) {
                        emptyMailboxTitle.setVisibility(View.VISIBLE);
                        emptyMailboxMessage.setVisibility(View.VISIBLE);

                    } else {
                        for (int i = 0; i < newConversationsList.size(); i++) {
                            if(newConversationsList.get(i) != null) {
                                conversationsList.add(newConversationsList.get(i));
                                int conversationId = newConversationsList.get(i).getConversationId();

                                if(newUsersList.get(i) != null) {
                                    usersList.add(newUsersList.get(i));
                                    newConversationsList.get(i).setUser(newUsersList.get(i));
                                }

                                if (avatars.get(i) != null) {
                                    newConversationsList.get(i).getUser().setAvatarUrl(avatars.get(i));
                                }

                                for (int j = 0; j < messages.size(); j++) {
                                    if (Objects.equals(messages.get(j).getConversationId(), conversationId)) {
                                        newConversationsList.get(i).setLastMessage(messages.get(j));
                                    }
                                }


                                if (conversationId == newConversationsList.get(newConversationsList.size() - 1).getConversationId()) {
                                    conversationAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.socket_failure, Toast.LENGTH_SHORT).show();
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
        scrollListener = new EndlessRecyclerViewScrollListener((LinearLayoutManager) conversationLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d("PAGE", String.valueOf(page));
                getAllConversations(page);
            }
        };
        conversationRecyclerView.addOnScrollListener(scrollListener);

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
