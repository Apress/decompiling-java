public  class Cubes
{
  public static void main(String[] args)
    {
     int local1[]=new int[10]; //4
     int local2[]=new int[10]; //9
     System.out.println(args[0]); //16
     for (int local3=1; local3<=10; local3++) //20
       {
        local1[local3 - 1]=(local3 * local3) * local3;
       }
     int local3=0; //44
     for (int local4=9; local3<10; local4--) //47
       {
        local2[local4]=local1[local3++];
       }
     for (local3=0; local3<10; local3++) //72
       {
        System.out.println(("arr[") + (local3) + ("] = ") + (local1[local3]) + (" arrCopy[") + (local3) + ("] = ") + (local2[local3])); //132
       }
     return; //141
    }
}