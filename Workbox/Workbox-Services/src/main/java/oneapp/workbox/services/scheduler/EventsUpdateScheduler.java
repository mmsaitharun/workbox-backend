package oneapp.workbox.services.scheduler;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oneapp.workbox.services.adapters.AdminParse;
import oneapp.workbox.services.adapters.AdminParse.AdminParseResponse;
import oneapp.workbox.services.dao.ProcessEventsDao;
import oneapp.workbox.services.dao.ProjectProcessDao;
import oneapp.workbox.services.dao.TaskEventsDao;
import oneapp.workbox.services.dao.TaskOwnersDao;
import oneapp.workbox.services.dto.TaskOwnersDto;
import oneapp.workbox.services.entity.ProjectProcessMapping;

@Component("eventsScheduler")
public class EventsUpdateScheduler {

	@Autowired
	AdminParse parse;

	@Autowired
	TaskEventsDao taskEvents;

	@Autowired
	ProcessEventsDao processEvents;

	@Autowired
	TaskOwnersDao ownerEvents;
	
	@Autowired
	ProjectProcessDao prjPrcMapping;

//	@Scheduled(fixedDelay = 6000)
	public void updateEvents() {
		System.err.println("[PMC]EventsUpdateScheduler started : " + new Date());
		AdminParseResponse parseResponse = parse.parseDetail();
		processEvents.saveOrUpdateProcesses(parseResponse.getProcesses());
		List<ProjectProcessMapping> prjPrcMaps = parseResponse.getPrjPrcMaps();
		prjPrcMapping.saveOrUpdatePrjPrcMaps(prjPrcMaps);
		prjPrcMapping.saveOrUpdateProcessDetails(parseResponse.getProcessDetails());
		taskEvents.saveOrUpdateTasks(parseResponse.getTasks());
		List<TaskOwnersDto> owners = parseResponse.getOwners();
		ownerEvents.saveOrUpdateOwners(owners);
		System.err.println("[PMC]EventsUpdateScheduler ended : " + new Date());
	}
	
	public void updateCustomAttributes() {
		AdminParseResponse parseResponse = parse.parseDetail();
		parse.updateCustomAttributes(parseResponse.getTasks());
	}
	
//	@Scheduled(fixedDelay = 6000)
	public void updateCompleteEvents() {
		System.err.println("[PMC]EventsUpdateScheduler Completed started : " + new Date());
		AdminParseResponse parseResponse = parse.parseCompleteDetail();
		processEvents.saveOrUpdateProcesses(parseResponse.getProcesses());
		taskEvents.saveOrUpdateTasks(parseResponse.getTasks());
		List<TaskOwnersDto> owners = parseResponse.getOwners();
		ownerEvents.saveOrUpdateOwners(owners);
		System.err.println("[PMC]EventsUpdateScheduler Completed ended : " + new Date());
	}
	
}
