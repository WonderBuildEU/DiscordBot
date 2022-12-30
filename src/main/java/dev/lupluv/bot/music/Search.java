package dev.lupluv.bot.music;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Search {

    String searchQuery;

    public Search(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public static void main(String[] args){
        Search search = new Search("belle u");
        try {
            search.executeSearch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static final String REQUEST_URL = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q=%keyword%&key=AIzaSyB2XqlgXN-1R5T01bHiEGCQgWxm-WTX2wg";


    public String executeSearch() throws IOException {
        try {
            URL url = new URL(REQUEST_URL.replace("%keyword%", searchQuery.replace(" ", "+")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // If you're not sure if the request will be successful,
            // you need to check the response code and use #getErrorStream if it returned an error code
            InputStream inputStream = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);

            // This could be either a JsonArray or JsonObject
            JsonElement element = new JsonParser().parse(reader);
            if (element.isJsonObject()) {
                // Is JsonObject
                JsonElement jsonElement = element.getAsJsonObject().getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("id").get("videoId");
                System.out.println("Handeling search, the result is:");
                System.out.println("https://www.youtube.com/watch?v=" + jsonElement.getAsString());
                return "https://www.youtube.com/watch?v=" + jsonElement.getAsString();
                /*
                String version = jsonObject.get("name").getAsString();
                System.out.println("Newest Version: " + version);
                */
            }
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return null;
    }
}
