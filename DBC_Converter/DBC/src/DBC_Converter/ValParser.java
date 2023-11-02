package DBC_Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValParser {
    public String text;
    public int msgId;
    public String sigName;

    public boolean passString(String line)
    {
        int msgIdFromLine = 0;
        List<String> strOut = new ArrayList<>();

        Pattern pattern = Pattern.compile("\"[^\"]+\"|\\w+");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            strOut.add(matcher.group());
        }
        try {
            msgIdFromLine = Integer.parseInt(strOut.get(1));
        } catch (NumberFormatException e) {
            return false; // Parsing failed
        }
        msgId=msgIdFromLine & (~(1<<31));
        sigName= strOut.get(2);
        int pairs = (strOut.size() - 3) / 2;
        String text = "";

        for(int i=0;i<pairs;i++){
            text += "  // " + strOut.get(3 + i * 2) + " - " + strOut.get(3 + i * 2 + 1) + System.lineSeparator();
        }
        return true;
    }
}