package farin.vue.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener{

	private static final Logger LOG = LoggerFactory.getLogger(SessionListener.class);
	
	@Value("${server.servlet.session.timeout}")
	private int sessionTime;
	
	public void sessionCreated(HttpSessionEvent se) {
		LOG.debug("sessionCreated ::: "+ se.toString());
		se.getSession().setMaxInactiveInterval(sessionTime);
	}
}
