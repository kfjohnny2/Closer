package douche.com.closer.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Johnny on 16/06/2017.
 */

public class WebServiceImpl {
    public static String sendPost(String url, String json) throws GenericException {

        try {
            HttpURLConnection request = (HttpURLConnection) new URL(url).openConnection();

            try {
                request.setDoOutput(true);
                request.setDoInput(true);
                request.setRequestProperty("Content-Type", "application/json");
                request.setRequestMethod("POST");
                request.connect();

                try (OutputStream outputStream = request.getOutputStream()) {
                    outputStream.write(json.getBytes("UTF-8"));
                }
                return readResponse(request);

            } finally {
                request.disconnect();
            }

        } catch (IOException ex) {
            throw new GenericException(ex);
        }
    }

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
