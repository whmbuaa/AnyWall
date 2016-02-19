package com.parse.anywall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVACL;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;

/**
 * Activity which displays a login screen to the user, offering registration as well.
 */
public class PostActivity extends Activity {
  // UI references.
  private EditText postEditText;
  private TextView characterCountTextView;
  private Button postButton;

  private int maxCharacterCount = Application.getConfigHelper().getPostMaxCharacterCount();
  private AVGeoPoint geoPoint;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_post);

    Intent intent = getIntent();
    Bundle bundle = intent.getParcelableExtra(Application.INTENT_EXTRA_LOCATION);
    geoPoint = new AVGeoPoint(bundle.getDouble("latitude"), bundle.getDouble("longitude"));

    postEditText = (EditText) findViewById(R.id.post_edittext);
    postEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void afterTextChanged(Editable s) {
        updatePostButtonState();
        updateCharacterCountTextViewText();
      }
    });

    characterCountTextView = (TextView) findViewById(R.id.character_count_textview);

    postButton = (Button) findViewById(R.id.post_button);
    postButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        post();
      }
    });

    updatePostButtonState();
    updateCharacterCountTextViewText();
  }

  private void post () {
    String text = postEditText.getText().toString().trim();

    // Set up a progress dialog
    final ProgressDialog dialog = new ProgressDialog(PostActivity.this);
    dialog.setMessage(getString(R.string.progress_post));
    dialog.show();

    // Create a post.
    AnywallPost post = new AnywallPost();

    // Set the location to the current user's location
    post.setLocation(geoPoint);
    post.setText(text);
    post.setUser(AVUser.getCurrentUser());
    AVACL acl = new AVACL();

    // Give public read access
    acl.setPublicReadAccess(true);
    post.setACL(acl);

    // Save the post
    post.saveInBackground(new SaveCallback() {
      @Override
      public void done(AVException e) {
        dialog.dismiss();
        finish();
      }
    });
  }

  private String getPostEditTextText () {
    return postEditText.getText().toString().trim();
  }

  private void updatePostButtonState () {
    int length = getPostEditTextText().length();
    boolean enabled = length > 0 && length < maxCharacterCount;
    postButton.setEnabled(enabled);
  }

  private void updateCharacterCountTextViewText () {
    String characterCountString = String.format("%d/%d", postEditText.length(), maxCharacterCount);
    characterCountTextView.setText(characterCountString);
  }
}
