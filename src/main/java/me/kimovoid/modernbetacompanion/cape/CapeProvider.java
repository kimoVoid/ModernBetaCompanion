package me.kimovoid.modernbetacompanion.cape;

import com.google.gson.Gson;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.executor.ExecutorType;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class CapeProvider {

    public Future<String> getCape(String username) {
        try {
            HttpClient httpClient = new HttpClient(ExecutorType.AUTO);
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
        return new Gson().fromJson(httpClient.get(URL).execute().getContentAsString(), classOfT);
    }

    private static class UUIDResponse {
        private String id;
    }

    private static class FUIDResponse {
        private String floodgateuid;
    }
}