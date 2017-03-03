import java.io.Serializable;

public class MyNumber implements Serializable{

	private static final long serialVersionUID = 1L;
	private int number;
	
	public MyNumber(int number){
		this.number = number;
	}
	
	public int getNumber(){
		return number;
	}
}
