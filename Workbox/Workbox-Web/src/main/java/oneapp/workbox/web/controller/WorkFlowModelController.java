package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.adapters.WorkFlowModelMaster;
import oneapp.workbox.services.config.HibernateConfiguration;
import oneapp.workbox.services.dao.WorkFlowModelDao;

@RestController
@RequestMapping(value = "/wfModel", produces = "application/json")
public class WorkFlowModelController {
	
	@Autowired
	WorkFlowModelDao wFmodelDao;

	@RequestMapping(value = "/saveWorkFlowModel", method = RequestMethod.POST)
	public String saveWorkFlowModel(@RequestParam String workFlowDefId) {
		return wFmodelDao.saveWorkFlowModel(workFlowDefId);
	}
	
	@RequestMapping(value = "/getWorkFlowModel", method = RequestMethod.GET)
	public WorkFlowModelMaster getWorkFlowModel(@RequestParam String workFlowDefId) {
		return wFmodelDao.getWorkFlowModel(workFlowDefId);
	}
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		WorkFlowModelController myController = applicationContext.getBean(WorkFlowModelController.class);
		System.out.println(myController.getWorkFlowModel("detailed_scoping_wf").getNetworkSequence());
		applicationContext.close();
	}

}
