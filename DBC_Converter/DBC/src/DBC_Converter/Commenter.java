package DBC_Converter;

public class Commenter {
    private boolean isCollectingData;
    private CommentDescription info = new CommentDescription();

    public boolean getIsCollectingData() {
        return isCollectingData;
    }
    public void setCollectingData(boolean isCollectingData) {
        this.isCollectingData = isCollectingData;
    }

    public CommentDescription getInfo() {
        return info;
    }

    public void setInfo(CommentDescription info) {
        this.info = info;
    }

    public boolean passString(String line){
        if(line.startsWith("CM_")){
            isCollectingData=true;
            String[] strs = line.split("\"+");
            String[] directs = strs[0].split("[ ]+");

            if(directs[1]=="SG_"){
                info.type=CommentType.SignalComment;
                info.msgId=Integer.parseUnsignedInt(directs[2]) & (~(1 << 31));
                info.sigName=directs[3];
            } else if (directs[1]=="BO_") {
                info.type=CommentType.MessageComment;
                info.msgId=Integer.parseUnsignedInt(directs[2]) & (~(1 << 31));
            } else if (directs[1]=="BU_") {
                info.type=CommentType.NodeComment;
                info.nodeName=directs[2];
            }else {
                isCollectingData=false;
            }

            info.text=strs[1];
            if (strs.length == 3)
            {
                isCollectingData = false;
                return true;
            }
        } else if (isCollectingData==true) {
            String[] strs = line.split("\"+");
            info.text+="   "+strs[0];
            if (strs.length > 1)
            {
                isCollectingData = false;
                return true;
            }
        }
        return false;
    }
}
