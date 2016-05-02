package Utils;

public class OneWriteNReadByzantineRegularRegister extends AuthPerfectPointToPointLinks {

	@Override
	public void Deliver(String port, String m) {
		// TODO Auto-generated method stub
		//m=[WRITE,...] ou m=[ACK,...] ou m=[READ,...] ou m=[VALUE,...]
	}

	private void DeliverWrite(int ts, String v, String signature)
	{
		
	}
	
	private String DeliverAck(int ts)
	{
		
	}
	
	private void DeliverRead(int ts, String v, String signature)
	{
		
	}
	
	private String DeliverValue(int ts)
	{
		
	}
	
	public void Init()
	{
		
	}
	
	public void Write(String v)
	{
		
	}
}
