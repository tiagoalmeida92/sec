package Utils;

public abstract class AuthPerfectPointToPointLinks {
	
	public void Send(String port, String m)
	{
		
	}
	
	public abstract void Deliver(String port, String m);
	
	public void Init()
	{
		//It is the function of TCP
		//Not necessary the delivered logic..
	}
}
