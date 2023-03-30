package ver1.SpringDemoBot.model;

import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class Cont {

    public static String getContInfo(String messageText, ShippingInfo shippingInfo) throws IOException {
        URL url = new URL("https://api.maersk.com/track/" + messageText);
        Scanner in = new Scanner((InputStream) url.getContent());
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
        String eta = null;
        for (int i = 0; i < n; ++i) {
            final JSONObject cont_num = container.getJSONObject(i);
            contNum = container.getJSONObject(i).getString("container_num");
            contSize = container.getJSONObject(i).getString("container_size");
            eta = container.getJSONObject(i).getString("eta_final_delivery");
        }


        return "Container №: " + contNum + "\n" +
                "Container size: " + contSize + "\n" +
                "FROM: \n" + cityFrom + "\n"
                + countryFrom + "\n\n" +
                "TO: " + cityTo + "\n" +
                countryTo + "\n"
                + "\n" + "ETA: " + eta

                ;
//        return result;
    }

}
