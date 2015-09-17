
public class Robot 
{
	public static final int on = 0x0001;
	public static final int slow = 0x0002;
	public static final int fast = 0x0004;
	public static final int clockwise = 0x0008;
	public static final int counter = 0x0010;
	public static final int rotate90 = 0x0020;
	public static final int rotate180 = 0x0040;
	public static final int rotate270 = 0x0080;
	public static final int rotate360 = 0x0100;
	int  opcode;
	public Robot()
	{
		opcode = 0;
	}
	public Boolean Set(int value)
	{
		boolean bvalue = false;
		switch (value)
		{
		case on : 
			bvalue = true;
		case slow :
			if ( (opcode & fast) == 0 )
				bvalue = true;
			break;
		case fast :
			if ( (opcode & slow) == 0 )
				bvalue = true;
			break;
		case clockwise :
			if ( (opcode & counter) == 0 )
				bvalue = true;
			break;
		case counter :
			if ( (opcode & clockwise) == 0 )
				bvalue = true;
			break;
		case rotate90 :	
		case rotate180 :
		case rotate270 :
		case rotate360 :
			if ((opcode & 0x01E0) == 0)
				bvalue = true;
			break;
		}
		if (bvalue == true)
			opcode += value;
		return bvalue;
	}
	private String Validate()
	{
		String sresult = "Executing: ";
		boolean bvalue = false;
		if ((opcode & on) > 0)
		{
			bvalue = true;
			sresult = "On";
			if ((opcode & (slow+fast)) == (slow+fast))
				bvalue = false;
			if ((opcode & (clockwise+counter)) == (clockwise+counter))
				bvalue = false;
			int rotate = opcode & 0x01E0;
			if ((rotate != rotate90 ) && (rotate != rotate180) && (rotate != rotate270) && (rotate != rotate360))
				bvalue = false;
			if (bvalue == true)
			{
				if ((opcode & slow) > 0)
					sresult +="+Slow";
				if ((opcode & fast) > 0)
					sresult += "+Fast";
				
				if ((opcode & clockwise) > 0)
					sresult +="+Clockwise";
				if ((opcode & counter) > 0)
					sresult += "+Counter";
				
				if ((opcode & rotate90) > 0)
					sresult +="+Rotate90";
				if ((opcode & rotate180) > 0)
					sresult += "+Rotate180";
				if ((opcode & rotate270) > 0)
					sresult +="+Rotate270";
				if ((opcode & rotate360) > 0)
					sresult += "+Rotate360";
			}
			if (bvalue == false)
				sresult = "Invalid opcode";
		}
		return sresult;
	}
	public void Execute()
	{
		String svalue = Validate();
		System.out.println(svalue);
		opcode = 0;
	}
}
