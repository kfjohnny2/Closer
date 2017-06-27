package douche.com.closer.service;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import douche.com.closer.model.Device;
import douche.com.closer.model.Person;

/**
 * Created by Johnny on 16/06/2017.
 */

public class WebServiceImpl {

    public static String get(String urlBase){
        HttpURLConnection urlConnection = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(urlBase);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

        }catch( Exception e) {
            e.printStackTrace();
        }
        finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }

    public static String sendPost(String urlBase, String query){
        // Send data
        InputStream inputStream;
        HttpURLConnection urlConnection;
        byte[] outputBytes;
        String responseData = null;
        try {
            URL url = new URL(urlBase);

        /* forming th java.net.URL object */
            urlConnection = (HttpURLConnection) url.openConnection();

            /* pass post data */
            outputBytes = query.getBytes("UTF-8");

            urlConnection.setRequestMethod("POST");
            urlConnection.connect();
            OutputStream os = urlConnection.getOutputStream();
            os.write(outputBytes);
            os.close();

        /* Get Response and execute WebService request*/
            int statusCode = urlConnection.getResponseCode();

        /* 200 represents HTTP OK */
            if (statusCode == HttpsURLConnection.HTTP_OK) {

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                responseData =  convertStreamToString(inputStream);
            } else {
                responseData = null;
            }


        } catch (Exception e) {

            e.printStackTrace();

        }

        return responseData;
    }
    public static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
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

//    public static String sendPost(URL url){
//        String result = "";
//
//        try {
//            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.setRequestMethod("POST");
//
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
//            String inputLine;
//            StringBuilder response = new StringBuilder();
//
//            while ((inputLine = bufferedReader.readLine()) != null) {
//                response.append(inputLine);
//            }
//
//            result = response.toString();
//            bufferedReader.close();
//        } catch (Exception e) {
//            Log.d("InputStream", e.getMessage());
//        }
//
//        return result;
//    }

    private static String readResponse(HttpURLConnection request) throws IOException {
        ByteArrayOutputStream os;
        try (InputStream is = request.getInputStream()) {
            os = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) != -1) {
                os.write(b);
            }
        }
        return new String(os.toByteArray());
    }

    public static class GenericException extends Exception {
        private static final long serialVersionUID = 1L;

        public GenericException(Throwable cause) {
            super(cause);
        }
    }
}
