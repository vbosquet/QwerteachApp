package com.qwerteach.wivi.qwerteachapp.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.Teacher;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.qwerteach.wivi.qwerteachapp.models.UserJson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by wivi on 26/10/16.
 */

public class DescriptionTabFragment extends Fragment implements View.OnClickListener {

    EditText firstNameEditText, lastNameEditText, birthDateEditText, phoneNumberEditText;
    Calendar calendar;
    View view;
    DatePickerDialog.OnDateSetListener dateSetListener;
    String userId, email, token;
    ProgressDialog progressDialog;
    Button saveInfosButton, updateUserAvatarButton;
    Teacher teacher;
    User user;
    ImageView userAvatar;
    QwerteachService service;

    public static DescriptionTabFragment newInstance() {
        DescriptionTabFragment descriptionTabFragment = new DescriptionTabFragment();
        return descriptionTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        userId = preferences.getString("userId", "");
        email = preferences.getString("email", "");
        token = preferences.getString("token", "");

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            teacher = (Teacher) getActivity().getIntent().getSerializableExtra("teacher");
            user = (User) getActivity().getIntent().getSerializableExtra("student");

        }

        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_description_tab, container, false);

        calendar = Calendar.getInstance(TimeZone.getDefault());
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();

            }
        };

        firstNameEditText = (EditText) view.findViewById(R.id.firstname);
        lastNameEditText = (EditText) view.findViewById(R.id.lastname);
        birthDateEditText = (EditText) view.findViewById(R.id.birthdate);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phoneNumber);
        saveInfosButton = (Button) view.findViewById(R.id.save_infos_button);
        userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
        updateUserAvatarButton = (Button) view.findViewById(R.id.update_user_avatar_button);

        birthDateEditText.setOnClickListener(this);
        saveInfosButton.setOnClickListener(this);
        updateUserAvatarButton.setOnClickListener(this);

        displayUserInfos();

        return view;
    }

    public void displayUserInfos() {
        if (user != null) {
            firstNameEditText.setText(user.getFirstName());
            lastNameEditText.setText(user.getLastName());
            birthDateEditText.setText(user.getBirthdate());
            phoneNumberEditText.setText(user.getPhoneNumber());
            Picasso.with(getContext()).load(user.getAvatarUrl()).resize(150, 150).centerCrop().into(userAvatar);
        }

        if (teacher != null) {
            firstNameEditText.setText(teacher.getUser().getFirstName());
            lastNameEditText.setText(teacher.getUser().getLastName());
            birthDateEditText.setText(teacher.getUser().getBirthdate());
            phoneNumberEditText.setText(teacher.getUser().getPhoneNumber());
            Picasso.with(getContext()).load(teacher.getUser().getAvatarUrl()).resize(150, 150).centerCrop().into(userAvatar);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateLabel() {
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        birthDateEditText.setText(sdf.format(calendar.getTime()));
    }

    public void startSaveInfosProfileTabAsyncTask() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String birthDate = birthDateEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();

        final User newUser = new User();

        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setBirthdate(birthDate);
        newUser.setPhoneNumber(phoneNumber);

        Map<String, User> requestBody = new HashMap<>();
        requestBody.put("user", newUser);

        Call<JsonResponse> call = service.getStudentInfos(userId, requestBody, email, token);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                User userResponse = response.body().getUser();
                String success = response.body().getSuccess();
                String message = response.body().getMessage();

                progressDialog.dismiss();

                if (success.equals("true")) {

                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("lastName", userResponse.getLastName());
                    editor.putString("firstName", userResponse.getFirstName());
                    editor.apply();

                } else {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {

            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.birthdate:
                new DatePickerDialog(getContext(), dateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.save_infos_button:
                startProgressDialog();
                startSaveInfosProfileTabAsyncTask();
                break;
            case R.id.update_user_avatar_button:
                getImageFromGallery();
                break;
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.formations_tab_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void startProgressDialog() {
        progressDialog.setMessage("Loading...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    public void getImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public String getPath(Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            Picasso.with(getContext()).load(imageUri.toString()).resize(150, 150).centerCrop().into(userAvatar);

            File file = new File(getPath(imageUri));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("user[avatar]", file.getName(), requestFile);

            QwerteachService service = ApiClient.getClient().create(QwerteachService.class);
            Call<JsonResponse> call = service.uploadAvatar(userId, body, email, token);
            call.enqueue(new Callback<JsonResponse>() {
                @Override
                public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                }

                @Override
                public void onFailure(Call<JsonResponse> call, Throwable t) {

                }
            });
        }
    }
}
