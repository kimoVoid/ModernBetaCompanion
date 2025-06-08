package me.kimovoid.modernbetacompanion.cape;

import com.google.gson.Gson;
import net.lenni0451.commons.httpclient.HttpClient;
import net.lenni0451.commons.httpclient.executor.ExecutorType;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@SuppressWarnings("unchecked")
public class CapeProvider {

    public Future<String> getCape(String username) {
        try {
            HttpClient httpClient = new HttpClient(ExecutorType.AUTO);
            UUIDResponse uuidresponse = this.getRequest(httpClient, "https://api.mojang.com/users/profiles/minecraft/" + username);

            /* The API requires UUIDs with dashes */
            String uuid = uuidresponse.id.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
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

    private <T> T getRequest(HttpClient httpClient, String URL) throws IOException {
        return new Gson().fromJson(httpClient.get(URL).execute().getContentAsString(), (Class<T>) UUIDResponse.class);
    }

    private static class UUIDResponse {
        private String id;
        private String name;
    }
}