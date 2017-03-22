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

import com.google.gson.Gson;
import com.qwerteach.wivi.qwerteachapp.LessonReservationActivity;
import com.qwerteach.wivi.qwerteachapp.MyMessagesActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.ReadCommentsActivity;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Level;
import com.qwerteach.wivi.qwerteachapp.models.Message;
import com.qwerteach.wivi.qwerteachapp.models.Review;
import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by wivi on 11/01/17.
 */

public class TeacherProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    TextView teacherName, teacherDescription, teacherOccupation, teacherAge, courseMaterialNames,
            minPrice, senderFirstName, reviewText, sendingDate, readMoreComments, detailedPricesTextView;
    LinearLayout lastReview;
    RatingBar ratingBar;
    Button contactTeacherButton, lessonReservationButton;
    Teacher teacher;
    ArrayList<Review> reviews;
    ArrayList<SmallAd> smallAds;
    ProgressDialog progressDialog;
    ImageView teacherAvatar;
    QwerteachService service;
    AlertDialog.Builder contactDialog;
    User user;

    public static TeacherProfileFragment newInstance() {
        TeacherProfileFragment teacherProfileFragment = new TeacherProfileFragment();
        return teacherProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Gson gson = new Gson();
        String json = preferences.getString("user", "");
        user = gson.fromJson(json, User.class);

        progressDialog = new ProgressDialog(getContext());
        contactDialog = new AlertDialog.Builder(getContext());
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

        service = ApiClient.getClient().create(QwerteachService.class);
        reviews = teacher.getReviews();
        smallAds = teacher.getSmallAds();

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
        teacherDescription.setText(Html.fromHtml(teacher.getUser().getDescription()), TextView.BufferType.SPANNABLE);
        contactTeacherButton.setText("Contacter " + teacher.getUser().getFirstName());
        courseMaterialNames.setText(teacher.getTopics());
        minPrice.setText("A partir de " +teacher.getMinPrice() + " €/h");
        teacherAge.setText(teacher.getUser().getAge() + " ans");

        if (teacher.getNumberOfReviews() > 0) {
            displayLastComment();
        }

        Picasso.with(getContext()).load(teacher.getUser().getAvatarUrl()).resize(teacherAvatar.getWidth(), 1000).into(teacherAvatar);
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
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.alert_dialog_contact_teacher, null);
        contactDialog.setView(content);

        TextView titleTextView = (TextView) content.findViewById(R.id.alert_dialog_title);
        final EditText userMessageEditText = (EditText) content.findViewById(R.id.alert_dialog_user_message);

        String message = ("Posez une question à " + teacher.getUser().getFirstName());
        titleTextView.setText(message);

        contactDialog.setPositiveButton("ENVOYER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String body = userMessageEditText.getText().toString();
                int recipient = teacher.getUser().getUserId();
                String subject = user.getFirstName() + " " + user.getLastName() + " vous pose une question !";

                startSendMessageToTeacher(body, subject, recipient);

            }
        });
        contactDialog.setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        contactDialog.create().show();

    }

    public void startSendMessageToTeacher(String body, final String subject, int recipient) {
        Message message = new Message(subject, body, recipient);
        Map<String, Message> requestBody = new HashMap<>();
        requestBody.put("message", message);

        startProgressDialog();
        Call<JsonResponse> call = service.sendMessageToTeacher(requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                String success = response.body().getSuccess();
                String message = response.body().getMessage();

                progressDialog.dismiss();
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                if (success.equals("true")) {
                    Intent intent = new Intent(getContext(), MyMessagesActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });
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

    public void didTouchSeeDetailedPrices() {
        createAlertDialog();
    }


    public void createAlertDialog() {
        startProgressDialog();
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.detailed_prices_alert_dialog, null);
        builder.setView(dialogView);

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        final LinearLayout alertDialog = (LinearLayout) dialogView.findViewById(R.id.alert_dialog_linear_layout);

        title.setText("Tarif(s) de " + teacher.getUser().getFirstName());

        for (int i = 0; i < smallAds.size(); i++) {
            final String topic = smallAds.get(i).getTitle();
            final ArrayList<SmallAdPrice> smallAdPrices = smallAds.get(i).getSmallAdPrices();

            Call<JsonResponse> call = service.getInfosForDetailedPrices(smallAds.get(i).getAdvertId(), user.getEmail(), user.getToken());
            call.enqueue(new Callback<JsonResponse>() {
                @Override
                public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                    ArrayList<Level> levels = response.body().getLevels();
                    List<Level> newLevelsList = new ArrayList<>();
                    for (Level element : levels) {
                        if (!newLevelsList.contains(element)) {
                            newLevelsList.add(element);
                        }
                    }

                    List<SmallAdPrice> newSmallAdPricesList = new ArrayList<>();
                    for (SmallAdPrice element : smallAdPrices) {
                        if (!newSmallAdPricesList.contains(element)) {
                            newSmallAdPricesList.add(element);
                        }
                    }

                    String topicGroup = response.body().getTopicGroupTitle();
                    addSmallAdTitlesToAlertDialog(topic, topicGroup, alertDialog);

                    for (int j = 0; j < newSmallAdPricesList.size(); j++) {
                        addSmallAdLevelsAndPricesToAlertDialog(String.valueOf(newSmallAdPricesList.get(j).getPrice()),
                                newLevelsList.get(j).getLevelName(), alertDialog);
                    }

                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<JsonResponse> call, Throwable t) {

                }
            });
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
        Intent intent = new Intent(getContext(), LessonReservationActivity.class);
        intent.putExtra("teacher", teacher);
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
