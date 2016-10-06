import java.applet.Applet;
import java.awt.Graphics;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HelloTCC extends Applet {
    public String getLocalHostName() {
		String result="";
        try {
            InetAddress address = InetAddress.getLocalHost();
            return (address.getHostName());
        }
        catch (UnknownHostException e) {
		   System.out.println(e);
           return ("Not known");
        }
        catch (Exception x) {
		   System.out.println(x);
           return ("Not known");
        }
    }
    public void paint(Graphics g) {
        g.drawString("Hello " + getLocalHostName() + "!", 50, 25);
    }
}
