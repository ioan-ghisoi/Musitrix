package ioan.ghisoi.disertation;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


/**
 * Created by jarvis on 18/04/2017.
 */

public class ComunityContributionContributor extends AppCompatActivity {

    private static final int FIND_MUSIC = 345;
    private static final int FIND_IMAGE = 123;;
    private Button mUpload;
    private Button mDownload;
    private Button mBrowse;
    private Button mPreview;
    private Uri mFilePath;
    private StorageReference mStorageReference;
    FirebaseDatabase database;
    Spinner dropdown;
    MediaPlayer mp;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ImageButton backk;
    EditText userDetails, mUrl;
    String mSelectedPieces;

    private Button buttonDrop;

    private String instantiateSong, instantiatePiece, instantiateName;

    @Override
    public void onBackPressed() {
        Toast.makeText(ComunityContributionContributor.this,"Thank you by the way!",Toast.LENGTH_LONG).show();
        stopBackgroundMusic();
        finish();
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contribution_contribute);

        database = FirebaseDatabase.getInstance();

        mUpload = (Button) findViewById(R.id.mUpload);
        mDownload = (Button) findViewById(R.id.mDownload);
        mBrowse = (Button) findViewById(R.id.mBrowse);
        mPreview = (Button) findViewById(R.id.mPreview);
        backk = (ImageButton) findViewById(R.id.back_button);
        userDetails = (EditText) findViewById(R.id.user_description);
        buttonDrop = (Button) findViewById(R.id.dropButton);
        mUrl = (EditText) findViewById(R.id.user_url);


        buttonDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ComunityContributionContributor.this, buttonDrop);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.poupup_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mSelectedPieces = "" + item.getTitle();
                        Toast.makeText(
                                ComunityContributionContributor.this,
                                "You selected " + item.getTitle() + " pieces",
                                Toast.LENGTH_SHORT
                        ).show();
                        buttonDrop.setTextColor(getResources().getColor(R.color.musitrix_green));
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        }); //closing the setOnClickListener method

        mStorageReference = FirebaseStorage.getInstance().getReference();


        backk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mp.stop();
                    mp = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent myIntent = new Intent(ComunityContributionContributor.this, ComunityChose.class);
                ComunityContributionContributor.this.startActivity(myIntent);
            }
        });



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

        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPlayerFromUrl(instantiateSong);
            }
        });



        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStorageReference.child("songs/"+"song_"+auth.getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        instantiateSong = ""+uri;
                        instantiatePiece = "" + mSelectedPieces;

                        instantiateName = auth.getCurrentUser().getDisplayName();

                        createEntity();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
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
                mBrowse.setText("Browse Done");
                mBrowse.setTextColor(getResources().getColor(R.color.musitrix_green));
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

            StorageReference riversRef = mStorageReference.child("songs/"+"song_"+auth.getCurrentUser().getUid());



            riversRef.putFile(mFilePath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Well Done!");
                            mUpload.setText("Upload Done");
                            mUpload.setTextColor(getResources().getColor(R.color.musitrix_green));
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

    private void createEntity() {
        DatabaseReference reference;
        reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/name");
        reference.setValue(instantiateName);
        reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/piece");
        reference.setValue(instantiatePiece);
        reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/song");
        reference.setValue(instantiateSong);
        try{
            reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/details");
            reference.setValue(userDetails.getText().toString());
            reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/url");
            reference.setValue(mUrl.getText().toString());
            reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/likes");
            reference.setValue("0");
            reference = database.getReference("Contributors/" + auth.getCurrentUser().getUid() + "/uid");
            reference.setValue(auth.getCurrentUser().getUid());
        }catch (Exception e) {

        }
        mDownload.setText("Submitted");
    }

    public void createPlayerFromUrl(String url) {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(this, Uri.parse(url));
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.prepare(); //don't use prepareAsync for mp3 playback

            mp.seekTo(0);
            mp.start();

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void stopBackgroundMusic() {
        try {
            mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
