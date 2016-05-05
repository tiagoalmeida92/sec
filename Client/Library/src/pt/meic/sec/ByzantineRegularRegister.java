package pt.meic.sec;

import java.util.HashMap;

public class ByzantineRegularRegister {

    private final String[] _processesPorts;
    private int _ts;
    private byte[] _val;
    private byte[] _signature;
    private int _wts;
    private HashMap<Integer, Boolean> _ackList;
    private int _rid;
    private HashMap<Integer, String> _readList;
    private int _nProcessesToTolerateFaults;



    //Byzantine quorum tolerating f faults
    public ByzantineRegularRegister(String[] processesPorts)
    {
        _nProcessesToTolerateFaults = processesPorts.length;
        _processesPorts = processesPorts;
        //Triple
        _ts = 0;
        _val = null;
        _signature = null;

        _wts = 0;
        _ackList = new HashMap<Integer,Boolean>();
        _rid = 0;
        _readList = new HashMap<Integer,String>();
    }

    public byte[] read(String method, byte[] params){

    }



}
