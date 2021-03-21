package uk.ac.wlv.tothespec;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Spec> mSpec;

    public void setSpecs(List<Spec> specs) {
        mSpec = specs;
    }

    public PostAdapter(List<Spec> specs) {
        mSpec = specs;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.post, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Spec spec = mSpec.get(position);
        holder.bindSpec(spec);

        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spec spec = mSpec.get(position);
                Toast.makeText(v.getContext(), "Post Deleted", Toast.LENGTH_LONG).show();
                Post.get(v.getContext()).deleteSpec(spec);
                PostAdapter.this.notifyItemRemoved(position);

            }
        });
        holder.mShareButton.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View v) {
               Spec spec = mSpec.get(position);
               Intent i = new Intent(Intent.ACTION_SEND);
               i.setType("text/plain");
               i.putExtra(Intent.EXTRA_SUBJECT, "ToTheSpec - Benchmark");
               i.putExtra(Intent.EXTRA_TEXT, spec.getMessage());
               i = Intent.createChooser(i, "Send Post Via");
               v.getContext().startActivity(i);

           }
        });
        holder.postEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int j, int k) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int j, int k) {
                Spec spec = mSpec.get(position);
                spec.setMessage(charSequence.toString());
                updateNewSpec(holder.getContext(), spec);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        holder.mBlogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                final String[] token = new String[1];
                //Query for Oauth token
                String authURL = "https://accounts.google.com/o/oauth2/auth/772592637043-627621t962fs1lcia7g05k2gasjgftf3.apps.googleusercontent.com";
                final StringRequest authRequest = new StringRequest(Request.Method.GET, authURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                token[0] = response;
                            }
                        },new Response.ErrorListener()
                            {
                                @Override
                                public void onErrorResponse (VolleyError error){
                            }
                        });
                //Query to blogger with Oauth token.
                String url = "https://www.googleapis.com/blogger/v3/blogs/6305961960461102987/posts?key=AIzaSyAKflzQJlwzYDZvvOHH9a0P5MPHu1xsDNA";
                JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(holder.itemView.getContext(), "Post Uploaded to Blogger", Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(holder.itemView.getContext(), "Post Failed", Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Authorization","Bearer " + requestQueue.add(authRequest));
                        params.put("Accept", "application/json");
                        params.put("Content-Type", "application/json");
                        return params;
                    }
                    @Override
                    protected Map<String, String> getParams () {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Title", "abc");
                        params.put("Content", "abc");
                        return params;
                    }
                };
                requestQueue.add(stringRequest);
            }
        });

    }
    public void updateNewSpec(Context ctx,Spec spec) {
        Post post = new Post(ctx);
        post.updateSpec(spec);
    }


    @Override
    public int getItemCount() {
        return mSpec.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        EditText postEditTextView;
        ImageView postImageView;
        Button mDeleteButton;
        Button mShareButton;
        Button mBlogButton;
        private Spec mSpec;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.postEditTextView = itemView.findViewById(R.id.postEditTextView);
            this.postImageView = itemView.findViewById(R.id.postImageView);
            this.mDeleteButton = itemView.findViewById(R.id.postDeleteButton);
            this.mShareButton = itemView.findViewById(R.id.postShareButton);
            this.mBlogButton = itemView.findViewById(R.id.postShareBlogButton);
        }

        public void bindSpec(Spec spec) {
            mSpec = spec;
            postEditTextView.setText(mSpec.getMessage());
            System.out.println("path:" + mSpec.getUrl());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 8;
            Bitmap myBitmap = BitmapFactory.decodeFile(mSpec.getUrl(), options);
            postImageView.setImageBitmap(myBitmap);
        }
        public Context getContext() {return itemView.getContext();}

    }
}
