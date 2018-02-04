package posener.poppit.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.facebook.login.widget.ProfilePictureView;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import posener.poppit.interfaces.MainActivityComm;
import posener.poppit.adapters.MiniUserAdapter;
import posener.poppit.objects.Question;
import posener.poppit.R;

/**
 * Created by Or on 06/06/2015.
 */
public class InflaterFragment extends Fragment {
    TextView username, followup_text, followup_content;
    EditText questionText;
    GridView recipientsGrid;
    ArrayList<ParseUser> recipients_list;
    MainActivityComm comm;
    MiniUserAdapter miniUserAdapter;
    ProfilePictureView profilePictureView;
    ViewFlipper fields_flipper;
    RadioGroup module_selector;
    ImageView first_image, second_image;
    ParseObject followup;
    private Bitmap firstSelectedImage, secondSelectedImage;
    private int qType = 0;

    private static final int SELECT_FIRST_PHOTO = 101;
    private static final int SELECT_SECOND_PHOTO = 102;
    private static final int CAPTURE_FIRST_PHOTO = 103;
    private static final int CAPTURE_SECOND_PHOTO = 104;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (RelativeLayout) inflater.inflate(R.layout.fragment_inflater, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        comm = (MainActivityComm) getActivity();

        recipientsGrid = (GridView) getActivity().findViewById(R.id.recipients_grid);
        questionText = (EditText) getActivity().findViewById(R.id.question_box);
        username = (TextView) getActivity().findViewById(R.id.user_name);
        followup_text = (TextView) getActivity().findViewById(R.id.followup_text);
        followup_content = (TextView) getActivity().findViewById(R.id.followup_question);
        profilePictureView = (ProfilePictureView) getActivity().findViewById(R.id.inflater_profile_picture);
        module_selector = (RadioGroup) getActivity().findViewById(R.id.module_selector);
        recipients_list = new ArrayList<ParseUser>();


        fields_flipper = (ViewFlipper) getActivity().findViewById(R.id.fields_flipper);

        Button popQuestion = (Button) getActivity().findViewById(R.id.btn_pop_question);
        popQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                popQuestion(v);
            }
        });
        Button editRecipients = (Button) getActivity().findViewById(R.id.edit_recipients_button);
        editRecipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comm.SelectFriends(recipients_list);
            }
        });


        module_selector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.module_selector_yn) {
                    fields_flipper.setDisplayedChild(0);
                    qType = Question.TYPE_YN;
                }
                if (checkedId == R.id.module_selector_custom) {
                    fields_flipper.setDisplayedChild(1);
                    qType = Question.TYPE_CUSTOM;
                }
                if (checkedId == R.id.module_selector_image) {
                    fields_flipper.setDisplayedChild(2);
                    qType = Question.TYPE_IMAGE;
                }

            }
        });
        miniUserAdapter = new MiniUserAdapter(getActivity(), new ArrayList<ParseUser>());
        recipientsGrid.setAdapter(miniUserAdapter);
        ParseUser currentUser = ParseUser.getCurrentUser();
        username.setText(currentUser.getString("name"));
        profilePictureView.setProfileId(currentUser.get("facebook_id").toString());

        first_image = (ImageView) getActivity().findViewById(R.id.first_option_image);
        second_image = (ImageView) getActivity().findViewById(R.id.second_option_image);

        ImageButton img_select_button_1 = (ImageButton) getActivity().findViewById(R.id.first_img_select_button);
        ImageButton img_select_button_2 = (ImageButton) getActivity().findViewById(R.id.second_img_select_button);
        ImageButton img_capture_button_1 = (ImageButton) getActivity().findViewById(R.id.first_img_capture_button);
        ImageButton img_capture_button_2 = (ImageButton) getActivity().findViewById(R.id.second_img_capture_button);
        img_select_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_FIRST_PHOTO);
            }
        });
        img_select_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_SECOND_PHOTO);
            }
        });
        img_capture_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, CAPTURE_FIRST_PHOTO);
            }
        });
        img_capture_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(captureIntent, CAPTURE_SECOND_PHOTO);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public void setRecipients(ArrayList<ParseUser> recipients){
        recipients_list = recipients;
        miniUserAdapter.addAll(recipients);
        miniUserAdapter.notifyDataSetChanged();
    }

    public void setFollowUp(ParseObject question, ArrayList<ParseUser> recipients, int option){
        setRecipients(recipients);
        followup = question;
        followup_text.setVisibility(View.VISIBLE);
        followup_content.setText(followup.getString("content"));
        followup_content.setVisibility(View.VISIBLE);
    }

    public void popQuestion(View view){
        if(!questionText.getText().equals("") && !recipients_list.isEmpty()) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            try {
                currentUser.fetchIfNeeded();
            } catch (ParseException e) {
                Log.d("Fetching - ", e.getMessage());
            }
            String text = questionText.getText().toString();
            ParseObject question = new ParseObject("Questions");
            question.put("user_id", currentUser);
            question.put("content", text);
            question.put("recipients", recipients_list);
            question.put("type", qType);
            if(followup!=null)
                question.put("followup", followup);
            switch (qType) {
                case Question.TYPE_YN:
                    PostQuestion(question);
                    break;
                case Question.TYPE_CUSTOM:
                    TextView foTV = (TextView) getActivity().findViewById(R.id.inflater_field_first_option_text);
                    TextView soTV = (TextView) getActivity().findViewById(R.id.inflater_field_second_option_text);
                    String fT = foTV.getText().toString();
                    String sT = soTV.getText().toString();
                    if(!fT.isEmpty() && !sT.isEmpty());
                    question.put("first_option", fT);
                    question.put("second_option", sT);
                    PostQuestion(question);
                    break;
                case Question.TYPE_IMAGE:
                    // Convert it to byte
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                    // Compress image to lower quality scale 1 - 100
                    firstSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                    secondSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream2);
                    byte[] image1 = stream1.toByteArray();
                    byte[] image2 = stream2.toByteArray();

                    // Create the ParseFile

                    ParseFile file1 = new ParseFile("q_image.png", image1);
                    ParseFile file2 = new ParseFile("q_image.png", image2);
                    // Upload the image into Parse Cloud
                    try{
                        file1.save();
                        file2.save();
                    }catch (ParseException e){
                        Log.d("Upload - ", e.getMessage());
                    }


                    // Create a column named "ImageFile" and insert the image
                    question.put("first_option_img", file1);
                    question.put("second_option_img", file2);

                    // Create the class and the columns
                    PostQuestion(question);
                    break;
            }
        }
        else{
            Log.d("INSERT - ","You must type a question and have recipients");
        }

    }
    private void PostQuestion(ParseObject question){
        ParseUser currentUser = ParseUser.getCurrentUser();
        try {
            question.save();
        } catch (ParseException e) {
            Log.d("Popper - ", e.getMessage());
        }
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereContainedIn("user", recipients_list);
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        if (recipients_list.size() == 1) {
            if (!currentUser.has("recent_friends")) {
                currentUser.put("recent_friends", recipients_list);
                currentUser.saveInBackground();
            } else {
                List<ParseUser> recent = currentUser.getList("recent_friends");
                if (recent.contains(recipients_list.get(0))) {
                    recent.remove(recipients_list.get(0));
                    recent.add(0, recipients_list.get(0));
                    currentUser.put("recent_friends", recent);
                    currentUser.saveInBackground();
                } else {
                    recent.add(0, recipients_list.get(0));
                    currentUser.put("recent_friends", recent);
                    currentUser.saveInBackground();
                }
            }
        }
        JSONObject jPush = new JSONObject();
        try {
            jPush.put("type", "question");
            jPush.put("question", question.getObjectId());
        } catch (JSONException e) {
            Log.d("JSON - ", e.getMessage());
        }
        push.setData(jPush);
        push.sendInBackground();
        ClearFields();
        Question q = new Question(question);
        Log.d("question content - ", q.content);
        comm.SelectFriends(new ArrayList<ParseUser>());
        SeeQuestion(q);
    }
    public void ClearFields(){
        questionText.setText("");
        recipients_list.clear();
        miniUserAdapter.clear();
        miniUserAdapter.notifyDataSetChanged();
    }

    public void SeeQuestion(Question question){
        Log.d("Sending question to activity - ", question.content);
        comm.ViewPostedQuestion(question);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == SELECT_FIRST_PHOTO || requestCode == SELECT_SECOND_PHOTO || requestCode == CAPTURE_FIRST_PHOTO || requestCode == CAPTURE_SECOND_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                /*String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getActivity().getContentResolver().query(
                        selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();*/

                if (requestCode == SELECT_FIRST_PHOTO || requestCode == CAPTURE_FIRST_PHOTO) {
                    try {
                        firstSelectedImage = decodeUri(selectedImage);
                    }catch (FileNotFoundException e){
                    Log.d("IMG ", e.getMessage());//BitmapFactory.decodeFile(filePath);
                    }
                    first_image.setImageBitmap(firstSelectedImage);
                }
                else {
                    try {
                        secondSelectedImage = decodeUri(selectedImage);
                    }catch (FileNotFoundException e){
                        Log.d("IMG ", e.getMessage());//BitmapFactory.decodeFile(filePath);
                    }
                    //secondSelectedImage = BitmapFactory.decodeFile(filePath);
                    second_image.setImageBitmap(secondSelectedImage);
                }
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 140;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(selectedImage), null, o2);

    }
}
