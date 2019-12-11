package com.cloud.common.oauth.handler;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.map.MapUtil;

import cn.hutool.core.util.StrUtil;
import com.cloud.common.oauth.properties.SecurityProperties;
import com.cloud.common.util.base.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *  APP环境下认证成功处理器
 * @author Aijm
 * @since 2019/5/27
 */
@Slf4j
@Component("proAuthenticationSuccessHandler")
public class ProAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ClientDetailsService clientDetailsService;

	@Autowired
	private SecurityProperties securityProperties;

	/**
	 * 用lazy 解决死循环问题
	 */
	@Lazy
	@Autowired
	private AuthorizationServerTokenServices authorizationServerTokenServices;

	private static final String BEARER_TOKEN_TYPE = "Basic ";

	@SuppressWarnings("unchecked")
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

		logger.info("登录成功");

		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		String clientId = securityProperties.getClient().getClientId();
		String clientSecret = securityProperties.getClient().getClientSecret();
		if (StrUtil.isNotBlank(header) && header.startsWith(BEARER_TOKEN_TYPE)) {
			String[] tokens = extractAndDecodeHeader(header);
			assert tokens.length == 2;
			clientId = tokens[0];
			clientSecret = tokens[1];
			log.info("请求头中信息为clientId:{};clientSecret:{}", clientId, clientSecret);
		}
		ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);

		if (StrUtil.isBlank(clientSecret)) {
			throw new UnapprovedClientAuthenticationException("clientId对应的配置信息不存在:" + clientId);
		} else {
			if (!passwordEncoder.matches(clientSecret, clientDetails.getClientSecret())) {
				throw new UnapprovedClientAuthenticationException("clientSecret不匹配:" + clientId);
			}
		}

		TokenRequest tokenRequest = new TokenRequest(MapUtil.newHashMap(), clientId, clientDetails.getScope(), "app");

		OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

		OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

		OAuth2AccessToken token = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
//		SecurityUser principal = (SecurityUser) authentication.getPrincipal();

		//todo  记录日志 UserService.handlerLoginData(token, principal, request);
//		log.info("用户【 {} 】记录登录日志", principal.getUsername());

		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(Result.success(token)));

	}

	private String[] extractAndDecodeHeader(String header) {

		String base64Token = header.substring(6);
		String token = Base64.decodeStr(base64Token);
		log.info("解密后的token为:{}", token);
		int delim = token.indexOf(':');
		if (delim == -1) {
			throw new BadCredentialsException("Invalid basic authentication token");
		}
		return new String[] { token.substring(0, delim), token.substring(delim + 1) };
	}


}
