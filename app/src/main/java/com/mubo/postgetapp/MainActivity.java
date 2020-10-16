package com.mubo.postgetapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.mubo.courier.post;
import com.mubo.courier.postBuilder;
import com.mubo.courier.response;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new postBuilder()
                .setActivity(this)
                .setMethod(post.REQUEST_METHODS.POST)
                .setPost_type(post.POST_TYPE.MULTIPART)
                .setReturn_type(post.RETURN_TYPE.STRING)
                .setParameters("label","merhaba")
                .setUrl("http://192.168.0.14:8189/api/test/post")
                .setCompletionHandler(new post.completionHandler() {
                    @Override
                    public void onHttpFinished(response response) {
                        Toast.makeText(MainActivity.this, response.getData(), Toast.LENGTH_SHORT).show();
                    }
                })
                .createPost()
                .Process();

        JSONObject j=new JSONObject();
        try {
            j.put("a","String A");
            j.put("b",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new postBuilder()
                .setActivity(this)
                .setMethod(post.REQUEST_METHODS.POST)
                .setPost_type(post.POST_TYPE.JSON)
                .setReturn_type(post.RETURN_TYPE.MAPPER)
                .setJsonPostData(j.toString())
                .setMapperClass(TestClass.class)
                .setUrl("http://192.168.0.14:8189/api/post/json")
                .setCompletionHandler(new post.completionHandler() {
                    @Override
                    public void onHttpFinished(response response) {
                        TestClass tc= (TestClass) response.getMapperData();
                        Toast.makeText(MainActivity.this, tc.getA(), Toast.LENGTH_SHORT).show();
                    }
                })
                .createPost()
                .Process();


      new postBuilder()
                .setActivity(this)
                .setMethod(post.REQUEST_METHODS.GET)
                .setPost_type(post.POST_TYPE.JSON)
                .setUrl("http://192.168.0.14:8189/api/")
                .setCompletionHandler(new post.completionHandler() {
                    @Override
                    public void onHttpFinished(response response) {
                        Toast.makeText(MainActivity.this, response.getData(), Toast.LENGTH_SHORT).show();
                    }
                })
                .createPost()
                .Process();
    }
}