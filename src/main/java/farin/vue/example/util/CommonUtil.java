package farin.vue.example.util;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

public class CommonUtil {
	/**
	 * RESTful로 서비스 요청
	 *
	 * @param url
	 * @param inParams
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> loadPlmRemoteService(final String url, final Map<String, Object> inParams) {
		Map<String, Object> outParams = new HashMap<String, Object>();

		final RestTemplate restTemplate = (RestTemplate) ContextUtil.getBean("restTemplate");
		final String basePath = ContextUtil.getProperty("fo.static.basePath");

		if (restTemplate == null) {
			outParams.put("ERR_MSG", "No RestTemplate instance!! ");
			return outParams;
		}

		try {
			String _url = "";
			if (!url.startsWith("http")) {
				_url = basePath.concat((basePath.endsWith("/") ? "" : "/").concat(url).replace("//", "/"));
			} else {
				_url = url;
			}
			// RESTful 요청
			// content-type: application/json;charset=utf-8
			HttpHeaders headers = getCurrRequest();
			ResponseEntity<Map> responseEntity = restTemplate.exchange(
					encodedUrl(_url),
					HttpMethod.POST,
					new HttpEntity<Map>(inParams, headers),
					Map.class);
			outParams = responseEntity.getBody();
		} catch(Exception e) {
			outParams.put("ERR_MSG", url);
			return outParams;
		}

		return outParams;
	}

	/**
	 * RESTful 요청을 위한 URL 정보 인코딩
	 *
	 * @param url
	 * @return
	 */
	private static String encodedUrl (final String url) {
		return UriComponentsBuilder.fromHttpUrl(url).encode(StandardCharsets.UTF_8)
				.build().toUriString();
	}

	/**
	 * RESTful 요청을 위한 Request Header 생성
	 *
	 * @return
	 */
	private static HttpHeaders getCurrRequest() {
		HttpHeaders httpHeader = new HttpHeaders();
		HttpServletRequest curr = ContextUtil.getRequest();

		if (curr == null) {
			return null;
		}
		httpHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
		httpHeader.setContentLanguage(curr.getLocale());
		httpHeader.add(HttpHeaders.COOKIE, curr.getHeader(HttpHeaders.COOKIE));
		httpHeader.add(HttpHeaders.HOST, curr.getHeader(HttpHeaders.HOST));

		return httpHeader;
	}
}
