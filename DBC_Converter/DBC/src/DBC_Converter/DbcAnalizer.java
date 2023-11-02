package DBC_Converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class DbcAnalizer {

	private List<MessageDescriptor> messages = new ArrayList<MessageDescriptor>();

	public List<MessageDescriptor> getMessages() {
		return messages;
	}

	public void setMessages(List<MessageDescriptor> messages) {
		this.messages = messages;
	}

	public void trimDbcRawStream(StringReader reader) throws IOException {
		String line = "";
		MessageDescriptor msgDesc = null;
		BufferedReader read = new BufferedReader(reader);
		while ((line = read.readLine()) != null) {
			if (line.startsWith("BO_ ")) {
				if (msgDesc != null) {

					List<SignalBitsDesc> signals = msgDesc.getSignals();
					Collections.sort(signals,
							(signal1, signal2) -> Integer.compare(signal1.getStartBit(), signal2.getStartBit()));
					msgDesc.addSignals(signals);
					getMessages().add(msgDesc);
				}
				msgDesc = new MessageDescriptor();

				if (!msgDesc.parseVectorDbcLine(line)) {
					msgDesc = null;
				}
			}
			if ((regSigStart.matcher(line).find()) && msgDesc != null) {
				SignalBitsDesc sig = new SignalBitsDesc();
				sig.parseFromVectorDbcString(line);
				msgDesc.getSignals().add(sig);
			} else if (line.startsWith("BA_")) {
				AttrDescriptor bastr = new AttrDescriptor();
				if (bastr.parseBA_(line) == true) {
					if (getBa_().stream().noneMatch(b -> b.getMsgId() == bastr.getMsgId())) {
						getBa_().add(bastr);
					}
				}
			}

			if (line.startsWith("CM_") || commenter.getIsCollectingData()) {
				if (commenter.passString(line) == true) {
					if (commenter.getInfo().type == CommentType.MessageComment) {
						Optional<MessageDescriptor> msg = messages.stream()
								.filter(m -> m.getMessageId() == commenter.getInfo().msgId).findFirst();

						if (msg.isPresent()) {
							MessageDescriptor message = msg.get(); // Extract
																	// the value
																	// from
																	// Optional
							message.setCommentText(commenter.getInfo().text);
						}
					} else if (commenter.getInfo().type == CommentType.SignalComment) {
						Optional<MessageDescriptor> msg = messages.stream()
								.filter(m -> m.getMessageId() == commenter.getInfo().msgId).findFirst();

						if (msg.isPresent()) {
							MessageDescriptor message = msg.get();


							Optional<SignalBitsDesc> sig = message.getSignals().stream()
									.filter(s -> s.getFieldName() == commenter.getInfo().sigName).findFirst();


							if (sig.isPresent()) {
								SignalBitsDesc sign = sig.get(); 
								sign.setCommentText(commenter.getInfo().text);


								if (getIsRollingCounterCodeAvailable()
										&& sign.getCommentText().contains(rollingCounterMatch)) {
									if (message.getRollSig() == null) {
										message.setRollSig(sign);
									}
								}

								else if (getIsCheckSumMatchCodeAvailable()
										&& sign.getCommentText().contains(checkSumMatch)) {
									{
										if (message.getCsmSig() == null) {
											message.setCsmType("kCRCUndefined");
											if (sign.getCommentText().contains("CRC8_SAEJ1850")) {
												message.setCsmType("kSAEJ1850");
											} else if (sign.getCommentText().contains("XOR8_Generic")) {
												message.setCsmType("kXOR8");
											} else if (sign.getCommentText().contains("XOR4_generic")
													|| sign.getCommentText().contains("XOR4_SAS")) {
												message.setCsmType("kXOR4");
											} else if (sign.getCommentText().contains("XOR")) {
												message.setCsmType("kXOR8");
											}
											message.setCsmSig(sign);
										}
									}
								}
							}
						}
					}
				}
			} else if (line.startsWith("VAL_ ")) {

				valuator.passString(line);
				Optional<MessageDescriptor> msg = messages.stream().filter(m -> m.getMessageId() == valuator.msgId)
						.findFirst();

				if (msg.isPresent()) {
					MessageDescriptor foundMessage = msg.get();

					Optional<SignalBitsDesc> sig = foundMessage.getSignals().stream()
							.filter(s -> s.getFieldName() == valuator.sigName).findFirst();


					if (sig.isPresent()) {
						SignalBitsDesc foundSignal = sig.get();
						foundSignal.setValueText(valuator.text);
					}
				}
			}
			Collections.sort(messages, (m1, m2) -> Integer.compare((int) m1.getMessageId(), (int) m2.getMessageId()));
		}
		if (msgDesc != null) {

			Collections.sort(msgDesc.getSignals(), (s1, s2) -> Integer.compare(s1.getStartBit(), s2.getStartBit()));
			messages.add(msgDesc);
		}
		for (AttrDescriptor ba : ba_) {

			Optional<MessageDescriptor> msg = messages.stream().filter(d -> d.getMessageId() == ba.getMsgId())
					.findFirst();

			if (msg.isPresent()) {
				MessageDescriptor found_Message = msg.get();

				found_Message.setCycTime(ba.getCycTime());
			}
		}
	}

	Pattern regSigStart = Pattern.compile("^(\\s+SG_)");
	private List<AttrDescriptor> ba_ = new ArrayList<AttrDescriptor>();
	Commenter commenter = new Commenter();
	ValParser valuator = new ValParser();
	private boolean isRollingCounterCodeAvailable = false;
	private String rollingCounterMatch = "Rolling Counter";
	private boolean isCheckSumMatchCodeAvailable = false;
	private String checkSumMatch = "Checksum";

	public List<AttrDescriptor> getBa_() {
		return ba_;
	}

	public void setBa_(List<AttrDescriptor> ba_) {
		this.ba_ = ba_;
	}
	
	public boolean getIsRollingCounterCodeAvailable() {
		return isRollingCounterCodeAvailable;
	}

	public void setIsRollingCounterCodeAvailable(boolean isRollingCounterCodeAvailable) {
		this.isRollingCounterCodeAvailable = isRollingCounterCodeAvailable;
	}

	public String getRollingCounterMatch() {
		return rollingCounterMatch;
	}

	public void setRollingCounterMatch(String rollingCounterMatch) {
		this.rollingCounterMatch = rollingCounterMatch;
	}

	public boolean getIsCheckSumMatchCodeAvailable() {
		return isCheckSumMatchCodeAvailable;
	}

	public void setIsCheckSumMatchCodeAvailable(boolean isCheckSumMatchCodeAvailable) {
		this.isCheckSumMatchCodeAvailable = isCheckSumMatchCodeAvailable;
	}

	public String getCheckSumMatch() {
		return checkSumMatch;
	}

	public void setCheckSumMatch(String checkSumMatch) {
		this.checkSumMatch = checkSumMatch;
	}
}
