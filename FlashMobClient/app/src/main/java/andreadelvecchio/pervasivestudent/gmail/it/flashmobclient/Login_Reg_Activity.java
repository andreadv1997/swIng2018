package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import java.io.File;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class Login_Reg_Activity extends AppCompatActivity {

    private String baseUri = "http://192.168.2.117:8182/content/user";
    //private String baseUri = "http://192.168.43.60:8182/content/user";

    EditText username;
    EditText password;
    String user_pref;
    String pass_pref;
    SharedPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPreferences = getPreferences(MODE_PRIVATE);
        user_pref = myPreferences.getString("username","");
        pass_pref= myPreferences.getString("password","");
        if(!user_pref.equals("") && !pass_pref.equals(""))
            new LoginTask().execute(user_pref,pass_pref);
        else {
            setContentView(R.layout.activity_login__reg_);
            username= (EditText) findViewById(R.id.userText);
            password= (EditText) findViewById(R.id.passText);

        }

    }

    @Override
    public void onBackPressed(){

    }




    public void registration(View v){
            new RegistrationTask().execute(username.getText().toString(),password.getText().toString());
    }


    public void login(View v){
        new LoginTask().execute(username.getText().toString(),password.getText().toString());
    }

    public class RegistrationTask extends AsyncTask<String, Void, String> {
        private String response;
        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;

            cr = new ClientResource(baseUri + "/registration");
            String response=null;
            MyUser user = new MyUser(username.getText().toString(),password.getText().toString());
            String payload= new Gson().toJson(user,User.class);

            try {
                response = cr.put(payload).getText();
                if(cr.getStatus().getCode() == ErrorCodes.USERNAME_ALREADY_EXISTING)
                    throw new Gson().fromJson(response,AlreadyExistingException.class);

                if(cr.getStatus().getCode() == ErrorCodes.EMPTY_CREDENTIALS)
                    throw new Gson().fromJson(response,EmptyCredentialsException.class);
            } catch (ResourceException|IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Registration",text);
                response = text;
            } catch(AlreadyExistingException e){
                response = e.getMessage();
                Log.e("Registration",response);
            } catch (EmptyCredentialsException e){
                response = e.getMessage();
                Log.e("Registration",response);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
           if(res!=null){
                if (res.equals("OK")) {
                    Toast.makeText(getApplicationContext(), "Registration Status: " + res, Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = myPreferences.edit();
                    editor.putString("username", username.getText().toString());
                    editor.putString("password", password.getText().toString());
                    editor.commit();

                    startActivity(new Intent(getApplicationContext(), FlashMobListActivity.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
                }
           }
        }
    }

    public class LoginTask extends AsyncTask<String, Void, String> {
        private String response;
        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;

            cr = new ClientResource(baseUri + "/authentication");
            String response=null;
            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme,
                    params[0],params[1]);
            cr.setChallengeResponse(authentication);

            try {
                response = cr.get().getText();
            } catch (ResourceException|IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Login",text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res != null) {
                if (res.equals("OK")) {
                    Toast.makeText(getApplicationContext(), "Login Status: " + res, Toast.LENGTH_LONG).show();
                    if (username != null && password != null) {
                        SharedPreferences.Editor editor = myPreferences.edit();
                        editor.putString("username", username.getText().toString());
                        editor.putString("password", password.getText().toString());
                        editor.commit();
                    }


                    startActivity(new Intent(getApplicationContext(), FlashMobListActivity.class));
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Utente non autenticato: controlla le tue credenziali" , Toast.LENGTH_LONG).show();
            }
        }
    }

}
