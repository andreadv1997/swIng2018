package andreadelvecchio.pervasivestudent.gmail.it.flashmobclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    private String baseURI = "http://10.0.2.2:8182/content/user/flashmob/";


    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private static final int ACTIVITY_START_GALLERY_APP = 1;
    private ImageView photoTakenImageView;
    private Button load;
    private String mImageFileLocation = "";
    private String fmName;
    public final static String PERMISSION_WRITE_STORAGE ="android.permission.WRITE_EXTERNAL_STORAGE";
    public final static String PERMISSION_READ_STORAGE ="android.permission.READ_EXTERNAL_STORAGE";
    public final static int MY_PERMISSIONS_REQUEST_CAMERA=101;
    public final static int MY_PERMISSIONS_REQUEST_GALLERY=102;


    public void onCreate(Bundle savedInstanceState) {
        fmName = getIntent().getStringExtra("FlashMobName");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_picture);
        photoTakenImageView = (ImageView) findViewById(R.id.Prewiev);
        load = (Button) findViewById(R.id.butLoad);
        load.setEnabled(false);

    }

    private boolean checkPermissionCamera() {

        int result = ContextCompat.checkSelfPermission(this,PERMISSION_WRITE_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermisisonCamera(){

        ActivityCompat.requestPermissions(this,
                new String[]{PERMISSION_WRITE_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);

    }

    private void requestPermisisonGallery(){

        ActivityCompat.requestPermissions(this,
                new String[]{PERMISSION_READ_STORAGE}, MY_PERMISSIONS_REQUEST_GALLERY);

    }

    private boolean checkPermissionGallery() {

        int result = ContextCompat.checkSelfPermission(this,PERMISSION_READ_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }




    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhotAfterPermission();
            } else {
                Toast.makeText(getApplicationContext(), "No Permnissions for Camera", Toast.LENGTH_LONG).show();
            }
            return;
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_GALLERY) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadFromGallery();
            } else {
                Toast.makeText(getApplicationContext(), "No Permnissions for Gallery", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }

    public void takePhoto(View v){
        if (checkPermissionCamera()){
            takePhotAfterPermission();
        } else {
            requestPermisisonCamera();
        }
    }

    public void takePhotoGallery(View v){
        if (checkPermissionGallery()){
            loadFromGallery();
        } else {
            requestPermisisonGallery();
        }
    }

    public void loadFromGallery(){
        Intent galleryApplicationIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        Uri data = Uri.parse(pictureDirectoryPath);

        galleryApplicationIntent.setDataAndType(data, "image/*");

        startActivityForResult(galleryApplicationIntent, ACTIVITY_START_GALLERY_APP);
    }

    public void takePhotAfterPermission() {
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Uri outputUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                photoFile);
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        callCameraApplicationIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    public void uploadPhoto(View view){
        Toast.makeText(this, "Uploading: " + mImageFileLocation, Toast.LENGTH_SHORT).show();
        new PostContentTask().execute(mImageFileLocation);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {

            /** The code that handles the preview for the photo */
            Bitmap PhotoTakenBitmap = BitmapFactory.decodeFile(mImageFileLocation);
            // Assign the bitmap to the ImageView
            photoTakenImageView.setImageBitmap(PhotoTakenBitmap);
            load.setEnabled(true);
        }
        else if (requestCode == ACTIVITY_START_GALLERY_APP && resultCode == RESULT_OK){
            Uri imageUri = data.getData();



            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(imageUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mImageFileLocation = cursor.getString(columnIndex);
            cursor.close();


            //mImageFileLocation = imageUri.getPath();
            Log.println(Log.INFO,"FILE GALLERY", mImageFileLocation);
            InputStream inputStream;

            try {
                inputStream = getContentResolver().openInputStream(imageUri);

                Bitmap image  = BitmapFactory.decodeStream(inputStream);
                photoTakenImageView.setImageBitmap(image);
                load.setEnabled(true);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * The function that specifies the location and the name of the file that we want to create
     */
    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmSS").format(new Date());
        String imageFileName = "IMAGE_" + timeStamp+".jpg";
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/photo_saving_app");
        if (!storageDirectory.exists()) storageDirectory.mkdir();
        File image = new File(storageDirectory+ File.separator+imageFileName);
        mImageFileLocation = image.getAbsolutePath();
        return image;
    }




    public class PostContentTask extends AsyncTask<String, Void, String> {
        private String response;
        @Override
        protected String doInBackground(String... params) {
            ClientResource cr;
            SharedPreferences pref = getSharedPreferences("Login_Reg_Activity", MODE_PRIVATE);
            String user = pref.getString("username",null);
            String pass = pref.getString("password", null);
            String filename= (new File(mImageFileLocation)).getName();

            //if(!filename.contains(".jpg")) filename= "IMAGE_"+filename+".jpg";

            cr = new ClientResource(baseURI+fmName+"/"+filename);
            String response=null;
            ChallengeScheme scheme = ChallengeScheme.HTTP_BASIC;
            ChallengeResponse authentication = new ChallengeResponse(scheme,
                    user,pass);
            cr.setChallengeResponse(authentication);

            FileRepresentation payload = new FileRepresentation(new File(mImageFileLocation),
                    MediaType.IMAGE_JPEG);

            Log.e("FILE_GALLERY",payload.toString());



            try {
                response = cr.put(payload).getText();
            } catch (ResourceException|IOException e) {
                String text = "Error: " + cr.getStatus().getCode() + " - " + cr.getStatus().getDescription()+ " - " + cr.getStatus().getReasonPhrase();
                Log.e("GERARDO_AUDIO",text);
            }
            return response;
        }

        @Override
        protected void onPostExecute(String res) {
            if (res!=null)
                Toast.makeText(getApplicationContext(), "Upload " + res, Toast.LENGTH_SHORT).show();
        }
    }

}