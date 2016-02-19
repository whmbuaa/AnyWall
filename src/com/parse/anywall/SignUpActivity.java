package com.parse.anywall;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;

/**
 * Activity which displays a login screen to the user.
 */
public class SignUpActivity extends Activity {
  // UI references.
  private EditText usernameEditText;
  private EditText passwordEditText;
  private EditText passwordAgainEditText;
  

  private EditText mEtMobileNumber;
  private EditText mEtSms;

  

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_signup);

    // Set up the signup form.
    usernameEditText = (EditText) findViewById(R.id.username_edit_text);

    passwordEditText = (EditText) findViewById(R.id.password_edit_text);
    passwordAgainEditText = (EditText) findViewById(R.id.password_again_edit_text);
    
    mEtMobileNumber = (EditText) findViewById(R.id.et_mobile_number);
    mEtSms = (EditText) findViewById(R.id.et_sms);
    
    passwordAgainEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == R.id.edittext_action_signup ||
            actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
          signup();
          return true;
        }
        return false;
      }
    });

    // Set up the submit button click handler
    Button mActionButton = (Button) findViewById(R.id.action_button);
    mActionButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        signup();
      }
    });
    
    findViewById(R.id.bt_send_sms).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			  AVUser.requestMobilePhoneVerifyInBackground(mEtMobileNumber.getText().toString(),new RequestMobileCodeCallback() {
			
				      @Override
				      public void done(AVException e) {
				          //发送了验证码以后做点什么呢
				        if (e != null) {
				          // Show the error message
				          Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				        }
				      }
				    });
		}
	});
    
    findViewById(R.id.bt_verify_sms).setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			  AVUser.requestMobilePhoneVerifyInBackground(mEtSms.getText().toString(),new RequestMobileCodeCallback() {
			
				      @Override
				      public void done(AVException e) {
				          //发送了验证码以后做点什么呢
				        if (e != null) {
				          // Show the error message
				          Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
				        }
				        else{
				        	switchToSignup();
				        }
				      }
					
				    });
		}
	});
    
  }
  
  private void switchToSignup() {
		// TODO Auto-generated method stub
		findViewById(R.id.sms_verification_contaier).setVisibility(View.GONE);
		findViewById(R.id.signup_container).setVisibility(View.VISIBLE);
	}

  private void signup() {
	  
	  
    String username = usernameEditText.getText().toString().trim();
    String password = passwordEditText.getText().toString().trim();
    String passwordAgain = passwordAgainEditText.getText().toString().trim();

    // Validate the sign up data
    boolean validationError = false;
    StringBuilder validationErrorMessage = new StringBuilder(getString(R.string.error_intro));
    if (username.length() == 0) {
      validationError = true;
      validationErrorMessage.append(getString(R.string.error_blank_username));
    }
    if (password.length() == 0) {
      if (validationError) {
        validationErrorMessage.append(getString(R.string.error_join));
      }
      validationError = true;
      validationErrorMessage.append(getString(R.string.error_blank_password));
    }
    if (!password.equals(passwordAgain)) {
      if (validationError) {
        validationErrorMessage.append(getString(R.string.error_join));
      }
      validationError = true;
      validationErrorMessage.append(getString(R.string.error_mismatched_passwords));
    }
    validationErrorMessage.append(getString(R.string.error_end));

    // If there is a validation error, display the error
    if (validationError) {
      Toast.makeText(SignUpActivity.this, validationErrorMessage.toString(), Toast.LENGTH_LONG)
          .show();
      return;
    }

    // Set up a progress dialog
    final ProgressDialog dialog = new ProgressDialog(SignUpActivity.this);
    dialog.setMessage(getString(R.string.progress_signup));
    dialog.show();

    // Set up a new Parse user
    AVUser user = new AVUser();
    user.setUsername(username);
    user.setPassword(password);
    user.setMobilePhoneNumber(mEtMobileNumber.getText().toString());

    // Call the Parse signup method
    user.signUpInBackground(new SignUpCallback() {
      @Override
      public void done(AVException e) {
        dialog.dismiss();
        if (e != null) {
          // Show the error message
          Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
          // Start an intent for the dispatch activity
          Intent intent = new Intent(SignUpActivity.this, DispatchActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        }
      }
    });
	  
	  
	  
  }
}
