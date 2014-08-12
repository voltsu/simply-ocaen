package cn.simplyocean.jsonmodel;

public class SmsMessage {
	int resultCode;
	String dateCreated;
	String smsMessageSid;
	public int getResultCode() {
		return resultCode;
	}
	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getSmsMessageSid() {
		return smsMessageSid;
	}
	public void setSmsMessageSid(String smsMessageSid) {
		this.smsMessageSid = smsMessageSid;
	}
	@Override
	public String toString() {
		return "SmsMessage [resultCode=" + resultCode + ", dateCreated="
				+ dateCreated + ", smsMessageSid=" + smsMessageSid + "]";
	}
}
