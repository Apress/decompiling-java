import java.applet.Applet;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloTCCF extends Applet {
    public String getLocalHostName() {
		String result="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            result = address.getHostName();
        }
        catch (UnknownHostException e) {
		   System.out.println(e);
           result = "Not known";
        }
        catch (Exception e) {
		   System.out.println(e);
           result = "Error!";
        }
        finally {
		   System.out.println(result);
	    }
		return result;
    }
    public void paint(Graphics g) {
        g.drawString("Hello " + getLocalHostName() + "!", 50, 25);
    }
}
