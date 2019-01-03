package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.restlet.Client;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;

public class FlashMobListActivity extends AppCompatActivity {
    private ListView lv;
    private FlashMob[] lista;
    private FlashMobListActivity thisActivity;
    private String baseUri = "http://192.168.2.117:8182/content/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_mob_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        thisActivity=this;
        lv = (ListView) findViewById(R.id.list);
        new ListGetterTask().execute();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_with_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.logout_item:
                SharedPreferences preferences = getSharedPreferences("Login_Reg_Activity", MODE_PRIVATE);

                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                //Toast.makeText(this,preferences.toString(),Toast.LENGTH_LONG).show();

                startActivity(new Intent(this, Login_Reg_Activity.class));


                return true;

            default:
                return false;
        }
        //noinspection SimplifiableIfStatement


    }


    public void refresh(View v) {
        new ListGetterTask().execute();
    }

 //   @Override
 //   public void onBackPressed() {

 //   }



    public class ListGetterTask extends AsyncTask<String, Void, String> {
        private String response;

        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;

            cr = new ClientResource(baseUri+"/flashmob/list");
            String response = null;


            try {
                response = cr.get().getText();
            } catch (ResourceException | IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Getting List", text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res != null) {


                lista = new Gson().fromJson(res, FlashMob[].class);
                Log.e("Getting List", lista.toString());

                lv.setAdapter(new FlashMobAdapter(getApplicationContext(), lista));
                lv.setTextFilterEnabled(true);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override


                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FlashMob item = lista[position];
                        Date currentDate = new Date();

                        if (item.getStart().before(currentDate) && item.getEnd().before(currentDate)) {
                            //flash mob passato
                            Intent galleryIntent = new Intent(getApplicationContext(), Gallery.class);
                            galleryIntent.putExtra("FlashMobName", lista[position].getName());
                            startActivity(galleryIntent);
                        }
                        else {
                            //flash mob passato // oppute futuro
                            MenuDialogFragment.newInstance(lista[position].getName()).show(getFragmentManager(), "Menu");
                            Log.i("Creation of dialog", "Il dialog è stato creato");
                        }

                    }
                });

                Toast.makeText(getApplicationContext(), "DownloadList: OK", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "DownloadList: Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class PermissionGetterTask extends AsyncTask<String, Void, String> {
        private String response;
        private int response_code;
        private String fmName;

        @Override
        protected String doInBackground(String... params) {

            ClientResource cr;
            fmName= params[0];
            SharedPreferences pref = getSharedPreferences("Login_Reg_Activity", MODE_PRIVATE);
            String user = pref.getString("username",null);
            String pass = pref.getString("password", null);
            cr = new ClientResource(baseUri+"/flashmob/"+params[0]+"/authorization");

            String response = null;
            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme,
                    user,pass);
            cr.setChallengeResponse(authentication);

            try {
                response = cr.get().getText();
                response_code=cr.getStatus().getCode();
                Log.e("Accesso OK", response);
                if(response_code == ErrorCodes.EXPIRED_FLASHMOB){

                    throw new Gson().fromJson(response,ExpiredFlashMobException.class);
                } else if(response_code == ErrorCodes.FUTURE_FLASHMOB){

                    throw new Gson().fromJson(response,FutureFlashMobException.class);

                } else if(response_code == ErrorCodes.UNREGISTERED_USER){

                    throw new Gson().fromJson(response,UnregisteredUserException.class);
                }

                //se sono qui allora il codice è 200  e response vale "OK";



            } catch (ResourceException | IOException e) {
                String text = "Error caused by ResourceException: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                response = text;
                Log.e("Getting Permission", text);
            }catch(ExpiredFlashMobException e){
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                response=e.getMessage();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Getting Permission", response);
            }
            catch (FutureFlashMobException e){
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                response=e.getMessage();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Getting Permission", response);
            }
            catch(UnregisteredUserException e){
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                response=e.getMessage();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Getting Permission", text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res != null) {
                if(response_code == 200){
                    //permesso concesso
                    Toast.makeText(getApplicationContext(), "Sei autorizzato ad effetuare questa operazione", Toast.LENGTH_LONG).show();
                    Intent imagePicker = new Intent(getApplicationContext(),CameraActivity.class);
                    imagePicker.putExtra("FlashMobName", fmName);
                    startActivity(imagePicker);


                }
                else{
                    //non ho ottenuto il permesso di procedere, ma sono Registrato per questoflashMob
                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
                }
            } else {
                //response == null vuol dire che l'utente non è
                Toast.makeText(getApplicationContext(), "Non sei autorizzato ad effetuare questa operazione", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RegistrationFMTask extends AsyncTask<String, Void, String> {
        private String response;
        private int response_code;
        private String fmName;

        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;
            fmName= params[0];
            SharedPreferences pref = getSharedPreferences("Login_Reg_Activity", MODE_PRIVATE);
            String user = pref.getString("username",null);
            String pass = pref.getString("password", null);
            cr = new ClientResource(baseUri+"/flashmob/"+params[0]);
            String response = null;
            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme,
                    user,pass);
            cr.setChallengeResponse(authentication);

            try {
                response = cr.post(user).getText();
                response_code=cr.getStatus().getCode();
                if(response_code == ErrorCodes.EXPIRED_FLASHMOB){
                    response_code=cr.getStatus().getCode();
                    throw new Gson().fromJson(response,ExpiredFlashMobException.class);
                }


                //se sono qui allora il codice è 200  e response vale "OK";



            } catch (ResourceException | IOException e) {
                response = "Error caused by resourceException: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Registrarion: ", response);
            }catch(ExpiredFlashMobException e){
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription() + " - " + cr.getStatus().getReasonPhrase();
                response=e.getMessage();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
                Log.e("Registration: ", text);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res != null) {
                if(response_code == 200){
                    //permesso concesso
                    Toast.makeText(getApplicationContext(), "Sei stato registrato con successo", Toast.LENGTH_LONG).show();
                    //Intent imagePicker = new Intent(getApplicationContext(),CameraActivity.class);
                    //imagePicker.putExtra("FlashMobName", fmName);
                    //startActivity(imagePicker);


                }
                else{
                    //registrazion fallita
                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_LONG).show();
                }
            } else {
                //response == null vuol dire che l'utente non è
                if(response_code == 403) Toast.makeText(getApplicationContext(), "Non sei autorizzato ad effetuare questa operazione", Toast.LENGTH_LONG).show();
                if(response_code == 404) Toast.makeText(getApplicationContext(), "Non è stato trovato nessun flashMob con questo nome", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void invokePermissionGetter(String fmName){
        new PermissionGetterTask().execute(fmName);
    }
    private void registerForFM(String fmName){
        new RegistrationFMTask().execute(fmName);
    }

    public static class MenuDialogFragment extends DialogFragment {



        public static MenuDialogFragment newInstance(String flashMobName) {
            MenuDialogFragment d = new MenuDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("fmName", flashMobName);
            d.setArguments(args);
            Log.i("INSTANZIAZIONE", "Metodo newInstance() eseguito");
            return d;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {


            super.onCreate(savedInstanceState);
            final String fmName = getArguments().getString("fmName");



            final FlashMobListActivity acc = (FlashMobListActivity) getActivity();

            final Dialog myDialog = new Dialog(acc);
            myDialog.setContentView(R.layout.dialog_layout);


            Button post = (Button) myDialog.findViewById(R.id.post);
            Button register = (Button) myDialog.findViewById(R.id.register);

            post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acc.invokePermissionGetter(fmName);
                    myDialog.dismiss();
                }
            });



            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acc.registerForFM(fmName);
                }
            });


            return myDialog;
        }
    }

}




