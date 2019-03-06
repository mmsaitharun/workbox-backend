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
	public WorkFlowModelMaster getWorkFlowModel(@RequestParam String workFlowDefId, @RequestParam(required=false) String processId) {
		return wFmodelDao.getWorkFlowModel(workFlowDefId, processId);
	}
	
	@RequestMapping(value = "/saveWorkFlowModelRanking", method = RequestMethod.GET)
	public String saveWorkFlowModelRanking() {
		wFmodelDao.insertProjectRankings();
		return "Success";
	}
	
	@RequestMapping(value = "/getWorkFlowMasterModel", method = RequestMethod.GET)
	public WorkFlowModelMaster getMasterWorkFlowModel(@RequestParam String projectId) {
		return wFmodelDao.getMasterWorkFlowModel(projectId);
	}
	
	public static void main(String[] args) {
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		WorkFlowModelController myController = applicationContext.getBean(WorkFlowModelController.class);
		System.out.println(myController.getWorkFlowModel("material_definition_wf", "196dabbc-2e06-11e9-8db4-00163e836154").getActivities());
		applicationContext.close();
	}

}
