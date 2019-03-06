package oneapp.workbox.services.dao;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.entity.ProcessDetail;
import oneapp.workbox.services.entity.ProcessEventsDo;
import oneapp.workbox.services.entity.ProjectProcessMapping;
import oneapp.workbox.services.entity.TaskEventsDo;
import oneapp.workbox.services.entity.TaskOwnersDo;
import oneapp.workbox.services.util.ServicesUtil;

@Repository
@Transactional
public class EventsUpdateDao {

	private static final int _HIBERNATE_BATCH_SIZE = 300;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	public void saveOrUpdateProcesses(List<ProcessEventsDo> processes) {
		Session session = null;
		if(!ServicesUtil.isEmpty(processes) && processes.size() > 0) {
			session = this.getSession();
			for(int i = 0; i < processes.size(); i++) {
				session.saveOrUpdate(processes.get(i));
				if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
					session.flush();
					session.clear();
				}
			}
			if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}
	
	public void saveOrUpdateTasks(List<TaskEventsDo> tasks) {
		Session session = null;
		if(!ServicesUtil.isEmpty(tasks) && tasks.size() > 0) {
			session = this.getSession();
			for(int i = 0; i < tasks.size(); i++) {
				TaskEventsDo currentTask = tasks.get(i);
				currentTask.setUpdatedAt(new Date());
				session.saveOrUpdate(currentTask);
				if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
					session.flush();
					session.clear();
				}
			}
			if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}
	
	public void saveOrUpdateOwners(List<TaskOwnersDo> owners) {
		Session session = null;
		if(!ServicesUtil.isEmpty(owners) && owners.size() > 0) {
			session = this.getSession();
			for(int i = 0; i < owners.size(); i++) {
				TaskOwnersDo ownersDo = owners.get(i);
				try {
					if(!ServicesUtil.isEmpty(ownersDo)){
						session.saveOrUpdate(ownersDo);
						if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
							session.flush();
							session.clear();
						}
					} else {
						System.err.println("Can not insert as primary key columns are NULL : "+owners.get(i));
					}
				} catch (Exception ex) {
					System.err.println("Can not insert owner : "+owners.get(i) +" With exception : "+ex.getMessage());
				}
			}
			if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}
	
	public void saveOrUpdateProcessDetails(List<ProcessDetail> processDetails) {
		Session session = null;
		if (!ServicesUtil.isEmpty(processDetails) && processDetails.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < processDetails.size(); i++) {
				try {
					ProcessDetail processDetail = processDetails.get(i);
					if(!ServicesUtil.isEmpty(processDetail)) {
						if(!ServicesUtil.isEmpty(processDetail) && !ServicesUtil.isEmpty(processDetail.getProjectId()))
							session.saveOrUpdate(processDetail);
						if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
							session.flush();
							session.clear();
						}
					} else {
						System.err.println("Can not insert as primary key columns are NULL : "+processDetails.get(i));
					}
				} catch (Exception ex) {
					System.err.println("Can not insert processDetails : "+processDetails.get(i) +" With exception : "+ex.getMessage());
				}
			}
			if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}
	
	public void saveOrUpdatePrjPrcMaps(List<ProjectProcessMapping> prjPrcMaps) {
		Session session = null;
		if (!ServicesUtil.isEmpty(prjPrcMaps) && prjPrcMaps.size() > 0) {
			session = this.getSession();
			for (int i = 0; i < prjPrcMaps.size(); i++) {
				try {
					ProjectProcessMapping projectProcessMapping = prjPrcMaps.get(i);
					if(!ServicesUtil.isEmpty(projectProcessMapping)) {
						session.saveOrUpdate(projectProcessMapping);
						if(i % _HIBERNATE_BATCH_SIZE == 0 && i > 0) {
							session.flush();
							session.clear();
						}
					} else {
						System.err.println("Can not insert as primary key columns are NULL : "+prjPrcMaps.get(i));
					}
				} catch (Exception ex) {
					System.err.println("Can not insert prjPrcMaps : "+prjPrcMaps.get(i) +" With exception : "+ex.getMessage());
				}
			}
			if(!session.getTransaction().wasCommitted()) {
				session.flush();
			}
		}
	}
	
}
