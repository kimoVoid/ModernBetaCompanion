package me.kimovoid.modernbetacompanion.util;

import com.google.gson.Gson;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class CapeProvider {

    public Future<String> getCape(String username) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String uuid;

            /* Bedrock support */
            if (username.startsWith(".")) {
                FUIDResponse fuidResponse = this.getRequest(httpClient, "https://mcprofile.io/api/v1/bedrock/gamertag/" + username.substring(1), FUIDResponse.class);
                uuid = fuidResponse.floodgateuid;
            } else {
                UUIDResponse uuidResponse = this.getRequest(httpClient, "https://api.mojang.com/users/profiles/minecraft/" + username, UUIDResponse.class);
                uuid = uuidResponse.id.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
            }

            String s = "https://cape.modernbeta.org/cape/" + uuid;

            /* Check if the URL returns an image */
            URL url = new URL(s);
            ImageIO.read(url);

            return CompletableFuture.completedFuture(s);
        } catch (Exception e) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    private <T> T getRequest(HttpClient httpClient, String URL, Class<T> classOfT) throws IOException {
        String response = EntityUtils.toString(httpClient.execute(new HttpGet(URL)).getEntity());
        return new Gson().fromJson(response, classOfT);
    }

    private static class UUIDResponse {
        private String id;
    }

    private static class FUIDResponse {
        private String floodgateuid;
    }
}