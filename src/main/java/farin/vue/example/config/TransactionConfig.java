package farin.vue.example.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import farin.vue.example.handlr.CommonDao;
import farin.vue.example.handlr.RefreshableSqlSessionFactoryBean;

@Configuration
@EnableTransactionManagement(order = 1, proxyTargetClass = true)
public class TransactionConfig {
	
	@Value("${fvue.maxResultRows}")
	private String maxResultRows;
	
	private ApplicationContext applicationContext;

	@Autowired
	public TransactionConfig(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Bean
    @ConfigurationProperties(prefix = "spring.datasource-oracle")
	public HikariConfig hikariConfig() {
		return new HikariConfig();
	}
    
    @Bean
    //@Qualifier("oracleDataSource")
    public DataSource oracleDataSource() {
		return new HikariDataSource(hikariConfig());
    }
	
//	@Bean
//    @Primary
//    public SqlSessionFactory oracleSqlSessionFactory() throws Exception {
//    	SqlSessionFactoryBean factoryBean = null;
//    	factoryBean = new SqlSessionFactoryBean();
//        factoryBean.setDataSource(oracleDataSource());
//        factoryBean.setVfs(SpringBootVFS.class);
//        factoryBean.setConfigLocation(applicationContext.getResource("classpath:/mybatis/mybatis-config.xml"));
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        Resource[] resource = resolver.getResources("classpath*:/mybatis/**/*.sqlx");
//        factoryBean.setMapperLocations(resource);
// 
//        return factoryBean.getObject();
//    }
	
	@Bean
	@Primary
	public SqlSessionFactory oracleSqlSessionFactory() throws Exception {
	        
		RefreshableSqlSessionFactoryBean factoryBean = new RefreshableSqlSessionFactoryBean();
		factoryBean.setDataSource(oracleDataSource());
		factoryBean.setVfs(SpringBootVFS.class);
		factoryBean.setConfigLocation(applicationContext.getResource("classpath:/configs/mybatis-config.xml"));
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resource = resolver.getResources("classpath*:/mybatis/**/*.xml");
		((RefreshableSqlSessionFactoryBean) factoryBean).setInterval(500);
		factoryBean.setMapperLocations(resource);
		factoryBean.refresh();
		SqlSessionFactory rtn = factoryBean.getObject();
		return rtn;
	}
	
	@Bean
	@Primary
	public CommonDao commonDao() throws Exception {
		CommonDao commonDao = new CommonDao();
		commonDao.setSqlSessionFactory(oracleSqlSessionFactory());
		commonDao.setMaxResultRows(Integer.parseInt(maxResultRows));
		return commonDao;
	}

	@Bean
	@Primary
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(oracleDataSource());
    }
	
	@Bean
	public TransactionInterceptor txAdvice() {
		TransactionInterceptor txAdvice = new TransactionInterceptor();
		Properties txAttributes = new Properties();
		List<RollbackRuleAttribute> rollbackRules = new ArrayList<RollbackRuleAttribute>();
		rollbackRules.add(new RollbackRuleAttribute(Exception.class));
		
		DefaultTransactionAttribute readOnlyAttribute = new DefaultTransactionAttribute(
				TransactionDefinition.PROPAGATION_SUPPORTS);
		readOnlyAttribute.setReadOnly(true);

		RuleBasedTransactionAttribute writeAttribute = new RuleBasedTransactionAttribute(
				TransactionDefinition.PROPAGATION_REQUIRED, rollbackRules);
		
		RuleBasedTransactionAttribute writeNewAttribute = new RuleBasedTransactionAttribute(
				TransactionDefinition.PROPAGATION_REQUIRES_NEW, rollbackRules);
		
		String readOnlyTransactionAttributesDefinition = readOnlyAttribute.toString();
		String writeTransactionAttributesDefinition = writeAttribute.toString();
		String writeNewTransactionAttributesDefinition = writeNewAttribute.toString();

		txAttributes.setProperty("create*", writeTransactionAttributesDefinition);
		txAttributes.setProperty("insert*", writeTransactionAttributesDefinition);
		txAttributes.setProperty("update*", writeTransactionAttributesDefinition);
		txAttributes.setProperty("delete*", writeTransactionAttributesDefinition);
		txAttributes.setProperty("save*", writeTransactionAttributesDefinition);
		txAttributes.setProperty("cancle*", writeTransactionAttributesDefinition);
		txAttributes.setProperty("create*NewTx", writeNewTransactionAttributesDefinition);
		txAttributes.setProperty("insert*NewTx", writeNewTransactionAttributesDefinition);
		txAttributes.setProperty("update*NewTx", writeNewTransactionAttributesDefinition);
		txAttributes.setProperty("delete*NewTx", writeNewTransactionAttributesDefinition);
		txAttributes.setProperty("send*", writeNewTransactionAttributesDefinition);
		txAttributes.setProperty("requestWebService", writeNewTransactionAttributesDefinition);
		txAttributes.setProperty("*", readOnlyTransactionAttributesDefinition);

		txAdvice.setTransactionAttributes(txAttributes);
		txAdvice.setTransactionManager(transactionManager());
		return txAdvice;

	}
	
	@Bean
	public Advisor txAdviceAdvisor() {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression("execution(* *..service.*Service.*(..))||execution(* *..module.*Module.*(..))");
		return new DefaultPointcutAdvisor(pointcut, txAdvice());
	}
}
