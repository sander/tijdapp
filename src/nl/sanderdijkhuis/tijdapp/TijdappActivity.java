package nl.sanderdijkhuis.tijdapp;

import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.AndTerm;
import javax.mail.search.SubjectTerm;

import com.sun.mail.imap.IMAPFolder;

public class TijdappActivity extends Activity {
  private TextView failure;
  private EditText email;
  private EditText password;
  private RadioButton nlBox;
  private RadioButton enBox;
  private Button button;
  private TextView label;
  private TextView number;
  private ProgressDialog loading;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    
    failure = (TextView) findViewById(R.id.failure);
    hideFailure();
    
    email = (EditText) findViewById(R.id.email);
    password = (EditText) findViewById(R.id.password);
    nlBox = (RadioButton) findViewById(R.id.nl);
    enBox = (RadioButton) findViewById(R.id.en);
    
    button = (Button) findViewById(R.id.signIn);
    button.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        TijdappActivity.this.doIt();
      }
    });
    
    label = (TextView) findViewById(R.id.numberLabel);
    number = (TextView) findViewById(R.id.number);
    
    hideResult();
  }
  
  private class MailTask extends AsyncTask<String, Void, Integer> {
    protected Integer doInBackground(String... data) {
      try {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        
        Session session = Session.getInstance(props, null);
        
        Store store = session.getStore("imaps");
        store.connect("imap.gmail.com", (String) email.getText().toString(), (String) password.getText().toString());
        
        String folderName;
        if (nlBox.isChecked()) folderName = "[Gmail]/Alle berichten";
        else folderName = "[Gmail]/All Mail";
        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);
        
        IMAPFolder f = (IMAPFolder) folder;
        
        SearchTerm term = new AndTerm(
            new SubjectTerm("Tijd"),
            new AndTerm(new OrTerm[]{
                new OrTerm(new SearchTerm[]{
                    new FromTerm(new InternetAddress("aliradicali")),
                    new RecipientTerm(Message.RecipientType.TO, new InternetAddress("aliradicali")),
                    new RecipientTerm(Message.RecipientType.CC, new InternetAddress("aliradicali"))
                }),
                new OrTerm(new SearchTerm[]{
                    new FromTerm(new InternetAddress("elmervaneeghen")),
                    new RecipientTerm(Message.RecipientType.TO, new InternetAddress("elmervaneeghen")),
                    new RecipientTerm(Message.RecipientType.CC, new InternetAddress("elmervaneeghen"))
                }),
                new OrTerm(new SearchTerm[]{
                    new FromTerm(new InternetAddress("tychoang")),
                    new RecipientTerm(Message.RecipientType.TO, new InternetAddress("tychoang")),
                    new RecipientTerm(Message.RecipientType.CC, new InternetAddress("tychoang"))
                }),
                new OrTerm(new SearchTerm[]{
                    new FromTerm(new InternetAddress("samdekker")),
                    new RecipientTerm(Message.RecipientType.TO, new InternetAddress("samdekker")),
                    new RecipientTerm(Message.RecipientType.CC, new InternetAddress("samdekker"))
                }),
            })
        );
        
        Message[] messages = f.search(term);
        return messages.length;
      } catch (Exception e) {
        return -1;
      }
    }
    
    protected void onPostExecute(Integer result) {
      loading.hide();
      if (result == -1) {
        hideResult();
        showFailure();
      } else {
        number.setText(result.toString());
        showResult();
      }
    }
  }
  
  private void doIt() {
    hideFailure();
    hideResult();
    
    loading = ProgressDialog.show(TijdappActivity.this, "",
        "Laden…", true);
    
    new MailTask().execute();
  }
  
  private void hideFailure() {
    failure.setVisibility(View.INVISIBLE);
  }
  
  private void hideResult() {
    number.setVisibility(View.INVISIBLE);
    label.setVisibility(View.INVISIBLE);
  }
  
  private void showResult() {
    loading.hide();
    
    label.setVisibility(View.VISIBLE);
    number.setVisibility(View.VISIBLE);
  }
  
  private void showFailure() {
    failure.setVisibility(View.VISIBLE);
  }
}