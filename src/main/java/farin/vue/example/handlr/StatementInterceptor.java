package farin.vue.example.handlr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import farin.vue.example.util.ReflectionUtil;

@Intercepts({@Signature(type = StatementHandler.class, method = "batch", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
	@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})})
public class StatementInterceptor implements Interceptor{

	private static final Logger LOG = LoggerFactory.getLogger(StatementInterceptor.class);
	private static final String SQL_SPLITTER = "\n------------------------------------------------------------------------";
	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		String sql = null;

		/**
		 * 로그 레벨이 Debug 로 켜져있을 경우에만 로그를 남기도록 한다. 
		 * 운영시에는 평상시에는 로그를 꺼두고 있다가 필요할 경우에는 서비스 리스트에 
		 * 등록하여 로그 남기도록 한다.
		 */
		if(LOG.isDebugEnabled()) {
			try {
				sql = getBoundSql(invocation);
				LOG.debug(sql);
			} catch(Exception e) {
				//오류가 발생하여도 본 거래에는 영향을 주지 않도록 한다.
				e.printStackTrace();
				LOG.error("Exception", e);
			}
		}

		return invocation.proceed();
	}

	/**
	 * 파라미터가 매핑된 SQL문 리턴
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	private String getBoundSql(Invocation invocation) {

		String result = null;
		try {
			StatementHandler handler = (StatementHandler)invocation.getTarget();
			BoundSql boundSql = handler.getBoundSql();

			String sql = boundSql.getSql();

			Object obj = handler.getParameterHandler().getParameterObject();
			List<ParameterMapping> parameterList = boundSql.getParameterMappings();

			if(obj instanceof Map) {
				Map<String, String> map = (Map<String, String>)boundSql.getParameterObject();
				StringBuilder sb = new StringBuilder(SQL_SPLITTER.concat("\n"));

				/**
				 * sql 문자열에서 ? 를 split 한 후에 클래스 타입에 맞게 
				 * 싱글쿼테이션을 붙여서(숫자의 경우는 붙이지 않음) 문자열을 조합한다.
				 */
				String[] splitedSql = sql.split("\\?");
				if(parameterList.size() > 0) {
					for(int i = 0; i < parameterList.size(); i++) {
						ParameterMapping parameterMapping = parameterList.get(i);

						// foreach 구문으로 바인딩하는 파라미터는 
						// __frch_xxx_0, __frch_xxx_1 과 같은 프로퍼티 명을 가진다
						if(parameterMapping.getProperty().indexOf("__frch_") > -1) {
							sb.append(splitedSql[i]).append(
									getClassType(boundSql.getAdditionalParameter(parameterMapping.getProperty())));
						} else {
							sb.append(splitedSql[i]).append(getClassType(map, parameterMapping.getProperty()));
						}
					}

					if(splitedSql.length - parameterList.size() > 0) {
						sb.append(splitedSql[splitedSql.length - 1]);
					}
				} else {
					sb.append(sql);
				}
				//return sb.toString();
				result = sb.toString();
			} else {
				//return sql;
				result = SQL_SPLITTER.concat("\n\t\t").concat(sql);
			}
			result = result.concat(SQL_SPLITTER);
		} catch(Exception e) {
			if(LOG.isErrorEnabled()) {
				LOG.error("Exception", e);
			}
		}

		return result;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		//To change body of implemented methods use File | Settings | File Templates.
	}

	/**
	 * Map에서 key로 꺼낸 value 오브젝트의 Class Type 에 따라 쿼테이션이 포함된 데이터 문자열 리턴 
	 * @param param
	 * @param key
	 * @return
	 */
	private String getClassType(Map param, String key) {
		return getClassType(getValue(param, key));
	}

	/**
	 * 매개변수로 받은 오브젝트의 Class Type 에 따라 쿼테이션이 포함된 데이터 문자열 리턴 
	 * @param obj
	 * @return
	 */
	private String getClassType(Object obj) {
		StringBuilder sb = new StringBuilder();
		if(obj == null) {
			return "null";
		}

		Class classType = obj.getClass();

		/*
		 * 문자일 경우에는 싱글 쿼테이션을 붙이도록 한다. - else문 로직과 동일하여 주석처리
		 */
		/*if(classType.equals(String.class) || classType.equals(Clob.class) || classType.equals(java.sql.Date.class)
				|| classType.equals(java.sql.Timestamp.class) || classType.equals(java.util.Date.class)
				|| classType.equals(java.sql.Blob.class)) {
			sb.append("'").append(obj).append("'");
		} */
		//숫자형 (실수형)
		if(classType.equals(BigDecimal.class) || classType.equals(Float.class) || classType.equals(Double.class)
				|| classType.equals(Integer.class) || classType.equals(Long.class)) {
			sb.append(obj);

			//날짜형, 문자형
		} else {
			sb.append("'").append(obj).append("'");
		}

		return sb.toString();
	}

	/**
	 * key에 해당하는 value를 반환
	 * @param param
	 * @param key
	 * @return
	 */
	private Object getValue(Map param, String key) {
		if(key != null && !"".equals(key)) {
			/**
			 * key 
			 */
			Object obj = param;
			String[] keylist = key.split("\\.");

			if(keylist.length == 0) {
				obj = param.get(key);
			} else {
				for(int i = 0; i < keylist.length; i++) {
					obj = getMappingObject(obj, keylist[i]);
					if(obj == null) {
						// 앞 index에서 수행한 내용 때문에 obj가 null이 되었다면?
						break;
					}
				}
			}

			return obj;

		} else {
			return null;
		}
	}

	/**
	 * Object 타입이 Map일 때와 VO일 때를 구분하여 key 에 해당하는 값을 리턴한다.
	 * @param obj
	 * @param key
	 * @return
	 */
	private Object getMappingObject(Object obj, String key) {
		if(obj instanceof Map) {
			//Map 인 경우
			return ((Map)obj).get(key);
		} else {
			/**
			 * get 메소드를 통하여 변수값을 획득함
			 */
			Object result = null;
			String methodName = "get" + StringUtils.capitalize(key);
			Method method = ReflectionUtil.getMethod(obj.getClass(), methodName, new Object[]{});
			try {
				result = method.invoke(obj, new Object[]{});
			} catch(IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("IllegalAccessException", e);
			} catch(IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("IllegalArgumentException", e);
			} catch(InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOG.error("InvocationTargetException", e);
			}
			return result;
		}
	}
}
