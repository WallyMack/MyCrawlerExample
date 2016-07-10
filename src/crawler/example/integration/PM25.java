package crawler.example.integration;

import com.github.abola.crawler.CrawlerPack;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;
import com.mashape.unirest.http.Unirest;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import static crawler.example.integration.PM25ElasticImport.sendPost;

/**
 * 整合練習：PM2.5 資料取得
 *
 * @author Abola Lee
 */
public class PM25 {
    static String elasticHost = "192.168.38.30" ;
    static String elasticPort = "9200" ;
    static String elasticIndex = "pm25";
    static String elasticIndexType = "data";

    public static void main(String[] args) {

        // 遠端資料路徑
        String uri = "http://opendata2.epa.gov.tw/AQX.xml";
        Document jsoupDoc = CrawlerPack.start().getFromXml(uri);
        for (Element Ele : jsoupDoc.select("Data")) {
            Double co = Doubles.tryParse(Ele.getElementsByTag("CO").text());
            String county = Ele.getElementsByTag("County").text();
            Long fpmi = Longs.tryParse(Ele.getElementsByTag("FPMI").text());
            String majorpollutant
                    = Ele.getElementsByTag("MajorPollutant").text();
            Double no = Doubles.tryParse(Ele.getElementsByTag("NO").text());
            Long no2 = Longs.tryParse(Ele.getElementsByTag("NO2").text());
            Double nox = Doubles.tryParse(Ele.getElementsByTag("NOx").text());
            Long o3 = Longs.tryParse(Ele.getElementsByTag("O3").text());
            Long pm10 = Longs.tryParse(Ele.getElementsByTag("PM10").text());
            Long pm25 = Longs.tryParse(Ele.getElementsByTag("PM2.5").text());
            Long psi = Longs.tryParse(Ele.getElementsByTag("PSI").text());
            String publishtime
                    = Ele.getElementsByTag("PublishTime").text()
                    .replace(' ', 'T') + ":00+0800";
            String sitename = Ele.getElementsByTag("SiteName").text();
            Long so2 = Longs.tryParse(Ele.getElementsByTag("Status").text());
            String status = Ele.getElementsByTag("Status").text();
            String windspeed
                    = Ele.getElementsByTag("WindSpeed").text();
            Double winddirec
                    = Doubles.tryParse(Ele.getElementsByTag("WindDirec").text());
            String elasticJson = "{" +
                    "\"co\":" + co +
                    ",\"county\":\"" + county + "\"" +
                    ",\"fpmi\":" + fpmi +
                    ",\"majorpollutant\":\"" + majorpollutant + "\"" +
                    ",\"no\":" + no +
                    ",\"no2\":" + no2 +
                    ",\"nox\":" + nox +
                    ",\"o3\":" + o3 +
                    ",\"pm10\":" + pm10 +
                    ",\"pm25\":" + pm25 +
                    ",\"psi\":" + psi +
                    ",\"publishtime\":\"" + publishtime + "\"" +
                    ",\"sitename\":\"" + sitename + "\"" +
                    ",\"so2\":" + so2 +
                    ",\"status\":\"" + status + "\"" +
                    ",\"windspeed\":\"" + windspeed + "\"" +
                    ",\"winddirec\":" + winddirec +
                    "}";
            System.out.println(
                    // curl -XPOST http://localhost:9200/pm25/data -d '{...}'
                    sendPost("http://" + elasticHost + ":" + elasticPort
                                    + "/" + elasticIndex + "/" + elasticIndexType
                            , elasticJson));

        }
    }

    static public <T> T nvl(T arg0, T arg1) {
        return (arg0 == null)?arg1:arg0;
    }

    static String sendPost(String url, String body){
        try{
            return Unirest.post(url)
                    .header("content-type", "text/plain")
                    .header("cache-control", "no-cache")
                    .body(body)
                    .asString().getBody();

        }catch(Exception e){return "Error:" + e.getMessage();}
    }
}
