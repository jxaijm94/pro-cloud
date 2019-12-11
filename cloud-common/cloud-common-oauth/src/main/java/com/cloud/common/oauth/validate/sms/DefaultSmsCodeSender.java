
package com.cloud.common.oauth.validate.sms;

import lombok.extern.slf4j.Slf4j;

/**
 * 默认的短信验证码发送器
 * @author Aijm
 * @since 2019/5/26
 */
@Slf4j
public class DefaultSmsCodeSender implements SmsCodeSender {

	/**
	 * Send.
	 *
	 * @param mobile the mobile
	 * @param code   the code
	 * @param ip     the ip
	 */
	@Override
	public void send(String mobile, String code, String ip) {
		log.warn("请配置真实的短信验证码发送器(SmsCodeSender)");
		log.info("ip:{}，向手机{}发送短信验证码:{}", ip, mobile, code);
	}

}
