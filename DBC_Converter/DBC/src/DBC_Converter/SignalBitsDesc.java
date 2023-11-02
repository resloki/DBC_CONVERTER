package DBC_Converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class SignalBitsDesc {
	private List<String> receivers = new ArrayList<>();
	private String fieldName;
	private String fieldUnit;
	private int startBit;
	private int lengthBit;
	private double factor;
	private double offset;
	private int rawOffset;
	private BitLayout order;
	private boolean signed;
	private SigTypeEnum sigType;
	private String[] sigToByte = new String[8];
	private int[] maskValue = new int[8];
	private String[] maskValueToString = new String[8];
	private double minValue;
	private double maxValue;
	private String commentText;
	public String valueText;

	public String getFieldName() {
		return fieldName;
	}

	private void setFieldName(String value) {
		fieldName = value;
	}

	public String getFieldUnit() {
		return fieldUnit;
	}

	public void setFieldUnit(String value) {
		fieldUnit = value;
	}

	public int getStartBit() {
		return startBit;
	}

	public void setStartBit(int startBit) {
		this.startBit = startBit;
	}

	public int getLengthBit() {
		return lengthBit;
	}

	public void setLengthBit(int lengthBit) {
		this.lengthBit = lengthBit;
	}

	public double getFactor() {
		return factor;
	}

	public void setFactor(double factor) {
		this.factor = factor;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public int getRawOffset() {
		return rawOffset;
	}

	public void setRawOffset(int rawOffset) {
		this.rawOffset = rawOffset;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

	public BitLayout getOrder() {
		return order;
	}

	public void setOrder(BitLayout order) {
		this.order = order;
	}

	public boolean getSigned() {
		return signed;
	}

	public void setSigned(boolean signed) {
		this.signed = signed;
	}

	public SigTypeEnum getSigType() {
		return sigType;
	}

	public void setSigType(SigTypeEnum sigType) {
		this.sigType = sigType;
	}

	public String[] getSigToByte() {
		return sigToByte;
	}

	public void setSigToByte(String[] sigToByte) {
		this.sigToByte = sigToByte;
	}

	public int[] getMaskValue() {
		return maskValue;
	}

	public void setMaskValue(int[] maskValue) {
		this.maskValue = maskValue;
	}

	public String[] getMaskValueToString() {
		return this.maskValueToString;
	}

	public void setMaskValueToString(String[] maskValueToString) {
		this.maskValueToString = maskValueToString;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public String getValueText() {
		return valueText;
	}

	public void setValueText(String valueText) {
		this.valueText = valueText;
	}

	@Override
	public String toString() {
		return "Start bit:" + getStartBit() + ". Length:" + getLengthBit() + ". " + getOrder();
	}

	public static final StringBuilder sBuild = new StringBuilder(512);
	public static final String dbcHalfPattern = "(\\s+:\\s+)";
	public static final String dbcSignalSecondHalfPattern = "\"";

	String vectorDbcPattern = "[^A-Za-z0-9_.+-]+";

	static long[] maxValues = { 255L, 65535L, 4294967295L, Long.MAX_VALUE };

	static int[] typesLen = { 8, 16, 32, 64 };

	public void parseFromVectorDbcString(String line) {

		String[] halfsplit = line.split(dbcHalfPattern);
		String[] split2ndpart = halfsplit[1].split(dbcSignalSecondHalfPattern);
		String[] rawSplit = split2ndpart[0].trim().split(vectorDbcPattern);
		halfsplit = halfsplit[0].split("[^\\w]+");

		try {			
			
			if(halfsplit.length > 2) {
				setFieldName(halfsplit[2]);
			}
			if (rawSplit.length > 0) {
				setStartBit(Integer.parseInt(rawSplit[0]));
			}
			if (rawSplit.length > 1) {
				setLengthBit(Integer.parseInt(rawSplit[1]));
			}
			if (rawSplit.length > 3) {
				setFactor(Double.parseDouble(rawSplit[3]));
			}
			if (rawSplit.length > 4) {
				setOffset(Double.parseDouble(rawSplit[4]));
			}
			setRawOffset((int) (getOffset() / getFactor()));

			if (rawSplit.length > 5) {
				setMinValue(Double.parseDouble(rawSplit[5]));
			}
			if (rawSplit.length > 6) {
				setMaxValue(Double.parseDouble(rawSplit[6]));
			}

			String rawSplitValue = rawSplit[2];
			setOrder((rawSplitValue.contains("1")) ? BitLayout.Intel : BitLayout.Motorolla);
			setSigned((rawSplitValue.contains("-")));
			setSigType(GetSigType());

		} catch (Exception e) {
			System.out.println("(!)Parsing exception occurred in line '" + line + "'.\n" + "Text: " + e.getMessage());
		}

		setFieldUnit("-");


		if (split2ndpart.length == 3) {
			setFieldUnit(split2ndpart[1]);
		}


		String[] receivers = split2ndpart[split2ndpart.length - 1].trim().split(vectorDbcPattern);
		getReceivers().clear();

		for (String rx : receivers) {
			getReceivers().add(rx);
		}
	}

	private SigTypeEnum GetSigType() {
		int isUnsign = 0;
		int len = lengthBit;
		SigTypeEnum sizeType = SigTypeEnum.i64;
		long roffset = (long) (getOffset() / getFactor());

		if (!getSigned()) {
			long maxvalue = 0;
			if (getLengthBit() < 33) {
				if (roffset >= 0) {
					maxvalue = (long) (Math.pow(2, getLengthBit()) - 1 + roffset);
					isUnsign = 1;
				} else {
					long maxPos = (long) (Math.pow(2, getLengthBit() + 1) - 1);
					long maxNeg = (long) (Math.abs(roffset * 2));
					maxvalue = Math.max(maxPos, maxNeg);
				}
				for (int i = 0; i < 4; i++) {
					if (maxvalue <= maxValues[i]) {
						sizeType = SigTypeEnum.values()[i + isUnsign * 4];
						break;
					}
				}
			} else {
				isUnsign = 1;
			}
		} else {
			for (int i = 0; i < 4; i++) {
				if (len <= typesLen[i]) {
					sizeType = SigTypeEnum.values()[i + isUnsign * 4];
					break;
				}
			}
		}
		return sizeType;
	}
}
