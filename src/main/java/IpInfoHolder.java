import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IpInfoHolder {
    private String ipAddress;
    private List<ExploitHolder> foundExploits;
    private String ports;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public List<ExploitHolder> getFoundExploits() {
        return foundExploits;
    }

    public void setFoundExploits(List<ExploitHolder> foundExploits) {
        this.foundExploits = foundExploits;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public IpInfoHolder (String ipAddress, String ports) {
        this.ipAddress=ipAddress;
        this.ports=ports;
    }

    public IpInfoHolder (String ipAddress, List<ExploitHolder> holders) {
        this.ipAddress = ipAddress;
        foundExploits = holders;
        //foundExploits = new ArrayList<>();
        //foundExploits.addAll(Arrays.asList(holders));
    }
}
