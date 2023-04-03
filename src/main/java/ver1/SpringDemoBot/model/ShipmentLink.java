package ver1.SpringDemoBot.model;

import io.micrometer.common.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShipmentLink {

    public static String getContInfoo(String messageText) throws Exception {
        String url = "https://ct.shipmentlink.com/servlet/TDB1_CargoTracking.do";
        String postData = "TYPE=CNTR&BL=&CNTR=" + messageText + "&bkno=&query_bkno=&query_rvs=&query_docno=&query_seq=&PRINT=&SEL=s_cntr&NO=" + messageText + "";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
        out.write(postData);
        out.flush();
        out.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String parsedData = parseLocationFromResponse(response.toString());
         return parsedData;
    }

    private static String parseLocationFromResponse(String response) {
        String textMessage = null;
        List<String> info = new ArrayList<>();
        Document doc = Jsoup.parse(response);

        String eta = "";
        String etd = "";

        Element element = doc.selectFirst("td:contains(Estimated Date of Arrival)");
        if (element != null) {
            String arrival = element.text();
            String arrivalFinal = arrival.replace("Estimated Date of Arrival : <br>", "");
            String[] splitResult = arrivalFinal.split(":");
            final String x = splitResult[1];
            eta = x.substring(1, 12);

            if (splitResult[2].contains(" All information")) {
                etd = "No information";
            } else {
                etd = splitResult[2].substring(1, 12);
            }
        }

        Element elementsByClass = doc.body().getElementsByClass("ec-table ec-table-sm").last();
        if (elementsByClass != null) {

            Element trInfo = elementsByClass.select("tr").last();
            Elements cellsInfo = trInfo.select("td");

            for (Element row : cellsInfo) {
                if (cellsInfo.size() > 1) {
                    List<Node> nodes = row.childNodes();
                    if (CollectionUtils.isEmpty(nodes)) {
                        info.add("");
                    } else {
                        String text = ((TextNode) nodes.get(0)).text();
                        info.add(text);
                    }
                }
            }

            textMessage = "Container # " + info.get(0) + " \nContainer size: " + info.get(1) + "\nETD: " + etd + " \nETA: " + eta;
        }

        return textMessage;
    }

}