import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

public class XmlParser {
    private static final Logger LOGGER = Logger.getLogger(XmlParser.class.getName());

    public XmlParser(String xmlName) throws Exception {
        String text = parseXml(xmlName);
        LOGGER.info("Got: " + text);
        parsePlainText(text);
    }

    public String parseXml(String xmlName) throws IOException {
        String html = FileManager.readFile(xmlName);
        org.jsoup.nodes.Document doc = Jsoup.parse(html.replaceAll("\n", "%newline%"), "UTF-8", Parser.xmlParser());
        Element test = doc.getElementsByTag("output").first();
        return test.text().replaceAll("%newline%", "\n");
    }

    private String getPortFromString(String text) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private String buildPortsString(List<String> ports) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String p : ports) stringBuilder.append(p).append(", ");
        String text = stringBuilder.toString();
        if (text.length()>1) {
            text = text.substring(0, text.length()-2);
        }
        return text;
    }

    private void tryAttachToList(String ipAddress, String ports) {
        List<IpInfoHolder> ipInfoHolders = AppMemory.getInstance().getIpInfoList();
        boolean found = false;
        for(IpInfoHolder ipInfoHolder : ipInfoHolders) {
            if (ipInfoHolder.getIpAddress().equals(ipAddress)) {
                ipInfoHolder.setPorts(ports);
                found = true;
                break;
            }
        }
        if (!found) ipInfoHolders.add(new IpInfoHolder(ipAddress, ports));
    }

    public void parsePlainText(String text) throws Exception {
        StringTokenizer testTokenizer = new StringTokenizer(text, "\n");
        LOGGER.info("Lines to parse: " + String.valueOf(testTokenizer.countTokens()));

        while(true) {
            String line;
            String ipAddress = null;
            List<String> ports = new ArrayList<>();
            if(testTokenizer.hasMoreTokens()) line = testTokenizer.nextToken();
            else break;
            if (line.equals("")) {
                continue;
            }
            if (line.startsWith("Nmap done")) {
                continue;
            }

            if (line.startsWith("Nmap scan report for")) {
                ipAddress = line.substring("Nmap scan report for".length()+1).trim();
                do {
                    if (testTokenizer.hasMoreTokens()) line = testTokenizer.nextToken();
                    else {
                        String portsString = buildPortsString(ports);
                        throw new Exception("Malformed xml, last line: "+line + ", port string: " + portsString);
                    }
                    if (line.trim().equals("")) break;

                    if (!Character.isDigit(line.charAt(0))) continue;
                    ports.add(getPortFromString(line));
                } while (true);
                String portsString = buildPortsString(ports);
                LOGGER.info("Testing IP: " + ipAddress + " ports: " + portsString);
                tryAttachToList(ipAddress, portsString);
            }
        }
    }
}
