import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppMemory {
    private static AppMemory _instance;
    private List<IpInfoHolder> ipInfoList;

    private AppMemory(){}

    synchronized public static AppMemory getInstance() {
        if (_instance == null) {
            _instance = new AppMemory();
            _instance.ipInfoList = new ArrayList<IpInfoHolder>();
        }
        return _instance;
    }

    public List<IpInfoHolder> getIpInfoList() {
        return ipInfoList;
    }


}
