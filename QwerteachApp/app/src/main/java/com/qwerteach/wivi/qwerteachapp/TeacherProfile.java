package com.qwerteach.wivi.qwerteachapp;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.models.SmallAd;
import com.qwerteach.wivi.qwerteachapp.models.SmallAdPrice;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.format;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class TeacherProfile extends AppCompatActivity {

    Teacher teacher;
    SmallAd smallAd;
    TextView teacherName, teacherDescription, teacherOccupation, teacherAge, courseMaterialNames, minPrice;
    Button contactTeacherButton;
    ArrayList<SmallAdPrice> smallAdPrices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        smallAdPrices = new ArrayList<>();

        teacherName = (TextView) findViewById(R.id.firstname_and_lastanme_text_view);
        teacherDescription = (TextView) findViewById(R.id.description_text_view);
        teacherOccupation = (TextView) findViewById(R.id.occupation_text_view);
        teacherAge = (TextView) findViewById(R.id.age_text_view);
        courseMaterialNames = (TextView) findViewById(R.id.course_names_text_view);
        minPrice = (TextView) findViewById(R.id.teacher_min_price);
        contactTeacherButton = (Button) findViewById(R.id.contact_button);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getIntent().getSerializableExtra("teacher");
            smallAd = (SmallAd) getIntent().getSerializableExtra("smallAd");
        }

        teacherName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        teacherOccupation.setText(teacher.getOccupation());
        String text = teacher.getDescription();
        text = text.replace("\\n\\n", "");
        text = text.replace("\\n", "");
        teacherDescription.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        contactTeacherButton.setText("Contacter " + teacher.getFirstName());
        courseMaterialNames.setText(teacher.getTopicTitleList());
        minPrice.setText("A partir de " +teacher.getMinPrice() + " €/h");

        Date currentDate = new Date();
        currentDate.getTime();

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date birthDate = format.parse(teacher.getBirthDate());
            int age = getDiffBetYears(birthDate, currentDate);
            teacherAge.setText(age + " ans");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static int getDiffBetYears(Date first, Date last) {
        Calendar firstDate = getCalendar(first);
        Calendar secDate = getCalendar(last);
        int diff = secDate.get(YEAR) - firstDate.get(YEAR);
        if (firstDate.get(MONTH) > secDate.get(MONTH) ||
                (firstDate.get(MONTH) == secDate.get(MONTH) && firstDate.get(DATE) > secDate.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public void didTouchContactButton(View view) {
    }

    public void didTouchSeeDetailedPrices(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.detailed_prices_alert_dialog, null);
        builder.setView(dialogView);

        TextView title = (TextView) dialogView.findViewById(R.id.title);
        LinearLayout alertDialog = (LinearLayout) dialogView.findViewById(R.id.alert_dialog_linear_layout);

        title.setText("Tarif(s) de " + teacher.getFirstName());

        smallAdPrices = smallAd.getSmallAdPrices();

        for (int i = 0; i < smallAdPrices.size(); i++) {
            String priceString = String.valueOf(smallAdPrices.get(i).getPrice());
            LinearLayout linearLayout = new LinearLayout(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView price = new TextView(this);
            price.setText(priceString);
            linearLayout.addView(price);
            alertDialog.addView(linearLayout, params);
        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
