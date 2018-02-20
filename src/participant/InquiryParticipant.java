
package participant;

import protonema.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import java.io.Serializable;
import org.jpos.core.Configuration;
import org.jpos.q2.Q2;
import org.jpos.q2.qbean.QConfig;
import org.jpos.space.Space;
import org.jpos.space.SpaceFactory;
import static org.jpos.transaction.TransactionConstants.PREPARED;
import org.jpos.util.Log;


public class InquiryParticipant implements TransactionParticipant{
    
    Log log;  

    @Override
    public int prepare(long l, Serializable serializable) {
        Context ctx = (Context)serializable;
        ISOMsg respMsg = (ISOMsg)ctx.get(Constants.RESPONSE_KEY);
        ISOMsg reqMsg = (ISOMsg)ctx.get(Constants.REQUEST_KEY);
        
        log = Log.getLog(Q2.LOGGER_NAME, "INQUIRY ["+reqMsg.getString(11)+"]");
        String TAG = "["+reqMsg.getString(11)+"] "; 
        
        try {
            
            log.info(TAG+"Incoming : "+new String(reqMsg.pack()));
                        
            /* product */
            String pan = reqMsg.getString(2); log.info(TAG+"PAN : "+pan);
            
            /* partner cid */
            String cid_sender = reqMsg.getString(33); log.info(TAG+"CID Sender : "+cid_sender);
            
            String systemCid    = "4567898";
            String systemSpace  = "transient:default";
            String systemOut    = "biller-send";
            String systemIn     = "biller-receive";
            
            reqMsg.set(33,systemCid);
            
            
            /* Sending message to specific mux channel */
            Space sp = SpaceFactory.getSpace(systemSpace);            
            sp.out (systemOut, reqMsg,10000);
            ISOMsg response = (ISOMsg) sp.in (systemIn, 10000);
            
            /* returning to partner */
            response.set(33,cid_sender);
            
            log.info(TAG+"Outgoing : "+new String(response.pack()));
            
            ctx.put(Constants.RESPONSE_KEY,response);
            
        } catch (Exception e) {
            e.printStackTrace();
            log.fatal(e);
        }
        return PREPARED;
    }

    @Override
    public void commit(long l, Serializable serializable) {

    }

    @Override
    public void abort(long l, Serializable serializable) {
    }
}
