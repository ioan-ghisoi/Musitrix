package ioan.ghisoi.disertation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

/**
 * Created by jarvis on 18/04/2017.
 */

public class ComunityContributionContributor extends AppCompatActivity {

    private static final int FIND_MUSIC = 345;
    private Button mUpload;
    private Button mDownload;
    private Button mBrowse;
    private Uri mFilePath;
    private StorageReference mStorageReference;
    MediaPlayer mediaPlayer;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribution_contribute);

        database = FirebaseDatabase.getInstance();

        mUpload = (Button) findViewById(R.id.mUpload);
        mDownload = (Button) findViewById(R.id.mDownload);
        mBrowse = (Button) findViewById(R.id.mBrowse);
        mStorageReference = FirebaseStorage.getInstance().getReference();

        mBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showFileChooser();
                showMp3Chooser();
            }
        });

        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //uploadFile();
                uploadMp3();
            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        //magic
                        Uri audioFileUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/fir-78d72.appspot.com/o/songs?alt=media&token=832ca7cb-277c-481e-bf58-d7120c44cac2");
                        mediaPlayer = getMediaPlayerInstance();
                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(ComunityContributionContributor.this, audioFileUri);
                            mediaPlayer.prepare();
                            mediaPlayer.getDuration();
                            mediaPlayer.start();
                            mediaPlayer.setLooping(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FIND_MUSIC && resultCode == RESULT_OK){
            if ((data != null) && (data.getData() != null)){
                mFilePath= data.getData();
                System.out.println("ASTA E FILEPATHUL BOSSSSSSS" + mFilePath);
                // Now you can use that Uri to get the file path, or upload it, ...
            }
        }
    }

    private void showMp3Chooser() {
        Intent intent = new Intent();
        intent.setType("audio/mpeg");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select on mp3"),FIND_MUSIC);
    }

    private void uploadMp3() {

        try{
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            System.out.println("THIS IS THE FILEPATHHHHHH" + mFilePath);

            StorageReference riversRef = mStorageReference.child("songs");



            riversRef.putFile(mFilePath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("NU MERGE BOSS !!!!!!!!!!!!!!!!!!!!!!");
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("MERGE BOSS !!!!!!!!!!!!!!!!!!!!!!");
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") double progress = (100 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int)progress + "% Uploaded...");
                        }
                    });
        }catch (Exception e) {
            System.out.println(e);
        }

    }

    public MediaPlayer getMediaPlayerInstance() {
        MediaPlayer mMediaPlayer;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        return mMediaPlayer;
    }

}
