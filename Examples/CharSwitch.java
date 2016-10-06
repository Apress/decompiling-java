public class CharSwitch
{
  public static void main(String [] args)
    {
	 char standard='c';
	 switch (standard)
	 {
		case 'a':
		  System.out.println("No!");
		  break;
		case 'b':
		  System.out.println("Yes!");
		  break;
		case 'c':
		  System.out.println("Sorta.");
		  break;
		default:
		  System.out.println("Maybe.");
     }
    }
}