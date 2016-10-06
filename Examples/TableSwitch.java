public class TableSwitch
{
  public static void main(String [] args)
    {
	 int standard=Integer.parseInt(args[0]);
	 switch (standard%11)
	 {
		case 1:
		  System.out.println("No!");
		  break;
		case 2:
		  System.out.println("Yes!");
		  break;
		case 3:
		  System.out.println("Yes!");
		  break;
		case 4:
		  System.out.println("Yes!");
		  break;
		case 5:
		  System.out.println("Yes!");
		  break;
		case 6:
		  System.out.println("Yes!");
		  break;
		case 7:
		  System.out.println("Yes!");
		  break;
		default:
		  System.out.println("Maybe.");
     }
     System.out.println("So?");
    }
}