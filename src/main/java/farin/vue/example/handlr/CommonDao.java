package farin.vue.example.handlr;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CommonDao extends SqlSessionDaoSupport {

	public static final String TRANSACTION_MANAGER = "transactionManager";
	private static final Logger LOG = LoggerFactory.getLogger(CommonDao.class);
	private int maxResultRows = 100000;
	
	public void setMaxResultRows(int maxResultRows) {
		this.maxResultRows = maxResultRows;
	}

	@Autowired(required = false)
	@Override
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}
}
