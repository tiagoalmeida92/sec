package pt.meic.sec;

public class DependabilityException extends Exception{
	
	//Parameterless Constructor
	public DependabilityException() {}

	//Constructor that accepts a message
	public DependabilityException(String message)
	{
		super(message);
	}
}
