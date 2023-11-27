package farin.vue.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import farin.vue.example.handlr.ApplicationContextProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ContextUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(ContextUtil.class);
	
	
	/**
	 * 빈을 직접 얻습니다.
	 *
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
		
		
		return applicationContext.getBean(beanName);
	}
	
	/**
	 * property 가져오기.
	 *
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(String propertyName) {
		return getProperty(propertyName, null);
	}
	
	/**
	 * property 가져오기 (with defaultValue).
	 *
	 * @param propertyName
	 * @param defaultValue
	 * @return
	 */
	public static String getProperty(String propertyName, String defaultValue) {
		String value = defaultValue;
		
		try {
			ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
			if ( applicationContext == null || 
				applicationContext.getEnvironment() == null || 
				ObjectUtils.isEmpty(applicationContext.getEnvironment().getProperty(propertyName)) ) {
				LOG.warn(propertyName + " properties was not loaded.");
			} else {
				value = applicationContext.getEnvironment().getProperty(propertyName);
			}
		} catch (Exception e) {
			LOG.error(propertyName + " properties was not loaded :: " + e.getMessage(), e);
		} 
		return value;
	}

	/**
	 * HttpServletReqeust 객체를 직접 얻습니다.
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getRequest();
	}

	/**
	 * HttpServletResponse 객체를 직접 얻습니다.
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getResponse();
	}

	/**
	 * HttpSession 객체를 직접 얻습니다.
	 *
	 * @param gen 새 세션 생성 여부
	 * @return
	 */
	public static HttpSession getSession(boolean gen) {
		return ContextUtil.getRequest().getSession(gen);
	}

	/**
	 * REQUEST 영역에서 가져오기
	 *
	 * @param key
	 * @return
	 */
	public static Object getAttrFromRequest(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getAttribute(key, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * REQUEST 영역에 객체 저장
	 *
	 * @param key
	 * @param obj
	 */
	public static void setAttrToRequest(String key, Object obj) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(key, obj, RequestAttributes.SCOPE_REQUEST);
	}

	/**
	 * SESSION 영역에서 가져오기
	 *
	 * @param key
	 * @return
	 */
	public static Object getAttrFromSession(String key) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		return attr.getAttribute(key, RequestAttributes.SCOPE_SESSION);
	}

	/**
	 * Session 영역에 객체 저장
	 *
	 * @param key
	 * @param obj
	 */
	public static void setAttrToSession(String key, Object obj) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
		attr.setAttribute(key, obj, RequestAttributes.SCOPE_SESSION);
	}
}

