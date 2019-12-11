package com.cloud.common.oauth.validate.sms;

/**
 * The interface Sms code sender.
 * @author Aijm
 * @since 2019/5/26
 */
public interface SmsCodeSender {

	/**
	 * Send.
	 *
	 * @param mobile the mobile
	 * @param code   the code
	 * @param ip     the ip
	 */
	void send(String mobile, String code, String ip);

}
