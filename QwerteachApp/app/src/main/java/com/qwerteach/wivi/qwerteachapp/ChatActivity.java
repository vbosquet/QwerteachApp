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
import android.widget.EditText;
import android.widget.ListView;

import com.qwerteach.wivi.qwerteachapp.asyncTasks.GetAllMessagesAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.ReplyAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Conversation;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements ReplyAsyncTask.IReply,
        GetAllMessagesAsyncTask.IGetAllMessages{

    Conversation conversation;
    ListView messageListView;
    ArrayList<Message> messages;
    EditText messageToSendEditText;
    String email, token, userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        userId = preferences.getString("userId", "");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(conversation.getUser().getFirstName());

        messageToSendEditText = (EditText) findViewById(R.id.message_to_send_edit_text);
        messageListView = (ListView) findViewById(R.id.message_list_view);
        messages = conversation.getMessages();

        displayMessagesListView();
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
        MessageAdapter messageAdapter = new MessageAdapter(this, messages);
        messageListView.setAdapter(messageAdapter);
    }

    public void didTouchSendButton(View view) {
        String message = messageToSendEditText.getText().toString();
        int conversationId = conversation.getConversationId();
        ReplyAsyncTask replyAsyncTask = new ReplyAsyncTask(this);
        replyAsyncTask.execute(email, token, message, conversationId);

    }

    @Override
    public void confirmationMessage(String string) {

        int conversationId = conversation.getConversationId();
        GetAllMessagesAsyncTask getAllMessagesAsyncTask = new GetAllMessagesAsyncTask(this);
        getAllMessagesAsyncTask.execute(email, token, conversationId);
    }

    @Override
    public void getAllMessages(String string) {

        messageToSendEditText.setText("");

        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONObject lastMessage = jsonObject.getJSONObject("last_message");

            int messagesId = lastMessage.getInt("id");
            String body = lastMessage.getString("body");
            String subject = lastMessage.getString("subject");
            int senderId = lastMessage.getInt("sender_id");
            int conversationId = lastMessage.getInt("conversation_id");
            String creationDate = lastMessage.getString("created_at");

            boolean isMine = false;
            if (userId.equals(String.valueOf(senderId))) {
                isMine = true;
            }

            Message message = new Message(messagesId, body, subject, senderId, conversationId, creationDate, isMine);
            messages.add(message);

            displayMessagesListView();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
