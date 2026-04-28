package me.kimovoid.modernbetacompanion.util;

import com.google.gson.Gson;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.spongepowered.asm.mixin.Unique;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class TabProvider {

    public final static TabProvider INSTANCE = new TabProvider();

    public Future<String[]> getPlayers() {
        try {
            Header header = new BasicHeader("X-API-Key", "6md-3-GUfASL4lAzaSyCDSj4FqCxFOxOq4YLtu6z8uI");
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setDefaultHeaders(Collections.singleton(header))
                    .build();

            OnlinePlayersResponse response = this.getRequest(httpClient, "https://api.modernbeta.org/api/v1/server/online/all", OnlinePlayersResponse.class);
            return CompletableFuture.completedFuture(response.names);
        } catch (Exception e) {
            CompletableFuture<String[]> future = new CompletableFuture<>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Unique
    private <T> T getRequest(HttpClient httpClient, String URL, Class<T> classOfT) throws IOException {
        String response = EntityUtils.toString(httpClient.execute(new HttpGet(URL)).getEntity());
        return new Gson().fromJson(response, classOfT);
    }

    private static class OnlinePlayersResponse {
        private int count;
        private String[] names;
    }
}