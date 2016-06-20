package hugo.alberto.community.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import hugo.alberto.community.Application.Application;
import hugo.alberto.community.Model.CommunityPost;
import hugo.alberto.community.R;

public class PostActivity extends AppCompatActivity {
    private EditText postEditText;
    private ImageView icon;
    private ImageButton button;
    private TextView characterCountTextView;
    private ArrayAdapter ad;
    private String data;
    private GridView grid;
    private int maxCharacterCount = Application.getConfigHelper().getPostMaxCharacterCount();
    private ParseGeoPoint geoPoint;
    public String[] imgId = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44"};
    private Integer[] images = {R.mipmap.ic_happy, R.mipmap.ic_angel, R.mipmap.ic_confused_2, R.mipmap.ic_blablabla, R.mipmap.ic_blank_face, R.mipmap.ic_blushing, R.mipmap.ic_closing_eyes, R.mipmap.ic_confused, R.mipmap.ic_angry, R.mipmap.ic_cool, R.mipmap.ic_crying, R.mipmap.ic_crying_laugh, R.mipmap.ic_dancing, R.mipmap.ic_devil, R.mipmap.ic_disgust, R.mipmap.ic_dissapointed, R.mipmap.ic_dreaming, R.mipmap.ic_epic_win, R.mipmap.ic_eurica, R.mipmap.ic_evil_laugh, R.mipmap.ic_eye_roll, R.mipmap.ic_eyes_closed, R.mipmap.ic_eyes_hearts, R.mipmap.ic_forgot, R.mipmap.ic_hand_smack, R.mipmap.ic_hello, R.mipmap.ic_hug, R.mipmap.ic_drop, R.mipmap.ic_joke, R.mipmap.ic_kiss, R.mipmap.ic_like, R.mipmap.ic_like_2, R.mipmap.ic_lol, R.mipmap.ic_love, R.mipmap.ic_mad, R.mipmap.ic_nerd, R.mipmap.ic_overworked, R.mipmap.ic_poop, R.mipmap.ic_pour, R.mipmap.ic_rock, R.mipmap.ic_scared, R.mipmap.ic_secret, R.mipmap.ic_sleeping, R.mipmap.ic_success, R.mipmap.ic_wink};
    private String postId = null;
    private CommunityPost post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        icon = (ImageView) findViewById(R.id.btn);
        grid = (GridView) findViewById(R.id.grid);
        button = (ImageButton) findViewById(R.id.btn);
        postEditText = (EditText) findViewById(R.id.post_edittext);

        ad = new CustomSpinnerAdapter(this, R.layout.custom_spinner, imgId);
        grid = (GridView) findViewById(R.id.grid);

        grid.setAdapter(ad);

        if (getIntent().hasExtra("ID")) {
            postId = getIntent().getExtras().getString("ID");
        }
        if (postId == null) {
            data = imgId[0];
            icon.setImageResource(images[0]);

            Intent intent = getIntent();
            Location location = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
            geoPoint = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        }

        if (postId != null) {
            ParseQuery<CommunityPost> query = CommunityPost.getQuery();
            query.whereEqualTo("uuid", postId);
            query.getFirstInBackground(new GetCallback<CommunityPost>() {

                @Override
                public void done(CommunityPost object, ParseException e) {
                    post = object;
                    /* isFinishing : Check to see whether this activity is in the process of finishing,
                    // either because you called finish() on it or someone else has requested that it finished.*/
                    if (!isFinishing()) {
                        data = imgId[Integer.parseInt(post.getFeeling())];
                        post = object;
                        postEditText.setText(post.getText());
                        icon.setImageResource(images[Integer.parseInt(post.getFeeling())]);
                    }
                }

            });
        }

        final FloatingActionButton postButton = (FloatingActionButton) findViewById(R.id.post);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (postId != null) {
                    update(post);
                } else {
                    post();
                }

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                grid.setVisibility(View.VISIBLE);
                button.setVisibility(View.GONE);
                postButton.setVisibility(View.GONE);
                hideKeyboard();
                grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                        TextView label = (TextView) findViewById(R.id.label);
                        label.setText(imgId[position]);
                        data = imgId[position];
                        icon.setImageResource(images[position]);
                        grid.setVisibility(View.GONE);
                        button.setVisibility(View.VISIBLE);
                        postButton.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        postEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateCharacterCountTextViewText();
            }
        });

        characterCountTextView = (TextView) findViewById(R.id.character_count_textview);
        updateCharacterCountTextViewText();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void update(CommunityPost post) {
        final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();

        post.put("text", postEditText.getText().toString().trim());
        post.put("feeling", String.valueOf(data));

        finish();
        try {
            post.save();
            dialog.dismiss();
            finish();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void post() {
        String text = postEditText.getText().toString().trim();
        final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
        dialog.setMessage(getString(R.string.progress_post));
        dialog.show();

        CommunityPost post = new CommunityPost();

        post.setLocation(geoPoint);
        post.setText(text);
        post.setUser(ParseUser.getCurrentUser());

        post.setFeeling(String.valueOf(data));

        Date currentDate = new Date(System.currentTimeMillis());
        post.setDate(currentDate);
        post.setUuidString();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(post.getLocation().getLatitude(), post.getLocation().getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            post.setAddress(address);

        } catch (IOException e) {
            e.printStackTrace();
        }

        ParseACL acl = new ParseACL();

        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);

        post.setACL(acl);

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void updateCharacterCountTextViewText() {
        String characterCountString = String.format("%d/%d", postEditText.length(), maxCharacterCount);
        characterCountTextView.setText(characterCountString);
    }

    public class CustomSpinnerAdapter extends ArrayAdapter<String> {

        public CustomSpinnerAdapter(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.custom_spinner, parent, false);

            TextView label = (TextView) row.findViewById(R.id.label);
            label.setText(imgId[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.imageView1);
            icon.setImageResource(images[position]);
            return row;
        }
    }
}
