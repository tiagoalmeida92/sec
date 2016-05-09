package pt.meic.sec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.*;
import java.util.stream.Collectors;

public class ByzantineRegularRegister {

    private final List<Integer> _processesPorts;
    private final int _faults;
    private final PrivateKey _privateKey;
    private int _ts;
    private byte[] _val;
    private byte[] _signature;
    private int _wts;
    private HashMap<Integer, Boolean> _ackList;
    private int _rid;
    private HashMap<Integer, String> _readList;
    private int _replicas;
    private byte[] writeResult;


    //Byzantine quorum tolerating f faults
    public ByzantineRegularRegister(PrivateKey privateKey, String[]processesPorts, int faults)
    {
        _replicas = processesPorts.length;
        _processesPorts = Arrays.stream(processesPorts).map(Integer::parseInt).collect(Collectors.toList());
        _faults = faults;
        _privateKey = privateKey;
        _wts = 0;
        _ackList = new HashMap<>();
        _rid = 0;
        _readList = new HashMap<>();
    }

    public byte[] read(byte[] data) throws IOException {
        _rid++;
        _readList.clear();


        _processesPorts.parallelStream().forEach(port -> {
            AuthPerfectPointToPointLinks al = null;
            try {
                al = new AuthPerfectPointToPointLinks(port);
                al.Send(data);
                byte[] result = al.Deliver();
                _readList.put(port, new String(result));
                _wts++;

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }


        });
        if(_readList.size() > (_replicas + _faults) / 2){
            String highest = HighestHashMapTs(_readList);
            _readList.clear();
            return highest.getBytes();
        }

        return null;
    }

    public byte[] write(byte[] data) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        _wts++;
        _ackList.clear();
        byte[] signature = SecurityUtils.Sign(data, _privateKey);

        _processesPorts.parallelStream().forEach(port -> {
            try {
                AuthPerfectPointToPointLinks al = new AuthPerfectPointToPointLinks(port);
                al.Send(Utils.concat(data, signature));
                writeResult = al.Deliver();

                _ackList.put(port, true);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        if(_ackList.size() > (_replicas + _faults) / 2){
            _ackList.clear();
            return writeResult;
        }
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
