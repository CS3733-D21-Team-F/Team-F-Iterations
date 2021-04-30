package edu.wpi.cs3733.D21.teamF.pathfinding;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class GoogleAPI {
    public GoogleAPI() {
    }

    private String buildUrl(String origin, String destination){
        StringBuilder url = new StringBuilder();
        String apiKey = ""; //TODO: ADD API KEY HERE

        return url.toString();
    }

    public void findDistance(String origin, String destination) throws IOException{
        String requestUrl = buildUrl(origin, destination);
        URL getRequestUrl = new URL(requestUrl);
        StringBuilder apiResponse = new StringBuilder();
        HttpURLConnection connection = (HttpURLConnection) getRequestUrl.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        BufferedReader data = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String temp;
        while ((temp = data.readLine()) != null){
            apiResponse.append(temp);
            System.out.println(temp);
        }
        data.close();
    }

    private static class googleSingletonHelper{
        private static final GoogleAPI googleAPI = new GoogleAPI();
    }

    public static GoogleAPI getGoogleAPI(){
        return googleSingletonHelper.googleAPI;
    }
}
