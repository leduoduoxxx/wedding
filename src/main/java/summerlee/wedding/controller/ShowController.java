package summerlee.wedding.controller;import java.util.ArrayList;import java.util.List;import java.util.concurrent.ExecutorService;import java.util.concurrent.Executors;import javax.servlet.http.HttpServletRequest;import org.apache.commons.lang3.StringUtils;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.util.CollectionUtils;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.ResponseBody;import com.alibaba.fastjson.JSONObject;import summerlee.wedding.service.SaveService;import summerlee.wedding.websocket.Broadcaster;/** * 基础控制器 *  * @file BaseController * @author libin * @Create 2016-12-26 */@Controllerpublic class ShowController {		private final Logger logger = LoggerFactory.getLogger(this.getClass());	private final ExecutorService executorService = Executors.newFixedThreadPool(			Runtime.getRuntime().availableProcessors() * 2);		@Autowired	private SaveService saveService;	@Value("#{configProperties[forbidden]}")	private String forbidden;	private List<String> forbList;		@RequestMapping(value="/")	public String index(Model model, HttpServletRequest request) {		model.addAttribute("host", request.getHeader("Host"));		model.addAttribute("port", request.getLocalPort());		return "index";	}		@RequestMapping(value="/pop")	public @ResponseBody String danmu(String danmu, String firend, String avatar) {		logger.info("Received danmu : \"{}\" from firend : {}", danmu, firend);		if(StringUtils.isAnyEmpty(danmu, firend)				||isForbidden(danmu)){			return "error";		}		// 弹幕		JSONObject json = new JSONObject();		json.put("firend", firend);		json.put("avatar", avatar);		json.put("msg", danmu);		Broadcaster.addDanmu(json.toJSONString());		// 存储弹幕		executorService.submit(new SaveService.SaveJob(firend, avatar, danmu, saveService));		return "success";	}		@RequestMapping(value="forb")	public @ResponseBody String forbidden(String f) {		if(StringUtils.isNotBlank(f)){			if(!isForbidden(f)){				List<String> newForbList = new ArrayList<>(forbList);				newForbList.add(f);				forbList = newForbList;			}		}		return "success";	}		private boolean isForbidden(String danmu){		if(CollectionUtils.isEmpty(forbList)){			forbList = new ArrayList<>();			boolean frag = false;			for(String f : forbidden.split(",")){				forbList.add(f);				if(StringUtils.containsIgnoreCase(danmu, f)){					frag = true;				}			}			return frag;		}else{			for(String f : forbList){				if(StringUtils.containsIgnoreCase(danmu, f)){					return true;				}			}		}		return false;	}}