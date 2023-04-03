package ver1.SpringDemoBot.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Maersk {

    public static String getContInfo(String messageText) {
        URL url = null;
        String response = null;
        try {
            url = new URL("https://api.maersk.com/track/" + messageText);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        Scanner in = null;
        try {
            in = new Scanner((InputStream) url.getContent());
        } catch (IOException e) {
            return response;
        }

        String result = "";
        while (in.hasNext()) {
            result += in.nextLine();
        }

        JSONObject object = new JSONObject(result);
        final JSONObject origin = object.getJSONObject("origin"); // заходимо в обєкт
        String cityFrom = (String) origin.get("city"); // дістаємо з обєкту строку значення city
        String countryFrom = (String) origin.get("country");
        final JSONObject destination = object.getJSONObject("destination");
        String cityTo = (String) destination.get("city");
        String countryTo = (String) destination.get("country");

        final JSONArray container = object.getJSONArray("containers");
        final int n = container.length();
        String contNum = null;
        String contSize = null;
        String etd = null;
        String eta = null;
        String etaOutputDate = null;
        for (int i = 0; i < n; ++i) {
            final JSONObject cont_num = container.getJSONObject(i);
            contNum = container.getJSONObject(i).getString("container_num");
            contSize = container.getJSONObject(i).getString("container_size");
            eta = container.getJSONObject(i).getString("eta_final_delivery");

            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            LocalDateTime dateTime = LocalDateTime.parse(eta, inputFormatter);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            etaOutputDate = dateTime.format(outputFormatter);
        }


        return "Container №: " + contNum + "\n" +
                "Container size: " + contSize + "\n\n" +
                "FROM: " + cityFrom + "\n"
                + countryFrom + "\n\n" +
                "TO: " + cityTo + "\n" +
                countryTo + "\n"
                + "\n" + "ETA: " + etaOutputDate

                ;
//        return result;
    }

}
