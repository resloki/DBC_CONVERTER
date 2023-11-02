package DBC_Converter;

public class AttrDescriptor {
    private int cycTime=0;
    private int msgId=0;

    public int getCycTime() {
        return cycTime;
    }
    public void setCycTime(int cycTime) {
        this.cycTime = cycTime;
    }

    public int getMsgId() {
        return msgId;
    }
    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public boolean parseBA_(String line){
        if(!line.contains("GenMsgCycleTime")){
            return false;
        }
        String[] strs = line.split("[^A-Za-z0-9_]+");
        if(strs.length!=6){
            return false;
        }
        int value=0;
        if (strs.length > 3) {
            try {
                value = Integer.parseInt(strs[3]);
                setMsgId( value & 0x1FFF_FFFF);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }

        if (strs.length > 4) {
            try {
                value = Integer.parseInt(strs[4]);
                setCycTime(value);
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }
}
