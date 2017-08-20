package com.qwerteach.wivi.qwerteachapp.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.interfaces.QwerteachService;
import com.qwerteach.wivi.qwerteachapp.models.ApiClient;
import com.qwerteach.wivi.qwerteachapp.models.JsonResponse;
import com.qwerteach.wivi.qwerteachapp.models.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

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

public class DescriptionTabFragment extends Fragment implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    EditText firstNameEditText, lastNameEditText, birthDateEditText, phoneNumberEditText;
    View view;
    ProgressDialog progressDialog;
    Button saveInfosButton, updateUserAvatarButton;
    User user;
    ImageView userAvatar;
    QwerteachService service;
    CountryCodePicker ccp;
    DatePickerDialog dialog;

    public static DescriptionTabFragment newInstance() {
        DescriptionTabFragment descriptionTabFragment = new DescriptionTabFragment();
        return descriptionTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            user = (User) getActivity().getIntent().getSerializableExtra("user");
        }

        progressDialog = new ProgressDialog(getContext());
        service = ApiClient.getClient().create(QwerteachService.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.fragment_description_tab, container, false);

        Calendar now = Calendar.getInstance();
        dialog = DatePickerDialog.newInstance(this, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dialog.setMaxDate(java.util.Calendar.getInstance());

        firstNameEditText = (EditText) view.findViewById(R.id.firstname);
        lastNameEditText = (EditText) view.findViewById(R.id.lastname);
        birthDateEditText = (EditText) view.findViewById(R.id.birthdate);
        phoneNumberEditText = (EditText) view.findViewById(R.id.phoneNumber);
        saveInfosButton = (Button) view.findViewById(R.id.save_infos_button);
        userAvatar = (ImageView) view.findViewById(R.id.user_avatar);
        updateUserAvatarButton = (Button) view.findViewById(R.id.update_user_avatar_button);
        ccp = (CountryCodePicker) view.findViewById(R.id.ccp);

        birthDateEditText.setOnClickListener(this);
        saveInfosButton.setOnClickListener(this);
        updateUserAvatarButton.setOnClickListener(this);

        displayUserInfos();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void displayUserInfos() {
        firstNameEditText.setText(user.getFirstName());
        lastNameEditText.setText(user.getLastName());
        birthDateEditText.setText(user.getBirthdate());
        phoneNumberEditText.setText(user.getPhoneNumber());
        Picasso.with(getContext()).load(user.getAvatarUrl()).resize(150, 150).centerCrop().into(userAvatar);
        if(user.getPhoneCountryCode() != null) {
            ccp.setCountryForPhoneCode(Integer.parseInt(user.getPhoneCountryCode()));
        }
    }

    public void startSaveInfosProfile() {
        String zone = TimeZone.getDefault().getID();
        int slash = zone.indexOf('/');
        zone = zone.substring(slash + 1).replace("_", " ");
        Log.d("ZONE", zone);

        User newUser = new User();
        newUser.setFirstName(firstNameEditText.getText().toString());
        newUser.setLastName(lastNameEditText.getText().toString());
        newUser.setBirthdate(birthDateEditText.getText().toString());
        newUser.setPhoneNumber(phoneNumberEditText.getText().toString());
        newUser.setPhoneCountryCode(ccp.getSelectedCountryCode());
        newUser.setTimeZone(zone);

        Map<String, User> requestBody = new HashMap<>();
        requestBody.put("user", newUser);

        Call<JsonResponse> call = service.getStudentInfos(user.getUserId(), requestBody, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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
                    Gson gson = new Gson();
                    String json = gson.toJson(userResponse);
                    editor.putString("user", json);
                    editor.apply();

                } else {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    displayUserInfos();
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
                dialog.show(getActivity().getFragmentManager(), "datePickerDialog");
                break;
            case R.id.save_infos_button:
                startProgressDialog();
                startSaveInfosProfile();
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

    public void uploadFile(Uri image) {
        File file = new File(image.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("user[avatar]", file.getName(), requestFile);

        Call<JsonResponse> call = service.uploadAvatar(user.getUserId(), body, user.getEmail(), user.getToken());
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {
                Log.i("UPLOAD_AVATAR", "OK");
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("ERROR", t.toString());

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri).setMaxCropResultSize(2100, 2100).start(getContext(), this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso.with(getContext()).load(resultUri.toString()).resize(150, 150).centerCrop().into(userAvatar);
                verifyStoragePermissions(getActivity());
                uploadFile(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.d("ERROR", result.getError().toString());
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        birthDateEditText.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
    }
}
