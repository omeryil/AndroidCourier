package com.mubo.courier;

import android.app.Activity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class postBuilder {
    private String url;
    private String jsonPostData;
    private Map<String, String> parameters;
    private post.REQUEST_METHODS method;
    private post.POST_TYPE post_type;
    private String token;
    private post.RETURN_TYPE return_type;
    private Class mapperClass;
    private post.completionHandler completionHandler;
    private int requestCode;
    private File postFile;
    private Activity act;

    public postBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public postBuilder setJsonPostData(String jsonPostData) {
        this.jsonPostData = jsonPostData;
        return this;
    }

    public postBuilder setParameters(String key, String value) {
        if(parameters==null)
            parameters = new HashMap<>();
        parameters.put(key,value);
        return this;
    }

    public postBuilder setMethod(post.REQUEST_METHODS method) {
        this.method = method;
        return this;
    }

    public postBuilder setToken(String token) {
        this.token = token;
        return this;
    }

    public postBuilder setReturn_type(post.RETURN_TYPE return_type) {
        this.return_type = return_type;
        return this;
    }
    public postBuilder setPost_type(post.POST_TYPE post_type) {
        this.post_type = post_type;
        return this;
    }

    public postBuilder setMapperClass(Class mapperClass) {
        this.mapperClass = mapperClass;
        return this;
    }

    public postBuilder setCompletionHandler(post.completionHandler completionHandler) {
        this.completionHandler = completionHandler;
        return this;
    }

    public postBuilder setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }

    public postBuilder setPostFile(File postFie) {
        this.postFile = postFie;
        return this;
    }

    public postBuilder setActivity(Activity act) {
        this.act = act;
        return this;
    }
    public post createPost() {
        return new post(url, jsonPostData, parameters, method, post_type,
                token, return_type, mapperClass, completionHandler, requestCode,postFile,act);
    }
}