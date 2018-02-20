
package protonema;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFilter;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISORequestListener;
import org.jpos.iso.ISOSource;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

public class Protonema2 implements ISORequestListener{
    public boolean process (ISOSource isos, ISOMsg isomsg) {
        
        try {
            
            //kirim ke umam
            Space sp = SpaceFactory.getSpace("tspace:umam");
                
            ISOMsg m = new ISOMsg ();
            m.setMTI ("0800");
            m.set (11, "000001");
            m.set (41, "29110001");
            m.set (70, "301");

            sp.out ("umam-send", m);
            ISOMsg response = (ISOMsg) sp.in ("umam-receive", 60000);
            System.out.println(new String(response.pack()));
            
            isomsg.setResponseMTI();            
            isomsg.set(39,response.getString(39));
            isos.send(isomsg);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
       
        
        return true;
    }
}
