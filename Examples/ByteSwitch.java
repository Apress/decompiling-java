public class ByteSwitch
{
  public static void main(String [] args)
    {
	 byte standard=(byte)0xFF;
	 switch (standard)
	 {
		default:
		  System.out.println("Maybe.");
		case (byte)0xAB:
		  System.out.println("No!");
		  break;
		case (byte)0xAC:
		  System.out.println("Yes!");
		  break;
		case (byte)0xFF:
		  System.out.println("Sorta.");
		  break;
     }
    }
}