
package protonema;

import org.jdom.Element;

public interface QEchoMBean extends org.jpos.q2.QBeanSupportMBean {

    public Element getPersist();

    public void setTickInterval(long tickInterval);

    public long getTickInterval();
    
}
