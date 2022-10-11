import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Holiday {
    
    final LocalDate today = LocalDate.now();
    LocalDate calculateDay = LocalDate.now();
    
    public DayInfo findNextHoliday() throws IOException {
        int cnt = 0; // 최대 5개월의 공휴일 정보만 조회한다
        while (cnt < 5) {
            cnt++;
            URL url = makeUrl();
            DayInfo nextHoliday = parseData(url);
            if (nextHoliday == null) {
                calculateDay = calculateDay.plusMonths(1);
                calculateDay.withDayOfMonth(1);
                continue;
            }
            
            return nextHoliday;
        }
        return null;
    }
    
    private URL makeUrl()
        throws UnsupportedEncodingException, MalformedURLException {
        StringBuilder urlBuilder = new StringBuilder(
            "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo");
        urlBuilder.append(
            "?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + Secret.serviceKey);
        urlBuilder.append(
            "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append(
            "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));
        urlBuilder.append(
            "&" + URLEncoder.encode("solYear", "UTF-8") + "=" + calculateDay.getYear());
        urlBuilder.append("&_type=json");
        if (calculateDay.getMonth().getValue() < 10) {
            urlBuilder.append(
                "&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(
                    "0" + calculateDay.getMonth().getValue(), "UTF-8"));
        } else {
            urlBuilder.append(
                "&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + calculateDay.getMonth()
                    .getValue());
        }
        
        URL url = new URL(urlBuilder.toString());
        
        return url;
    }
    
    
    private DayInfo parseData(URL url) {
        
        String parsingData = "";
        
        try {
            
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            BufferedReader bf;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                bf = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null) {
                sb.append(line);
            }
            
            bf.close();
            conn.disconnect();
            
            parsingData = sb.toString();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(parsingData);
            JSONObject response = (JSONObject) jsonObject.get("response");
            JSONObject body = (JSONObject) response.get("body");
            
            if (body.get("totalCount").toString().equals("0")) { // 해당 월에 공휴일이 없는 경우
                return null;
            }
            JSONObject items = (JSONObject) body.get("items");
            
            JSONArray item = new JSONArray();
            if (items.get("item") instanceof JSONObject) { // item의 type이 JSONObject인 경우
                item.add(items.get("item"));
            } else { // item의 type이 JSONArray인 경우
                item = (JSONArray) items.get("item");
            }
            
            for (int i = 0; i < item.size(); i++) {
                JSONObject o = (JSONObject) item.get(i);
                
                if (o.get("isHoliday").equals("N")) {
                    continue;
                }
                
                String stringDate = String.valueOf(o.get("locdate"));
                LocalDate date = calculateDate(stringDate);
                if (date.isBefore(calculateDay) || date.isEqual(calculateDay)) {
                    continue;
                }
                
                DayInfo result = DayInfo.builder()
                    .dateName((String) o.get("dateName"))
                    .year(date.getYear())
                    .month(date.getMonth().getValue())
                    .day(date.getDayOfMonth())
                    .remainingDays((int) ChronoUnit.DAYS.between(today, date))
                    .build();
                
                return result;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    LocalDate calculateDate(String date) {
        LocalDate result;
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(4, 6));
        int day = Integer.parseInt(date.substring(6, 8));
        
        return LocalDate.of(year, month, day);
    }
}