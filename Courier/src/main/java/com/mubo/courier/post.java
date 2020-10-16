package com.mubo.courier;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class post {
    public interface completionHandler{
        void onHttpFinished(response response);
    }
    public enum REQUEST_METHODS{
        POST,
        GET
    }

    public enum RETURN_TYPE{
        STRING,
        MAPPER
    }
    public enum POST_TYPE{
        MULTIPART,
        JSON
    }
    private String Url;
    private String JsonPostData;
    private Map<String,String> Parameters;
    private REQUEST_METHODS Method;
    private POST_TYPE Post_type;
    private String Token;
    private RETURN_TYPE Return_type;
    private Class MapperClass;
    private completionHandler CompletionHandler;
    private int RequestCode;
    private File PostFile;
    private Activity Act;

    public post(String url, String jsonPostData, Map<String, String> parameters,
                REQUEST_METHODS method, POST_TYPE post_type, String token,
                RETURN_TYPE return_type, Class mapperClass, completionHandler completionHandler,
                int requestCode, File postFile, Activity act) {
        Url = url;
        JsonPostData = jsonPostData;
        Parameters = parameters;
        Method = method;
        Post_type = post_type;
        Token = token;
        Return_type = return_type;
        MapperClass = mapperClass;
        CompletionHandler = completionHandler;
        RequestCode = requestCode;
        PostFile=postFile;
        Act = act;
    }

    public void Process() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(Post_type == POST_TYPE.JSON)
                    JsonPost();
                else if(Post_type == POST_TYPE.MULTIPART){
                    MultiPost();
                }else{

                }
            }
        }).start();

    }
    private void MultiPost() {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        String data = null;
        Object cData = null;
        String ExceptionData = null;
        response.HttpSuccess result;
        final response res;
        int serverResponseCode = -1;

        if (PostFile != null && !PostFile.isFile()) {
            result = response.HttpSuccess.FAIL;
            ExceptionData = "File not exists";
        } else {

            try {
                MultiPartUtility multipart = new MultiPartUtility(Url, "UTF-8");

                multipart.addHeaderField("User-Agent", "CodeJava");
                multipart.addHeaderField("Test-Header", "Header-Value");
                if (Token!=null && !Token.isEmpty())
                    multipart.setToken(Token);
                Iterator<Map.Entry<String, String>> itr = Parameters.entrySet().iterator();

                while (itr.hasNext()) {
                    Map.Entry<String, String> d = itr.next();
                    multipart.addFormField(d.getKey(), d.getValue());
                }
                if (PostFile != null) {

                    multipart.addFilePart("file", PostFile);

                }
                data = multipart.finish();
                if (Return_type == RETURN_TYPE.MAPPER) {
                    ObjectMapper mapper = new ObjectMapper();
                    cData = mapper.readValue(data, MapperClass);
                }
                serverResponseCode=multipart.getStatus();
                result = response.HttpSuccess.SUCCESS;
            } catch (IOException e) {
                result = response.HttpSuccess.FAIL;
                e.printStackTrace();
                ExceptionData = e.getMessage();
            }
        }
        res = new responseBuilder().setResponseCode(serverResponseCode)
                .setJsonData(data)
                .setMapperData(cData)
                .setExceptionData(ExceptionData)
                .setRequestCode(RequestCode)
                .setResult(result)
                .createResponse();
        Act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CompletionHandler.onHttpFinished(res);
            }
        });


    }
    private void JsonPost(){
        int responsCode=-1;
        String data=null;
        Object cData=null;
        String ExceptionData=null;
        response.HttpSuccess result;
        String m = Method == REQUEST_METHODS.POST ? "POST":"GET";
        final response res;
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                doTrustToCertificates();
            }
            URL connectURL = new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();
            conn.setRequestMethod(m);
            if (Token!=null && !Token.isEmpty())
                conn.setRequestProperty("Authorization", Token);
            if(Method == REQUEST_METHODS.POST) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Content-Type", "application/json");
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream());
                os.write(JsonPostData);
                os.flush();
            }
            responsCode = conn.getResponseCode();
            InputStream is = conn.getInputStream();
            data = convertStreamToString(is);
            if (Return_type == RETURN_TYPE.MAPPER) {
                ObjectMapper mapper = new ObjectMapper();
                cData = mapper.readValue(data, MapperClass);
            }
            result = response.HttpSuccess.SUCCESS;
        } catch (Exception e) {
            result = response.HttpSuccess.FAIL;
            ExceptionData=e.toString();
        }
        res = new responseBuilder().setResponseCode(responsCode)
                .setJsonData(data)
                .setMapperData(cData)
                .setExceptionData(ExceptionData)
                .setRequestCode(RequestCode)
                .setResult(result)
                .createResponse();
        Act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CompletionHandler.onHttpFinished(res);
            }
        });
    }
    public static void doTrustToCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        return;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
                        return;
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                if (!urlHostName.equalsIgnoreCase(session.getPeerHost())) {
                    System.out.println("Warning: URL host '" + urlHostName + "' is different to SSLSession host '" + session.getPeerHost() + "'.");
                }
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}

