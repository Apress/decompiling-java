public class NestedTableSwitch
{
  public static void main(String [] args)
    {
	 int standard=Integer.parseInt(args[0]),nonstandard=Integer.parseInt(args[1]);
	 switch (standard%11)
	 {
		case 1:
		  System.out.println("No!");
		  break;
		case 2:
		  System.out.println("Yes!");
		  switch (nonstandard)
		  {
			  case 1:
			     System.out.println("Well, I think.");
			     break;
			  case 2:
			     System.out.println("Possibly.");
			     break;
			  case 3:
			     System.out.println("That's what I heard, anyway.");
			     break;
			  case 4:
			     System.out.println("So I was told.");
			     break;
			  case 5:
			     System.out.println("But I don't really mean that.");
			     break;
			  default:
			     System.out.println("Psych!");
	  	  }
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