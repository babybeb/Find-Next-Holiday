import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDate;

public class Holiday {
    LocalDate today = LocalDate.now();
    final String serviceKey = "LkLE%2FS83ZQIzTfq1ci5gOtjHY%2FIN0gdjuaza8tWuSLW%2B8RAi9HDJOw34JxXBUbb93pfJJti8CAPtc%2FsLrBjrRA%3D%3D";

    public DayInfo findNextHoliday() throws IOException {

        while (true) {
            /*
            요청 url 예시
            http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?solYear=2022&solMonth=10&ServiceKey=LkLE%2FS83ZQIzTfq1ci5gOtjHY%2FIN0gdjuaza8tWuSLW%2B8RAi9HDJOw34JxXBUbb93pfJJti8CAPtc%2FsLrBjrRA%3D%3D&_type=json
             */
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getAnniversaryInfo");
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + URLEncoder.encode(serviceKey, "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
            urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + today.getYear()); /*연*/
            if (today.getMonth().getValue() < 10) {
                urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode("0" + today.getMonth(), "UTF-8"));
            } else {
                urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + today.getMonth());
            }
            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            System.out.println(sb.toString());

            today = today.plusMonths(1);
        }
    }
}