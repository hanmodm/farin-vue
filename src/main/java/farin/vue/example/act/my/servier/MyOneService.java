package farin.vue.example.act.my.servier;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import farin.vue.example.handlr.CommonDao;

@Service
public class MyOneService {

	@Autowired
	private CommonDao dao;
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getTest (final Map<String, Object> parm) {
		return (Map<String, Object>) dao.getSqlSession().selectOne("myOneService.getTest", parm);
	}
}
