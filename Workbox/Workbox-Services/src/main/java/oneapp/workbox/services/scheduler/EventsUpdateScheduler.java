package oneapp.workbox.services.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import oneapp.workbox.services.adapters.AdminParse;
import oneapp.workbox.services.adapters.AdminParseResponse;
import oneapp.workbox.services.adapters.AdminParseResponseObject;
import oneapp.workbox.services.config.HibernateConfiguration;
import oneapp.workbox.services.dao.EventsUpdateDao;
import oneapp.workbox.services.util.ServicesUtil;

@Component("eventsScheduler")
public class EventsUpdateScheduler {

	@Autowired
	AdminParse parse;

//	@Autowired
//	TaskEventsDao taskEvents;
//
//	@Autowired
//	ProcessEventsDao processEvents;
//
//	@Autowired
//	TaskOwnersDao ownerEvents;
//	
//	@Autowired
//	ProjectProcessDao prjPrcMapping;
	
	@Autowired
	EventsUpdateDao eventsUpdateDao;

//	@Scheduled(fixedDelay = 6000)
//	public void updateEvents() {
//		System.err.println("[PMC]EventsUpdateScheduler started : " + new Date());
//		AdminParseResponse parseResponse = parse.parseDetail();
//		processEvents.saveOrUpdateProcesses(parseResponse.getProcesses());
//		List<ProjectProcessMapping> prjPrcMaps = parseResponse.getPrjPrcMaps();
//		prjPrcMapping.saveOrUpdatePrjPrcMaps(prjPrcMaps);
//		prjPrcMapping.saveOrUpdateProcessDetails(parseResponse.getProcessDetails());
//		taskEvents.saveOrUpdateTasks(parseResponse.getTasks());
//		List<TaskOwnersDto> owners = parseResponse.getOwners();
//		ownerEvents.saveOrUpdateOwners(owners);
//		System.err.println("[PMC]EventsUpdateScheduler ended : " + new Date());
//	}
	
	public void updateCustomAttributes() {
		AdminParseResponse parseResponse = parse.parseDetail();
		parse.updateCustomAttributes(parseResponse.getTasks());
	}
	
//	@Scheduled(fixedDelay = 6000)
//	public void updateCompleteEvents() {
//		System.err.println("[PMC]EventsUpdateScheduler Completed started : " + new Date());
//		AdminParseResponse parseResponse = parse.parseCompleteDetail();
//		processEvents.saveOrUpdateProcesses(parseResponse.getProcesses());
//		taskEvents.saveOrUpdateTasks(parseResponse.getTasks());
//		List<TaskOwnersDto> owners = parseResponse.getOwners();
//		ownerEvents.saveOrUpdateOwners(owners);
//		System.err.println("[PMC]EventsUpdateScheduler Completed ended : " + new Date());
//	}
	
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		EventsUpdateScheduler eventsUpdateScheduler = applicationContext.getBean(EventsUpdateScheduler.class);
		eventsUpdateScheduler.updateWorkFlowEvents();
		applicationContext.close();
		
	}
	
	public void updateWorkFlowEvents() {
		System.err.println("[parse][SchedulerStart] : " + System.currentTimeMillis());
		System.err.println("[parse][parsingStart] : " + System.currentTimeMillis());
		final AdminParseResponseObject parseResponse = parse.parseAPI();
		System.err.println("[parse][parsingEnd] : " + System.currentTimeMillis());
		if(!ServicesUtil.isEmpty(parseResponse)) {
			System.err.println("[parse][InsertingStart] : " + System.currentTimeMillis());
			
			Thread processSaveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					eventsUpdateDao.saveOrUpdateProcesses(parseResponse.getProcesses());
				}
			});
			
			Thread taskSaveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					eventsUpdateDao.saveOrUpdateTasks(parseResponse.getTasks());
				}
			});
			
			Thread ownerSaveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					eventsUpdateDao.saveOrUpdateOwners(parseResponse.getOwners());
				}
			});
			
			Thread processDetailSaveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					eventsUpdateDao.saveOrUpdateProcessDetails(parseResponse.getProcessDetails());
				}
			});
			
			Thread prjMapsSaveThread = new Thread(new Runnable() {
				@Override
				public void run() {
					eventsUpdateDao.saveOrUpdatePrjPrcMaps(parseResponse.getPrjPrcMaps());
				}
			});
			
			processSaveThread.start();
			taskSaveThread.start();
			ownerSaveThread.start();
			processDetailSaveThread.start();
			prjMapsSaveThread.start();
			
			System.err.println("[parse][InsertingEnd] : " + System.currentTimeMillis());
		}
		System.err.println("[parse][SchedulerEnd] : " + System.currentTimeMillis());
	}
	
}
