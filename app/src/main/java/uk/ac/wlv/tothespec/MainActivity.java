package uk.ac.wlv.tothespec;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private EditText mPostEditText;
    private EditText mPostSearchText;
    private ImageView mAddPostImageView;
    private Button mSendButton;
    private String imageURI;
    private RecyclerView mRecyclerView;
    PostAdapter postAdapter;

    private void updateUI() {
        Post post = Post.get(MainActivity.this);
        List<Spec> specs = post.getSpecs();
        if (postAdapter == null) {
            postAdapter = new PostAdapter(specs);
            mRecyclerView.setAdapter(postAdapter);
        } else {
            postAdapter.setSpecs(specs);
            postAdapter.notifyDataSetChanged();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final List<Spec> mPosts = new Post(MainActivity.this).getSpecs();
        mRecyclerView = (RecyclerView) findViewById(R.id.postRecyclerView);
        postAdapter = new PostAdapter(mPosts);
        mRecyclerView.setAdapter(postAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        mPostSearchText = (EditText) findViewById(R.id.postSearchText);
        mPostSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int j, int k) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int j, int k) {
                List<Spec> newSpec = new ArrayList<>();
                if (charSequence.toString().trim().length() > 0) {
                    for(Spec item: mPosts) {
                        if (item.getMessage().equals(charSequence.toString())) {
                            newSpec.add(item);
                            postAdapter = new PostAdapter(newSpec);
                            mRecyclerView.setAdapter(postAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                List<Spec> newSpec = new ArrayList<>();
                if (editable.toString().trim().length() > 0) {
                    for (Spec item : mPosts) {

                        if (item.getMessage().equals(editable.toString())) {
                            newSpec.add(item);
                            postAdapter = new PostAdapter(newSpec);
                            mRecyclerView.setAdapter(postAdapter);
                            mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                            postAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });

        mPostEditText = (EditText) findViewById(R.id.addPostEditText);
        mPostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int j, int k) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int j, int k) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Spec spec = new Spec();
                Post post = new Post(MainActivity.this);
                spec.setMessage(mPostEditText.getText().toString());
                spec.setUrl(imageURI);
                post.addSpec(spec);
                updateUI();
            }
        });
        mAddPostImageView = (ImageView) findViewById(R.id.addPostImageView);
        mAddPostImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    imageURI = getPath(MainActivity.this, uri);
                    Bitmap myBitmap = BitmapFactory.decodeFile(imageURI);
                    File direct = new File(Environment.getExternalStorageDirectory().toString() + "/toTheSpecMedia");
                    direct.mkdirs();

                    Random generator = new Random(); // This code was implemented from StackOverFlow
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fName = "Image-"+ n +".jpg";
                    File file = new File("/sdcard/toTheSpecMedia/", fName);
                    imageURI = file.getAbsolutePath();
                    try {
                        FileOutputStream out = new FileOutputStream(file);
                        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, "Image selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public static String getPath(Context context, Uri uri ) { //This code was from StackOverFlow to get image path from gallery selected item https://stackoverflow.com/questions/20324155/get-filepath-and-filename-of-selected-gallery-image-in-android/40844108
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }
}