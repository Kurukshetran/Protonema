package protonema;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.jdom.Element;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.jpos.q2.QBeanSupport;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;

public class QEcho extends QBeanSupport implements Runnable, QEchoMBean {

    long tickInterval = 3000;

    public QEcho() {
        super();
        log.info("constructor");
    }

    public void setTickInterval(long tickInterval) {
        this.tickInterval = tickInterval;
        setModified(true);
    }

    public long getTickInterval() {
        return tickInterval;
    }

    public void run() {
        for (int tickCount = 0; running(); tickCount++) {
            
            try {

                Space sp = SpaceFactory.getSpace("tspace:biller");

                ISOMsg m = new ISOMsg();
                m.setMTI("0800");
                m.set(11,"000001");
                String tgl = new SimpleDateFormat("yyyyMM").format(new Date());
                //m.set(12, tgl);
                //m.set(33, "4567898");
                m.set(70, "301");
                m.set(41, "000001");
                //m.set(48, "0000000");
                
                GenericPackager packager = new GenericPackager("cfg/packager/iso87ascii.xml");
                m.setPackager(packager);                
                log.info("Echo request : " + new String(m.pack()));
                
                sp.out("biller-send", m, 10000);
                ISOMsg response = (ISOMsg) sp.in("biller-receive", 10000);
                
                log.info("Echo response : " + new String(response.pack()));

            } catch (Exception e) {
                e.printStackTrace();
            }

            ISOUtil.sleep(tickInterval);
        }
    }

    public void startService() {
        new Thread(this).start();
    }

    
}
