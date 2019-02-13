package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.adapters.AdminParse;
import oneapp.workbox.services.adapters.AdminParse.AdminParseResponse;
import oneapp.workbox.services.config.HibernateConfiguration;
import oneapp.workbox.services.dao.WorkFlowModelDao;

@RestController
@RequestMapping(value = "/myController", produces = "application/json")
public class MyController {
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		MyController myController = applicationContext.getBean(MyController.class);
		System.out.println(myController.saveWorkFlowModel("plant_definition_wf"));
		applicationContext.close();
		
	}

	@Autowired
	WorkFlowModelDao wFmodelUpdateDao;
	
	@Autowired
	AdminParse adminParse;

	@RequestMapping(value = "/saveWorkFlowModel", method = RequestMethod.GET)
	public String saveWorkFlowModel(@RequestParam String workFlowDefId) {
		return wFmodelUpdateDao.saveWorkFlowModel(workFlowDefId);
	}
	
	@RequestMapping(value = "/getTaskDetails", method = RequestMethod.GET)
	public AdminParseResponse getDetails() {
		return adminParse.parseNewDetail();
	}

}
