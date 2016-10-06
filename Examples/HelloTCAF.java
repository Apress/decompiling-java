import java.applet.Applet;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloTCAF extends Applet {
    public String getLocalHostName() {
		String result="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            result = address.getHostName();
        }
        catch (Exception e) {
		   System.out.println(e);
           result = "Not known";
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
