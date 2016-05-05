package pt.meic.sec;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ByzantineRegularRegister {

    private final String[] _processesPorts;
    private final int _faults;
    private int _ts;
    private byte[] _val;
    private byte[] _signature;
    private int _wts;
    private HashMap<Integer, Boolean> _ackList;
    private int _rid;
    private HashMap<Integer, String> _readList;
    private int _nProcessesToTolerateFaults;


    //Byzantine quorum tolerating f faults
    public ByzantineRegularRegister(String[] processesPorts, int faults)
    {
        _nProcessesToTolerateFaults = processesPorts.length;
        _processesPorts = processesPorts;
        _faults = faults;

        _wts = 0;
        _ackList = new HashMap<Integer,Boolean>();
        _rid = 0;
        _readList = new HashMap<Integer,String>();
    }

    public byte[] read(byte[] data) throws IOException {
        _rid++;
        _readList = new HashMap<>();


        new ArrayList<Integer>().parallelStream().forEach(port -> {
            AuthPerfectPointToPointLinks al = new AuthPerfectPointToPointLinks(port);
            al.Send(data);
            byte[] result = al.Deliver();
            _readList.put(port, new String(result));
            _wts++;
            if(_wts > (_nProcessesToTolerateFaults + _faults) / 2){
                String highest = HighestHashMapTs(_readList);
                _readList.clear();
                return highest;
            }
        });

        return null;
    }



    private String HighestHashMapTs(HashMap<Integer, String> readList) {
        int localHighestTs = 0;
        String localHighestVal = "";
        for(Map.Entry<Integer, String> entry : readList.entrySet())
        {
            String value = entry.getValue();
            int ts = Integer
                    .valueOf(value.substring(0,
                            value.indexOf(Constants.DELIMITER)));
            if(ts > localHighestTs)
            {
                localHighestTs = ts;
                localHighestVal = value.substring(
                        value.indexOf(Constants.DELIMITER)+1);
            }
        }

        return localHighestVal;
    }


}
