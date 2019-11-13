import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class SiteParser {
    private static final Logger LOGGER = Logger.getLogger(SiteParser.class.getName());
    private List<String> realAddresses = new ArrayList<>();
    private List<Element> realTables = new ArrayList<>();

    public SiteParser(String htmlName) throws IOException, Exception  {
        parseSite(htmlName);
        extractData();
    }

    public void parseSite(String htmlName) throws IOException {
        String html = FileManager.readFile(htmlName);
        org.jsoup.nodes.Document doc = Jsoup.parse(html, "UTF-8");

        Elements links = doc.getElementsByTag("a");
        for(Element a : links) {
            //System.out.println(a.html());
            if (a.html().equals("Hosts Executive Summary")) {
                Elements realLinks = doc.getElementsByTag("a");
                int realCnt=0;
                for (Element realA : realLinks) {
                    if (!realA.outerHtml().contains("#idp")) continue;
                    if (realA.outerHtml().contains("Hosts Executive Summary")) continue;
                    System.out.print(realA.html() + ";");
                    realCnt++;
                    realAddresses.add(realA.html());
                }
                System.out.println();
                LOGGER.info("Loaded: " + (realCnt) + " addresses");
                break;
            }
        }

        Elements tables = doc.getElementsByTag("table");
        int tableTagsCount = tables.size();

        int realCnt=0;
        for(Element table : tables) {
            if(!table.outerHtml().contains("Plugin")) continue;
            realCnt++;
            realTables.add(table);
        }

        LOGGER.info("Loaded: " + (realCnt) + " tables");
    }

    public void extractData() throws Exception {
        int countA = realAddresses.size();
        int countT = realTables.size();
        if (countA != countT) throw new Exception("Size mismatch!");

        for (int i=0; i<countA; i++) {
            String ipAddress = realAddresses.get(i);
            List<ExploitHolder> exploitHolders = new ArrayList<>();
            Element table = realTables.get(i);
            Element tbody = table.getElementsByTag("tbody").get(0);
            Elements trTags = table.getElementsByTag("tr");
            trTags.remove(0);
            for (Element tr : trTags) {
                Elements tdTags = tr.getElementsByTag("td");
                if (tdTags.get(1).html().equals("Severity")) continue;
                String tdType = tdTags.get(1).getElementsByTag("span").get(0).html();
                String tdDescr = tdTags.get(6).html();
                tdDescr = Parser.unescapeEntities(tdDescr, true);
                if (tdType.equals("Info")) continue; //TODO: can be optional
                exploitHolders.add(new ExploitHolder(tdType, tdDescr));
                LOGGER.info("New holder: " + tdType + " -> " + tdDescr);
            }
            AppMemory.getInstance().getIpInfoList().add(new IpInfoHolder(ipAddress, exploitHolders));
        }
    }
}
