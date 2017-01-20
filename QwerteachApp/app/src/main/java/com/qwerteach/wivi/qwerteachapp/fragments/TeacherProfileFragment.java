package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.LessonReservationActivity;
import com.qwerteach.wivi.qwerteachapp.MyMessagesActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.ReadCommentsActivity;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.DisplayTopicLevelsAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.NewLessonRequestAsyncTask;
import com.qwerteach.wivi.qwerteachapp.asyncTasks.SendMessageToTeacherAsyncTask;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wivi on 11/01/17.
 */

public class TeacherProfileFragment extends Fragment implements View.OnClickListener,
        SendMessageToTeacherAsyncTask.ISendMessageToTeacher,
        DisplayTopicLevelsAsyncTask.IDisplayTopicLevels,
        NewLessonRequestAsyncTask.INewLessonRequest {

    View view;
    String email, token, firstName, lastName;
    TextView teacherName, teacherDescription, teacherOccupation, teacherAge, courseMaterialNames,
            minPrice, senderFirstName, reviewText, sendingDate, readMoreComments, detailedPricesTextView;
    LinearLayout lastReview;
    RatingBar ratingBar;
    Button contactTeacherButton, lessonReservationButton;
    Teacher teacher;
    ArrayList<Review> reviews;
    ArrayList<SmallAd> smallAds;
    ArrayList<Level> levels;
    ArrayList<String> topicGroupTitleList;
    ProgressDialog progressDialog;
    ImageView teacherAvatar;

    public static TeacherProfileFragment newInstance() {
        TeacherProfileFragment teacherProfileFragment = new TeacherProfileFragment();
        return teacherProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");
        firstName = preferences.getString("firstName", "");
        lastName = preferences.getString("lastName", "");

        progressDialog = new ProgressDialog(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_teacher_profile, container, false);

        //Bundle from SearchTeacherActivity
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getActivity().getIntent().getSerializableExtra("teacher");
        }

        //Bundle from ProfileActivity
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            teacher = (Teacher) bundle.getSerializable("teacher");
        }

        reviews = teacher.getReviews();
        smallAds = teacher.getSmallAds();
        levels = new ArrayList<>();
        topicGroupTitleList = new ArrayList<>();

        for (int i = 0; i < smallAds.size(); i++) {
            int topicId = smallAds.get(i).getTopicId();
            DisplayTopicLevelsAsyncTask displayTopicLevelsAsyncTask = new DisplayTopicLevelsAsyncTask(this);
            displayTopicLevelsAsyncTask.execute(topicId);
        }

        teacherName = (TextView) view.findViewById(R.id.firstname_and_lastanme_text_view);
        teacherDescription = (TextView) view.findViewById(R.id.description_text_view);
        teacherOccupation = (TextView) view.findViewById(R.id.occupation_text_view);
        teacherAge = (TextView) view.findViewById(R.id.age_text_view);
        courseMaterialNames = (TextView) view.findViewById(R.id.course_names_text_view);
        minPrice = (TextView) view.findViewById(R.id.teacher_min_price);
        detailedPricesTextView = (TextView) view.findViewById(R.id.detailed_prices_text_view);
        contactTeacherButton = (Button) view.findViewById(R.id.contact_button);
        lessonReservationButton = (Button) view.findViewById(R.id.reservation_button);
        teacherAvatar = (ImageView) view.findViewById(R.id.teacher_avatar);

        contactTeacherButton.setOnClickListener(this);
        detailedPricesTextView.setOnClickListener(this);
        lessonReservationButton.setOnClickListener(this);

        displayTeacherProfileInfos();

        return  view;
    }

    public void displayTeacherProfileInfos() {
        teacherName.setText(teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName());
        teacherOccupation.setText(teacher.getUser().getOccupation());
        String text = teacher.getUser().getDescription();
        text = text.replace("\\n\\n", "");
        text = text.replace("\\n", "");
        teacherDescription.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        contactTeacherButton.setText("Contacter " + teacher.getUser().getFirstName());
        courseMaterialNames.setText(teacher.getTopics());
        minPrice.setText("A partir de " +teacher.getMinPrice() + " €/h");
        teacherAge.setText(teacher.getUser().getAge() + " ans");

        if (teacher.getNumberOfReviews() > 0) {
            displayLastComment();
        }

        Picasso.with(getContext())
                .load(teacher.getUser().getAvatarUrl())
                .resize(teacherAvatar.getWidth(), 1000)
                .into(teacherAvatar);
    }

    public void displayLastComment() {
        lastReview = (LinearLayout) view.findViewById(R.id.last_comment);
        senderFirstName = (TextView) view.findViewById(R.id.sender_first_name);
        reviewText = (TextView) view.findViewById(R.id.review_text);
        sendingDate = (TextView) view.findViewById(R.id.sending_date);
        readMoreComments = (TextView) view.findViewById(R.id.read_more_comments);
        ratingBar = (RatingBar) view.findViewById(R.id.rating_bar);

        int lastPosition = reviews.size() - 1;

        lastReview.setVisibility(View.VISIBLE);
        senderFirstName.setText(reviews.get(lastPosition).getSenderFirstName());
        reviewText.setText(reviews.get(lastPosition).getReviewText());
        String dateToFormat = reviews.get(lastPosition).getCreationDate();
        sendingDate.setText(reviews.get(lastPosition).getMonth(dateToFormat) + " "
                + reviews.get(lastPosition).getYear(dateToFormat));
        readMoreComments.setText("Lire " + teacher.getNumberOfReviews() + " commentaire(s)");
        readMoreComments.setOnClickListener(this);
        ratingBar.setRating(teacher.getRating());

        if (reviews.size() > 1) {
            readMoreComments.setVisibility(View.VISIBLE);
        }

    }

    public void didTouchContactButton() {
        createContactTeacherAlertDialog();
    }

    public void createContactTeacherAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.alert_dialog_contact_teacher, null);
        builder.setView(content);

        TextView titleTextView = (TextView) content.findViewById(R.id.alert_dialog_title);
        final EditText userMessageEditText = (EditText) content.findViewById(R.id.alert_dialog_user_message);

        String message = ("Posez une question à " + teacher.getUser().getFirstName());
        titleTextView.setText(message);

        builder.setPositiveButton("ENVOYER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String body = userMessageEditText.getText().toString();
                int recipient = teacher.getUser().getUserId();
                String subject = firstName + " " + lastName + " vous pose une question !";

                startSendMessageToTeacherAsyncTask(body, subject, recipient);

            }
        });
        builder.setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.create().show();

    }

    public void startSendMessageToTeacherAsyncTask(String body, String subject, int recipient) {
        SendMessageToTeacherAsyncTask sendMessageToTeacherAsyncTask = new SendMessageToTeacherAsyncTask(this);
        sendMessageToTeacherAsyncTask.execute(email, token, subject, body, recipient);
        startProgressDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.contact_button:
                didTouchContactButton();
                break;
            case R.id.detailed_prices_text_view:
                didTouchSeeDetailedPrices();
                break;
            case R.id.read_more_comments:
                didTouchReadComments();
                break;
            case R.id.reservation_button:
                didTouchLessonReservationButton();
                break;
        }
    }

    @Override
    public void confirmationMessage(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String message = jsonObject.getString("message");
            progressDialog.dismiss();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), MyMessagesActivity.class);
            startActivity(intent);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void displayTopicLevels(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("levels");
            String topicGroupTitle = jsonObject.getString("topic_group_title");
            topicGroupTitleList.add(topicGroupTitle);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonData = jsonArray.getJSONObject(i);
                int levelId = jsonData.getInt("id");
                String levelName = jsonData.getString("fr");
                Level level = new Level(levelId, levelName);
                levels.add(level);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void didTouchSeeDetailedPrices() {
        createAlertDialog();
    }


    public void createAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.detailed_prices_alert_dialog, null);
        builder.setView(dialogView);

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        LinearLayout alertDialog = (LinearLayout) dialogView.findViewById(R.id.alert_dialog_linear_layout);

        title.setText("Tarif(s) de " + teacher.getUser().getFirstName());

        for (int i = 0; i < smallAds.size(); i++) {
            String topicTitle = smallAds.get(i).getTitle();
            String topicGroupTitle = topicGroupTitleList.get(i);
            addSmallAdTitlesToAlertDialog(topicTitle, topicGroupTitle, alertDialog);

            ArrayList<SmallAdPrice> smallAdPrices = smallAds.get(i).getSmallAdPrices();

            for (int j = 0; j < smallAdPrices.size(); j++) {
                int levelId = smallAdPrices.get(j).getLevelId();
                String price = String.valueOf(smallAdPrices.get(j).getPrice());
                String levelName = "";

                for (int k = 0; k < levels.size(); k++) {
                    if (levels.get(k).getLevelId() == levelId) {
                        levelName = levels.get(k).getLevelName();
                    }
                }

                addSmallAdLevelsAndPricesToAlertDialog(price, levelName, alertDialog);
            }
        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void addSmallAdTitlesToAlertDialog(String topicTitle, String topicGroupTitle, LinearLayout alertDialog) {
        LinearLayout titleLinearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleLinearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView topic = new TextView(getContext());
        topic.setText(topicGroupTitle + " - " + topicTitle);
        topic.setPadding(0, 20, 0, 20);
        topic.setTextColor(getActivity().getColor(R.color.colorPrimary));
        titleLinearLayout.addView(topic);
        alertDialog.addView(titleLinearLayout, titleParams);
    }

    public void addSmallAdLevelsAndPricesToAlertDialog(String priceString, String levelName, LinearLayout alertDialog) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView level = new TextView(getContext());
        TextView price = new TextView(getContext());

        level.setText(levelName);
        price.setText(priceString + " €/h");

        TableRow.LayoutParams levelParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
        TableRow.LayoutParams priceParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.3f);
        level.setLayoutParams(levelParams);
        price.setLayoutParams(priceParams);

        linearLayout.addView(level);
        linearLayout.addView(price);

        alertDialog.addView(linearLayout, params);

    }

    public void didTouchReadComments() {

        Intent intent = new Intent(getContext(), ReadCommentsActivity.class);
        intent.putExtra("reviews", reviews);
        startActivity(intent);
    }

    public void didTouchLessonReservationButton() {
        String teacherId = String.valueOf(teacher.getUser().getUserId());
        NewLessonRequestAsyncTask newLessonRequestAsyncTask = new NewLessonRequestAsyncTask(this);
        newLessonRequestAsyncTask.execute(teacherId, email, token);
    }

    @Override
    public void lessonRequest(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            String success = jsonObject.getString("success");

            if (success.equals("true")) {
                Intent intent = new Intent(getContext(), LessonReservationActivity.class);
                intent.putStringArrayListExtra("topicGroup", topicGroupTitleList);
                intent.putExtra("level", levels);
                intent.putExtra("teacher", teacher);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }
}
