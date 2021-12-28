package com.example.myuploadapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;



public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener  {
    Button btnbrowse, btnupload,btnsave;
    EditText txtdata ,description;
    ImageView imgview;
    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    int Image_Request_Code = 7;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //dropdown spinner

        Spinner spinner = findViewById(R.id.spinner1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.numbers, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //uploading
        storageReference = FirebaseStorage.getInstance().getReference("Images");
        databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        btnbrowse = (Button)findViewById(R.id.btnbrowse);
        btnupload= (Button)findViewById(R.id.btnupload);
        txtdata = (EditText)findViewById(R.id.txtdata);
        description = (EditText)findViewById(R.id.description);
        imgview = (ImageView)findViewById(R.id.image_view);
        progressDialog = new ProgressDialog(MainActivity.this);// context name as per your project name
        //btnsave
        btnsave = (Button)findViewById(R.id.btnsave);

        btnbrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), Image_Request_Code);

            }
        });
        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                UploadImage();

            }
        });
//code related when save btn is clicked

        btnsave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

                FilePathUri = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);
                    imgview.setImageBitmap(bitmap);
                }
                catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }


        public String GetFileExtension(Uri uri) {

            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

        }


        public void UploadImage() {

            if (FilePathUri != null) {

                progressDialog.setTitle("Image is Uploading...");
                progressDialog.show();
                StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
                storageReference2.putFile(FilePathUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String TempImageName = txtdata.getText().toString().trim();
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                                @SuppressWarnings("VisibleForTests")
                                uploadinfo imageUploadInfo = new uploadinfo(TempImageName, taskSnapshot.getUploadSessionUri().toString());
                                String ImageUploadId = databaseReference.push().getKey();
                                databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            }
                        });
            }
            else {

                Toast.makeText(MainActivity.this, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

            }
        }



    }
//dropdwn method
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        String text = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}