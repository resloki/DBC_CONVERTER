package DBC_Converter;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageDescriptor {
    public enum Direction {
        None, Tx, Rx, Both, NoCode
    }
    private String messageName;
    private int dataLen;
    private long messageId;
    private byte msgHiBits;
    private byte isExtended;
    private int cycTime=0;
    private Direction direction = Direction.None;
    private String transmitter=null;
    private String[] sigsToByteExpr=new String[8];
    private List<String> recS = new ArrayList<>();
    private String commentText;
    private List<SignalBitsDesc> signals = new ArrayList<SignalBitsDesc>();


    public String getMessageName() {
            return messageName;
    }
    public void setMessageName(String messageName) {
            this.messageName = messageName;
    }

    public int getDataLen(){
        return dataLen;
    }
    public void setDataLen(int dataLen){
        this.dataLen=dataLen;
    }

    public long getMessageId(){return messageId;}
    public void setMessageId(long messageId){
        this.messageId=messageId;
    }

    public byte getMsgHiBits(){
        return msgHiBits;
    }
    public void setMsgHiBits(byte msgHiBits){
        this.msgHiBits=msgHiBits;
    }

    public byte getIsExtended(){
        return isExtended;
    }
    public void setIsExtended(byte isExtended){
        this.isExtended=isExtended;
    }

    public int getCycTime() {return cycTime;}

    public void setCycTime(int cycTime) {this.cycTime = cycTime;}

    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getTransmitter(){
        return transmitter;
    }
    public void setTransmitter(String transmitter){
        this.transmitter=transmitter;
    }

    public String[] getSigsToByteExpr() {
        return sigsToByteExpr;
    }
    public void setSigsToByteExpr(int index, String value) {
        sigsToByteExpr[index] = value;
    }

    public List<String> getRecS() {
        return recS;
    }
    public void setRecS(List<String> recS) {
        this.recS = recS;
    }

    public String getCommentText() {return commentText;}
    public void setCommentText(String commentText) {this.commentText = commentText;}

    public List<SignalBitsDesc> getSignals() {
        return signals;
    }
    public void setSignals(List<SignalBitsDesc> signals) {
        this.signals = signals;
    }

    public void addSignals(List<SignalBitsDesc> list) {
        for (SignalBitsDesc sig : list) {
            for (String rx : sig.getReceivers()) {
                if (rx.length() > 0 && getRecS().contains(rx)==false) {
                    getRecS().add(rx);
                }
            }
        }
        Collections.sort(getRecS());
        setSignals(list);
    }

    public String PrintRecs(){
        String ret="";
        for(int i=0;i<getRecS().size();i++){
            if(i>0){
                ret +=", "+ getRecS().get(i);
            }else{
                ret +=getRecS().get(i);
            }
        }
        return ret;
    }

    private static final String VectorMessageDbcPattern = "[^A-Za-z0-9_.-]";
    
    public boolean parseVectorDbcLine(String line){
        if(line.startsWith("BO_ ")){
            String[] raw = line.split(VectorMessageDbcPattern);
            
            setMessageName(raw[2]);
            setMessageId(Long.parseLong(raw[1]));
            setDataLen(Integer.parseInt(raw[4]));
            setTransmitter(raw[5]);
            
            
            if((getMessageId() & 0x60000000)!=0 || getDataLen() ==0 || getDataLen() > 8){
                return false;
            }
            
            setMsgHiBits((byte)((getMessageId() >> 29) & 0x04));
            
            if(getMsgHiBits()==0x04){
                setIsExtended((byte)1);
            }
            setMessageId(getMessageId() & (0x1fffFFFF));
            
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("Msg: %s ID[%08X] len=%d", getMessageName(), getMessageId(), getDataLen());
    }

    public String printMsgIDName() {
        return getMessageName() + "_CANID";
    }

    public String printMsgIDValue() {
        int maskedMessageId =(int) getMessageId() & 0x1fffffff;
        return "0x" + Integer.toHexString(maskedMessageId).toUpperCase();
    }


    private SignalBitsDesc rollSig;
    private SignalBitsDesc csmSig;
    public SignalBitsDesc getRollSig() {
        return rollSig;
    }
    void setRollSig(SignalBitsDesc value) {
        rollSig = value;
    }

    public SignalBitsDesc getCsmSig() {
        return csmSig;
    }
     void setCsmSig(SignalBitsDesc value) {
        csmSig = value;
    }

    public String csmType;

    public String getCsmType() {
        return csmType;
    }
    void setCsmType(String value) {
        csmType = value;
    }

}
