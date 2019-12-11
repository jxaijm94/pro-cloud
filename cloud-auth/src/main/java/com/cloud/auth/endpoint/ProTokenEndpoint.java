package com.cloud.auth.endpoint;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.common.cache.constants.CacheScope;
import com.cloud.common.cache.redis.RedisDao;
import com.cloud.common.cache.util.CacheUtil;
import com.cloud.common.oauth.security.SecurityUser;
import com.cloud.common.util.base.Result;
import com.cloud.common.util.constant.PaginationConstants;
import com.cloud.common.util.enums.ResultEnum;
import com.cloud.common.util.var.RedisKeys;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ConvertingCursor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Aijm
 * @date 2019/09/06
 * 删除token端点
 */
@RestController
@AllArgsConstructor
@RequestMapping("/token")
public class ProTokenEndpoint {
	private static final String PRO_OAUTH_ACCESS = RedisKeys.REDIS_TOKEN_KEY + "auth_to_access:*";
	private final ClientDetailsService clientDetailsService;
	private final RedisTemplate redisTemplate;
	private final TokenStore tokenStore;

	/**
	 * 认证页面
	 *
	 * @param modelAndView
	 * @return ModelAndView
	 */
	@GetMapping("/login")
	public ModelAndView require(ModelAndView modelAndView) {
		modelAndView.setViewName("ftl/login");
		return modelAndView;
	}

	/**
	 * 确认授权页面
	 *
	 * @param request
	 * @param session
	 * @param modelAndView
	 * @return
	 */
	@GetMapping("/confirm_access")
	public ModelAndView confirm(HttpServletRequest request, HttpSession session, ModelAndView modelAndView) {
//		Map<String, Object> scopeList = (Map<String, Object>) request.getAttribute("scopes");
//		modelAndView.addObject("scopeList", scopeList.keySet());
//
//		Object auth = session.getAttribute("authorizationRequest");
//		if (auth != null) {
//			AuthorizationRequest authorizationRequest = (AuthorizationRequest) auth;
//			ClientDetails clientDetails = clientDetailsService.loadClientByClientId(authorizationRequest.getClientId());
//			modelAndView.addObject("app", clientDetails.getAdditionalInformation());
////			modelAndView.addObject("user", SecurityUtils.getUser());
//		}
		modelAndView.setViewName("ftl/confirm");
		return modelAndView;
	}

	/**
	 * 退出token
	 *
	 * @param authHeader Authorization
	 */
	@DeleteMapping("/logout")
	public Result logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
		if (StrUtil.isBlank(authHeader)) {
			return Result.error(ResultEnum.LOGOUT_CODE);
		}

		String tokenValue = authHeader.replace(OAuth2AccessToken.BEARER_TYPE, StrUtil.EMPTY).trim();
		OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
		if (accessToken == null || StrUtil.isBlank(accessToken.getValue())) {
			return Result.error(ResultEnum.TOKEN_ERROR);
		}

		OAuth2Authentication auth = tokenStore.readAuthentication(accessToken);

		SecurityUser user = (SecurityUser)auth.getPrincipal();
		// 清空菜单信息
		CacheUtil.remove(CacheScope.USER_MENU.getCacheName(), user.getUserId().toString());
		// 清空角色信息
		CacheUtil.remove(CacheScope.USER_ROLE.getCacheName(), user.getUserId().toString());
		// 清空用户信息
		CacheUtil.remove(CacheScope.USER_USER.getCacheName(), user.getUserId().toString());
		// 清空access token
		tokenStore.removeAccessToken(accessToken);
		// 清空 refresh token
		OAuth2RefreshToken refreshToken = accessToken.getRefreshToken();
		tokenStore.removeRefreshToken(refreshToken);
		return Result.success("");
	}

	/**
	 * 令牌管理调用
	 *
	 * @param token token
	 * @return
	 */
//	@Inner
//	@DeleteMapping("/{token}")
//	public Result delToken(@PathVariable("token") String token) {
//		OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
//		tokenStore.removeAccessToken(oAuth2AccessToken);
//		return Result.success("");
//	}


	/**
	 * 查询token
	 *
	 * @param params 分页参数
	 * @return
	 */
//	@Inner
	@PostMapping("/page")
	public Result tokenList(@RequestBody Map<String, Object> params) {
		//根据分页参数获取对应数据

		List<String> pages = findKeysForPage(PRO_OAUTH_ACCESS, MapUtil.getInt(params, PaginationConstants.CURRENT)
				, MapUtil.getInt(params, PaginationConstants.SIZE));

		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
		Page result = new Page(MapUtil.getInt(params, PaginationConstants.CURRENT), MapUtil.getInt(params, PaginationConstants.SIZE));
		result.setRecords(redisTemplate.opsForValue().multiGet(pages));
		result.setTotal(Long.valueOf(redisTemplate.keys(PRO_OAUTH_ACCESS).size()));
		return Result.success(result);
	}


	private List<String> findKeysForPage(String patternKey, int pageNum, int pageSize) {
		ScanOptions options = ScanOptions.scanOptions().match(patternKey).build();
		RedisSerializer<String> redisSerializer = (RedisSerializer<String>) redisTemplate.getKeySerializer();
		Cursor cursor = (Cursor) redisTemplate.executeWithStickyConnection(redisConnection -> new ConvertingCursor<>(redisConnection.scan(options), redisSerializer::deserialize));
		List<String> result = new ArrayList<>();
		int tmpIndex = 0;
		int startIndex = (pageNum - 1) * pageSize;
		int end = pageNum * pageSize;

		assert cursor != null;
		while (cursor.hasNext()) {
			if (tmpIndex >= startIndex && tmpIndex < end) {
				result.add(cursor.next().toString());
				tmpIndex++;
				continue;
			}
			if (tmpIndex >= end) {
				break;
			}
			tmpIndex++;
			cursor.next();
		}
		return result;
	}
}
