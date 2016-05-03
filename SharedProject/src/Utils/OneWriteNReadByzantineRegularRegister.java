package Utils;

import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.BiConsumer;

public class OneWriteNReadByzantineRegularRegister extends AuthPerfectPointToPointLinks {

	private int _ts;
	private String _val;
	private String _signature;
	private int _wts;
	private HashMap<Integer, Boolean> _ackList;
	private int _rid;
	private HashMap<Integer, String> _readList;
	private int _nProcessesToTolerateFaults;
	
	//Byzantine quorum tolerating f faults
	public OneWriteNReadByzantineRegularRegister(int nReplicas, int nFaults)
	{
		_nProcessesToTolerateFaults = (nReplicas + nFaults) / 2;
		//Triple
		_ts = 0;
		_val = null;
		_signature = null;
		
		_wts = 0;
		_ackList = new HashMap<Integer,Boolean>();
		_rid = 0;
		_readList = new HashMap<Integer,String>();
	}
	
	@Override
	public void Deliver(Socket connection, byte[] m) {
		//m=[WRITE,...] ou m=[ACK,...] ou m=[READ,...] ou m=[VALUE,...]
		String[] message = new String(m, StandardCharsets.UTF_8)
				.split(Constants.DELIMITER);
		switch(message[0])
		{
			case Constants.WRITETYPE:
				DeliverWrite(connection, Integer.valueOf(message[1]),message[2])
				break;
			case Constants.ADAPTED_WRITETYPE:
				break;	
			case Constants.READTYPE:
				break;
			default:
				break;
		}
	}

	public void Write(int[] ports, byte[] v)
	{
		++_wts;
		_ackList = new HashMap<Integer, Boolean>();
		String m = Constants.WRITETYPE +
				Constants.DELIMITER + _wts + 
				Constants.DELIMITER + Utils.byteToHex(v) +
				Constants.DELIMITER;
		
		//TODO ??qual assinatura??
		String signature = ""TODO;
		m = m+signature;
		
		//TODO filtrar o write?
		// enviar s� para 1 BS?
		// f+1 Sends?? (para Hashblock puth?)
		for(int port : ports)
		{
			Send(port, m.getBytes());
		}		
	}
	
	//Resposta BS com response ao PUTS ou PUTH
	private void DeliverWrite(int port, int ts, String v, String signature,
			String response)
	{
		if(ts > _ts)
		{
			_ts = ts;
			_val = v;
			_signature = signature;
			//CHAMAR SERVICE.PUTS?
			String m = Constants.ACKTYPE +
					Constants.DELIMITER + ts +
					Constants.DELIMITER + response;
			//como � s� 1 write n�o � preciso fazer ACK sempre
			//pode estar dentro da condi��o
			Send(port, m.getBytes());
		}
	}
	
	private String DeliverAck(int port, int ts, String response)
	{
		_ackList.put(port, true);
		if(_ackList.size() > _nProcessesToTolerateFaults)
		{
			_ackList = new HashMap<Integer, Boolean>();
			return response;
		}
		return null;
	}
	
	//(2_Read)
	private void DeliverRead(int port, int r, int ts, String v, String signature)
	{
		String m = Constants.VALUETYPE + Constants.DELIMITER +
				r + Constants.DELIMITER + ts + Constants.DELIMITER + 
				v + Constants.DELIMITER + signature;
		Send(port, m.getBytes());
	}
	
	private String DeliverValue(int port, int r, int ts, String v, String signature)
	{
		if(VerifySignature(signature))
		{
			_readList.put(port, ts + Constants.DELIMITER + v);
			if(_readList.size() > _nProcessesToTolerateFaults)
			{
				v = HighestHashMapTs(_readList);
				_readList = new HashMap<Integer,String>();
				return v;
			}
		}
	}
	
	private String HighestHashMapTs(HashMap<Integer, String> readList) {
		int localHighestTs = 0;
		String localHighestVal = "";
		for(Entry<Integer, String> entry : readList.entrySet()) 
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

	//(1_Read)
	public void Read(int[] ports, String id)
	{
		++_rid;
		_readList = new HashMap<>();
		for(int port : ports)
		{
			String m = Constants.READTYPE + Constants.DELIMITER + 
					_rid + Constants.DELIMITER + id;
			
			Send(port, m.getBytes());
		}
	}
}
