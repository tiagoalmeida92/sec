package pt.meic.sec;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.*;

public class ByzantineRegularRegister {

    private final List<Integer> _processesPorts;
    private final int _faults;
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
    public ByzantineRegularRegister(List<Integer> processesPorts, int faults)
    {
        _replicas = processesPorts.size();
        _processesPorts = processesPorts;
        _faults = faults;
        _wts = 0;
        _ackList = new HashMap<>();
        _rid = 0;
        _readList = new HashMap<>();
    }

    public byte[] read(ReadType readType, String id) throws IOException {
        _rid++;
        _readList.clear();

        String header = Constants.READ_BLOCK;
        byte[] request = (header + Constants.DELIMITER
                + id + Constants.DELIMITER
                +_rid).getBytes();

        _processesPorts.parallelStream().limit(_faults + 1).forEach(port -> {
            AuthPerfectPointToPointLinks al = null;
            try {
                al = new AuthPerfectPointToPointLinks(port);
                al.Send(request);
                byte[] deliver = al.Deliver();
                if(verifyReadResponse(deliver, readType, id)) {
                    _readList.put(port, new String(deliver));
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        if(_readList.size() > (_replicas + _faults) / 2){
            String highest = HighestHashMapTs(_readList);
            _readList.clear();
            byte[] finalRes = getReadResult(readType, highest);
            return finalRes;
        }

        return null;
    }

    public byte[] write(String header, byte[] data, String expectedHash, int timeStamp) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        _wts++;
        _ackList.clear();

        int totalCalls;
        float neededQuorom;
        if(header.equals(Constants.PUT_PUBLIC_KEY_BLOCK)){
            totalCalls = _faults * 3 + 1;
            neededQuorom = (float)(_replicas + _faults) / 2;
        }else {
            //PUT H self verifying
            totalCalls = _faults + 1;
            neededQuorom = 0.5f;
        }

        _processesPorts.parallelStream().limit(totalCalls).forEach(port -> {
            try {
                AuthPerfectPointToPointLinks al = new AuthPerfectPointToPointLinks(port);
                al.Send(data);

                byte[] deliver = al.Deliver();
                byte[] result = verifyAndGetWriteResponse(deliver, expectedHash, timeStamp);
                if(result != null){
                    writeResult = result;
                    _ackList.put(port, true);
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        if(_ackList.size() > neededQuorom){
            _ackList.clear();
            return writeResult;
        }
        return null;
    }

    private byte[] getReadResult(ReadType type, String highest) {
        String res = highest.split(Constants.DELIMITER)[2];
        return  SecurityUtils.hexStringToByteArray(res);
    }

    private boolean verifyReadResponse(byte[] result, ReadType readType, String id) {
        String stringRes = new String(result);
        String[] tokens = stringRes.split(Constants.DELIMITER);
        String ack = tokens[0];
        int rid = Integer.parseInt(tokens[1]);
        byte[] data = SecurityUtils.hexStringToByteArray(tokens[2]);
        if(ack.equals(Constants.READ_ACK) && rid == _rid){

            if(readType == ReadType.PublicKeyBlock){
                try {
                    PublicKeyBlock publicKeyBlock = PublicKeyBlock.createFromBytes(data);
                    if(publicKeyBlock.verifySignature(id)){
                        return true;
                    }
                } catch (DependabilityException | NoSuchAlgorithmException |SignatureException | InvalidKeyException e) {
                        return false;
                }


            }else if(readType == ReadType.ContentBlock){
                String hash = null;
                try {
                    hash = SecurityUtils.Hash(data);
                } catch (NoSuchAlgorithmException e) {
                    return false;
                }
                return id.equals(hash);
            }

        }
        return false;
    }



    private byte[] verifyAndGetWriteResponse(byte[] result, String expectedHash, int timeStamp) {
        String stringRes = new String(result);
        String[] tokens = stringRes.split(Constants.DELIMITER);
        String ack = tokens[0];
        if(ack.equals(Constants.ADAPTED_ACKTYPE) && tokens[1].equals(expectedHash)){
            return tokens[1].getBytes();
        }else if(ack.equals(Constants.ACKTYPE)){
            int ts = Integer.parseInt(tokens[1]);
            if (ts == timeStamp) return tokens[2].getBytes();
            else return null;
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
                    .valueOf(value.split(Constants.DELIMITER)[1]);
            if(ts > localHighestTs)
            {
                localHighestTs = ts;
                localHighestVal = value;
            }
        }

        return localHighestVal;
    }


}
