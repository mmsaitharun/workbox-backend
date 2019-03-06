package oneapp.workbox.web.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.adapters.AdminParse;
import oneapp.workbox.services.adapters.AdminParseResponseObject;
import oneapp.workbox.services.config.HibernateConfiguration;
import oneapp.workbox.services.dao.EventsUpdateDao;
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
	
	@Autowired
	EventsUpdateDao eventsUpdateDao;
	
	@RequestMapping(value = "/saveWorkFlowModel", method = RequestMethod.GET)
	public String saveWorkFlowModel(@RequestParam String workFlowDefId) {
		return wFmodelUpdateDao.saveWorkFlowModel(workFlowDefId);
	}
	
	@RequestMapping(value = "/getTaskDetails", method = RequestMethod.GET)
	public AdminParseResponseObject getDetails() throws IOException {
		return adminParse.parseAPI();
	}
	
	@RequestMapping(value = "/saveTaskDetails", method = RequestMethod.GET)
	public String saveDetails() throws IOException {

		System.err.println("[parse][parsingStart] : " + System.currentTimeMillis());
		AdminParseResponseObject parseResponse = adminParse.parseAPI();
		System.err.println("[parse][parsingEnd] : " + System.currentTimeMillis());
		System.err.println("[parse][InsertingStart] : " + System.currentTimeMillis());
		eventsUpdateDao.saveOrUpdateProcesses(parseResponse.getProcesses());
		eventsUpdateDao.saveOrUpdateTasks(parseResponse.getTasks());
		eventsUpdateDao.saveOrUpdateOwners(parseResponse.getOwners());
		eventsUpdateDao.saveOrUpdateProcessDetails(parseResponse.getProcessDetails());
		eventsUpdateDao.saveOrUpdatePrjPrcMaps(parseResponse.getPrjPrcMaps());
		System.err.println("[parse][InsertingEnd] : " + System.currentTimeMillis());
		
		return "Success";
	}

}
