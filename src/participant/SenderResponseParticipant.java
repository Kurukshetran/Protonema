
package participant;

import protonema.Constants;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOSource;
import org.jpos.transaction.Context;
import org.jpos.transaction.TransactionParticipant;

import java.io.IOException;
import java.io.Serializable;


public class SenderResponseParticipant implements TransactionParticipant{
    @Override
    public int prepare(long l, Serializable serializable) {
        Context ctx = (Context)serializable;
        ISOMsg respMsg = (ISOMsg)ctx.get(Constants.RESPONSE_KEY);
        String bit39 = respMsg.getString(39);
        if(bit39==null){
            try {
                respMsg.set(39,"06");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ctx.put(Constants.RESPONSE_KEY,respMsg);
        return PREPARED;
    }

    @Override
    public void commit(long l, Serializable serializable) {
        sendMessage((Context)serializable);
    }

    @Override
    public void abort(long l, Serializable serializable) {
        sendMessage((Context)serializable);
    }
    private void sendMessage(Context context){
        ISOSource source = (ISOSource)context.get(Constants.RESOURCE_KEY);
        ISOMsg msgResp = (ISOMsg)context.get(Constants.RESPONSE_KEY);
        try {
            source.send(msgResp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ISOException e) {
            e.printStackTrace();
        }
    }
}