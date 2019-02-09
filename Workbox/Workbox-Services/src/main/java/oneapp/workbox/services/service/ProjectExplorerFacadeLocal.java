package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.ProcessDetailResponse;
import oneapp.workbox.services.dto.ProcessDropdownResponse;
import oneapp.workbox.services.dto.ProjectDetailsResponse;
import oneapp.workbox.services.dto.TaskDetailStoryBoard;

public interface ProjectExplorerFacadeLocal {

	ProjectDetailsResponse getProjectDetails(String projectId, String projectCreatedFrom, String projectCreatedTo,
			Boolean isRecent);

	ProcessDetailResponse getProcessDetails(String projectId);

	TaskDetailStoryBoard getProcessFlowDetails(String processId);

	ProcessDropdownResponse getProcessDropdown(String projectId);

}
