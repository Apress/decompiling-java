public class LookupSwitch
{
  public static void main(String [] args)
    {
	 int standard=12;
	 switch (standard%11)
	 {
		case 0:
		  System.out.println("No!");
		case 12:
		  System.out.println("Yes!");
		default:
		  System.out.println("Maybe.");
     }
    }
}