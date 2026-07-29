package farin.vue.example.act.my.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import farin.vue.example.act.my.service.MyOneService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value="/svcApi")
public class MyOneController {

	@Autowired
	private MyOneService myOneSvc;
	
	@GetMapping("/test")
	@ResponseBody
	public Map<String, Object> getTest(@RequestParam String id, HttpServletRequest req, HttpServletResponse res) {
		return myOneSvc.getTest(null);
	}
}
