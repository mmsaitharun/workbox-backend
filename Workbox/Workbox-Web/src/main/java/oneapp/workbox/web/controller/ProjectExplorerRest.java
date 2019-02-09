package oneapp.workbox.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import oneapp.workbox.services.dto.ProcessDetailResponse;
import oneapp.workbox.services.dto.ProcessDropdownResponse;
import oneapp.workbox.services.dto.ProjectDetailsResponse;
import oneapp.workbox.services.dto.TaskDetailStoryBoard;
import oneapp.workbox.services.service.ProjectExplorerFacadeLocal;

@RestController
@CrossOrigin
@ComponentScan("oneapp.incture.workbox")
@RequestMapping(value = "/processExplorer")
public class ProjectExplorerRest {

	@Autowired
	ProjectExplorerFacadeLocal projectExplorer;

	@RequestMapping(value = "/getProjects", method = RequestMethod.GET)
	public ProjectDetailsResponse getProjectDetails(@RequestParam(name = "projectId", required = false) String projectId,
			@RequestParam(name = "projectCreatedFrom", required = false) String projectCreatedFrom, 
			@RequestParam(name = "projectCreatedTo", required = false) String projectCreatedTo,
			@RequestParam(name = "isRecent", required = false) Boolean isRecent) {
		return projectExplorer.getProjectDetails(projectId, projectCreatedFrom, projectCreatedTo,isRecent);
	}

	@RequestMapping(value = "/getProcesses", method = RequestMethod.GET)
	public ProcessDetailResponse getProcess(@RequestParam(name = "projectId") String projectId) {
		return projectExplorer.getProcessDetails(projectId);
	}

	@RequestMapping(value = "/getTaskDetailStory", method = RequestMethod.GET)
	public TaskDetailStoryBoard getTaskDetailStory(@RequestParam(name = "processId") String processId) {
		return projectExplorer.getProcessFlowDetails(processId);
	}

	@RequestMapping(value = "/getProcessDropdown", method = RequestMethod.GET)
	public ProcessDropdownResponse getProcessDropdown(@RequestParam(name = "projectId") String projectId) {
		return projectExplorer.getProcessDropdown(projectId);
	}

}
