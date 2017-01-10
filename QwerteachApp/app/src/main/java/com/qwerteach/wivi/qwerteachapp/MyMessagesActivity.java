package com.qwerteach.wivi.qwerteachapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.FindUsersByMangoIdAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllConversationsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Conversation;
import com.qwerteach.wivi.qwerteachapp.models.ConversationAdapter;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyMessagesActivity extends AppCompatActivity implements GetAllConversationsAsyncTask.IGetAllConversations,
        AdapterView.OnItemClickListener {

    String email, token, userId;
    ArrayList<Conversation> conversationsList;
    ArrayList<User> usersList;
    ListView conversationListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_messages);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        conversationListView = (ListView) findViewById(R.id.conversation_list_view);

        conversationsList = new ArrayList<>();
        usersList = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        GetAllConversationsAsyncTask getAllConversationsAsyncTask = new GetAllConversationsAsyncTask(this);
        getAllConversationsAsyncTask.execute(email, token);
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

    @Override
    public void getAllConversations(String string) {

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray conversations = jsonObject.getJSONArray("conversations");
            JSONArray recipients = jsonObject.getJSONArray("recipients");
            JSONArray messages = jsonObject.getJSONArray("messages");

            for (int i = 0; i < conversations.length(); i++) {
                ArrayList<Message> messagesList = new ArrayList<>();

                JSONObject conversationsData = conversations.getJSONObject(i);
                int conversationId = conversationsData.getInt("id");
                String conversationSubject = conversationsData.getString("subject");
                String conversationCreationDate = conversationsData.getString("created_at");
                String conversationUpdatingDate = conversationsData.getString("updated_at");

                for (int j = 0; j < messages.length(); j++) {
                    JSONObject messagesData = messages.getJSONObject(j);
                    int messageId = messagesData.getInt("id");
                    String body = messagesData.getString("body");
                    String subject = messagesData.getString("subject");
                    int senderId = messagesData.getInt("sender_id");
                    int messageConversationId = messagesData.getInt("conversation_id");
                    String creationDate = messagesData.getString("created_at");

                    boolean isMine = false;
                    if (userId.equals(String.valueOf(senderId))) {
                        isMine = true;
                    }

                    Message message = new Message(messageId, body, subject, senderId, messageConversationId, creationDate, isMine);

                    if (messageConversationId == conversationId) {
                        messagesList.add(message);
                    }
                }

                Conversation conversation = new Conversation(conversationId, conversationSubject,
                        conversationCreationDate, conversationUpdatingDate);
                conversation.setMessages(messagesList);

                conversationsList.add(conversation);
            }

            for (int i = 0; i < recipients.length(); i++) {
                JSONObject jsonData = recipients.getJSONObject(i);
                int userId = jsonData.getInt("id");
                String firstName = jsonData.getString("firstname");
                String lastName = jsonData.getString("lastname");

                User user = new User();
                user.setUserId(userId);
                user.setFirstName(firstName);
                user.setLastName(lastName);

                usersList.add(user);
            }

            for (int i = 0; i < conversationsList.size(); i++) {
                conversationsList.get(i).setUser(usersList.get(i));
                int conversationId = conversationsList.get(i).getConversationId();

                if (conversationId == conversationsList.get(conversationsList.size() - 1).getConversationId()) {
                    displayConversationListView();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayConversationListView() {
        ConversationAdapter conversationAdapter = new ConversationAdapter(this, conversationsList);
        conversationListView.setAdapter(conversationAdapter);
        conversationListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        int position = conversationListView.getPositionForView(view);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("conversation", conversationsList.get(position));
        startActivity(intent);
    }
}
