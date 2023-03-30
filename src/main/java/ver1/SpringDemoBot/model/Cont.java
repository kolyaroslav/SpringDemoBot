package ver1.SpringDemoBot.model;

import netscape.javascript.JSObject;
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

        shippingInfo.setOrigin(object.getString("city"));
        shippingInfo.setOrigin(object.getString("country"));
        shippingInfo.setDestination(object.getString("city"));
        shippingInfo.setDestination(object.getString("country"));

        return "FROM: " + shippingInfo.getOrigin();
//        return result;
    }

}
