package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.config.HibernateConfiguration;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.dto.SlaListDto;
import oneapp.workbox.services.dto.SlaManagementDto;
import oneapp.workbox.services.dto.SlaProcessNameListDto;
import oneapp.workbox.services.entity.ProcessConfigDo;
import oneapp.workbox.services.entity.SlaManagementDo;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("SlaManagementDao")
@Transactional
public class SlaManagementDao {

	@Autowired
	SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final Logger logger = LoggerFactory.getLogger(SlaManagementDao.class);

	protected SlaManagementDo importDto(SlaManagementDto fromDto) {
		SlaManagementDo entity = new SlaManagementDo();
		String sla = "";
		if (!ServicesUtil.isEmpty(fromDto.getSlaId()))
			entity.setSlaId(fromDto.getSlaId());
		if (!ServicesUtil.isEmpty(fromDto.getProcessName()))
			entity.setProcessName(fromDto.getProcessName());
		if (!ServicesUtil.isEmpty(fromDto.getTaskType()))
			entity.setTaskType(fromDto.getTaskType());
		if (!ServicesUtil.isEmpty(fromDto.getModeName()))
			entity.setModeName(fromDto.getModeName());
		if (!ServicesUtil.isEmpty(fromDto.getTaskName()))
			entity.setTaskName(fromDto.getTaskName());
		if (!ServicesUtil.isEmpty(fromDto.getDescription()))
			entity.setDescription(fromDto.getDescription());
		if (!ServicesUtil.isEmpty(fromDto.getSubject()))
			entity.setSubject(fromDto.getSubject());
		if (!ServicesUtil.isEmpty(fromDto.getSlaCount()) && !ServicesUtil.isEmpty(fromDto.getSlaUnit())) {
			sla = fromDto.getSlaCount() + " " + fromDto.getSlaUnit();
			entity.setSla(sla);
		}
		if (!ServicesUtil.isEmpty(fromDto.getUrgentSlaCount()) && !ServicesUtil.isEmpty(fromDto.getUrgentSlaUnit())) {
			sla = fromDto.getUrgentSlaCount() + " " + fromDto.getUrgentSlaUnit();
			entity.setUrgentSla(sla);
		}
		return entity;
	}

	protected SlaManagementDto exportDto(SlaManagementDo entity) {
		SlaManagementDto slaManagementDto = new SlaManagementDto();
		String[] sla, urgentSla;
		if (!ServicesUtil.isEmpty(entity.getSlaId()))
			slaManagementDto.setSlaId(entity.getSlaId());
		if (!ServicesUtil.isEmpty(entity.getProcessName()))
			slaManagementDto.setProcessName(entity.getProcessName());
		if (!ServicesUtil.isEmpty(entity.getModeName()))
			slaManagementDto.setModeName(entity.getModeName());
		if (!ServicesUtil.isEmpty(entity.getTaskName()))
			slaManagementDto.setTaskName(entity.getTaskName());
		if (!ServicesUtil.isEmpty(entity.getTaskType()))
			slaManagementDto.setTaskType(entity.getTaskType());
		if (!ServicesUtil.isEmpty(entity.getDescription()))
			slaManagementDto.setDescription(entity.getDescription());
		if (!ServicesUtil.isEmpty(entity.getSubject()))
			slaManagementDto.setSubject(entity.getSubject());

		if (!ServicesUtil.isEmpty(entity.getSla())) {
			sla = entity.getSla().split("\\s");
			if (!ServicesUtil.isEmpty(sla[1])) {
				slaManagementDto.setSlaCount(sla[0]);
				slaManagementDto.setSlaUnit(sla[1]);
				slaManagementDto.setSlaCountOld(sla[0]);
				slaManagementDto.setSlaUnitOld(sla[1]);
			}
		}
		if (!ServicesUtil.isEmpty(entity.getUrgentSla())) {
			urgentSla = entity.getUrgentSla().split("\\s");
			if (!ServicesUtil.isEmpty(urgentSla[1])) {
				slaManagementDto.setUrgentSlaCount(urgentSla[0]);
				slaManagementDto.setUrgentSlaUnit(urgentSla[1]);
				slaManagementDto.setUrgentSlaCountOld(urgentSla[0]);
				slaManagementDto.setUrgentSlaUnitOld(urgentSla[1]);
			}
		}
		return slaManagementDto;
	}

	@SuppressWarnings("unchecked")
	public List<SlaProcessNameListDto> getAllProcessName(String userRole) {
		logger.error("[PMC][SlaManagementDao][getAllProcessName] method invoked ");
		// Query query = this.getSession().createQuery("select p from
		// ProcessConfigDo p where p.userRole like '%"+user+"%'");
		// List<ProcessConfigDo> processNameList = (List<ProcessConfigDo>)
		// query.list();
		Criteria criteria = this.getSession().createCriteria(ProcessConfigDo.class);
		if (!ServicesUtil.isEmpty(userRole))
			criteria.add(Restrictions.eq("userRole", userRole));
		List<ProcessConfigDo> processNameList = criteria.list();
		List<SlaProcessNameListDto> slaProcessList = new ArrayList<SlaProcessNameListDto>();

		if (!ServicesUtil.isEmpty(processNameList)) {
			logger.error("[PMC][SlaManagementDao][getAllProcessName] processList not empty ");
			for (ProcessConfigDo entity : processNameList) {
				if (!entity.getProcessName().equals("ALL")) {
					SlaProcessNameListDto dto = new SlaProcessNameListDto();
					dto.setProcessName(entity.getProcessName());
					dto.setProcessDisplayName(entity.getProcessDisplayName());
					if (!ServicesUtil.isEmpty(entity.getSla())) {
						String[] sla = entity.getSla().split("\\s");
						if (!ServicesUtil.isEmpty(sla[1])) {
							dto.setSlaCount(sla[0]);
							dto.setSlaUnit(sla[1]);
						}
					}
					if (!ServicesUtil.isEmpty(entity.getSla())) {
						dto.setSlaExist(true);
					} else {
						dto.setSlaExist(false);
					}
					slaProcessList.add(dto);
					logger.error("[PMC][SlaManagementDao][getAllProcessName] entity " + entity);
				}
			}
		} else {
			logger.error("[PMC][SlaManagementDao][getAllProcessName] processList is empty ");
		}

		return slaProcessList;
	}

	/**
	 * @param processName
	 *            -
	 * @return
	 * @throws NoResultFault
	 */
	public Long getNoOfInstances(String processName) {
		Query query = this.getSession()
				.createQuery("select count(DISTINCT p.processId) from ProcessEventsDo p where p.name =:processName");
		query.setParameter("processName", processName);
		Long instanceCount = (Long) query.uniqueResult();
		return instanceCount;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<SlaProcessNameListDto> getSlaProcessList(String userRole) {
		try {
			List<SlaProcessNameListDto> resultset = this.getAllProcessName(userRole);
			Criteria criteria = this.getSession().createCriteria(SlaManagementDo.class);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			criteria.add(Restrictions.ne("sla", null));
			criteria.add(Restrictions.ne("urgentSla", null));
			// Query query = this.getSession().createQuery(
			// "select DISTINCT p.processName from SlaManagementDo p where p.sla
			// is not null or p.urgentSla is not null ");
			// List<String> slaProcessNameList = (List<String>) query.list();
			List<String> slaProcessNameList = criteria.list();
			if (!ServicesUtil.isEmpty(slaProcessNameList)) {
				for (SlaProcessNameListDto entity : resultset) {
					if (!entity.getSlaExist() && slaProcessNameList.contains(entity.getProcessName())) {
						entity.setSlaExist(true);
					}
				}
				return resultset;
			} else {
				return resultset;
			}
		} catch (Exception e) {
			logger.error("NO PROCESS FOUND");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public SlaListDto getDetailSla(String processName) {
		logger.error("[PMC][SlaManagementDao][getDetailSla] Method Initiated with Inputs :" + processName);
		SlaListDto slaDetailDto = new SlaListDto();

		Query query = this.getSession().createQuery(
				"select p from SlaManagementDo p where p.processName =:processName and upper(p.taskName) <> 'END' and upper(p.taskName) <> 'START'");
		query.setParameter("processName", processName);

		List<SlaManagementDo> slaDoList = (List<SlaManagementDo>) query.list();
		logger.error("Size : " + slaDoList.size());
		logger.error("[PMC][SlaManagementDao][getDetailSla] Sla List is empty check");
		if (!ServicesUtil.isEmpty(slaDoList)) {
			logger.error("[PMC][SlaManagementDao][getDetailSla] Sla List isnt empty");
			List<SlaManagementDto> slaDtoList = new ArrayList<SlaManagementDto>();
			for (SlaManagementDo entity : slaDoList) {
				logger.error("SlaManagementDto" + entity.toString());
				if (!ServicesUtil.isEmpty(entity.getTaskName())) {
					slaDtoList.add(exportDto(entity));
				}
			}
			slaDetailDto.setSlaList(slaDtoList);
		}
		Query query1 = this.getSession()
				.createQuery("select p from ProcessConfigDo p where  p.processName = :processName");
		query1.setParameter("processName", processName);
		List<ProcessConfigDo> headerSlas = (List<ProcessConfigDo>) query1.list();
		for (ProcessConfigDo headerSla : headerSlas) {
			if (!ServicesUtil.isEmpty(headerSla.getSla())) {
				slaDetailDto.setSlaCount(headerSla.getSla().split("\\s")[0]);
				slaDetailDto.setSlaUnit(headerSla.getSla().split("\\s")[1]);
				slaDetailDto.setSlaCountOld(headerSla.getSla().split("\\s")[0]);
				slaDetailDto.setSlaUnitOld(headerSla.getSla().split("\\s")[1]);
			}
			slaDetailDto.setProcessDisplayName(headerSla.getProcessDisplayName());
		}
		slaDetailDto.setNoOfInstances(this.getNoOfInstances(processName).toString());
		slaDetailDto.setProcessName(processName);
		return slaDetailDto;
	}

	public ResponseMessage updateSla(SlaListDto slaDto) {

		List<SlaManagementDto> slaDtoList = slaDto.getSlaList();
		ResponseMessage response = new ResponseMessage();
		if (!ServicesUtil.isEmpty(slaDto.getIsUpdated()) && slaDto.getIsUpdated() == true) {
			response.setStatus(this.headerUpdate(slaDto));
		}

		if (!ServicesUtil.isEmpty(slaDtoList)) {
			for (SlaManagementDto dto : slaDtoList) {
				logger.error("[PMC][SlaManagementDao][updateSla][updatedDto]" + dto.toString());
				if (!ServicesUtil.isEmpty(dto.getSlaId())) {
					Query query = this.getSession().createQuery(
							"update SlaManagementDo p set p.sla = :sla , p.urgentSla = :urgentSla  where  p.slaId =:slaId");
					query.setParameter("slaId", dto.getSlaId());
					if (!ServicesUtil.isEmpty(dto.getSlaCount()) && !ServicesUtil.isEmpty(dto.getSlaUnit())) {
						query.setParameter("sla", dto.getSlaCount() + " " + dto.getSlaUnit());
					} else {
						query.setParameter("sla", null);
					}
					if (!ServicesUtil.isEmpty(dto.getUrgentSlaCount())
							&& !ServicesUtil.isEmpty(dto.getUrgentSlaUnit())) {
						query.setParameter("urgentSla", dto.getUrgentSlaCount() + " " + dto.getUrgentSlaUnit());
					} else {
						query.setParameter("urgentSla", null);
					}

					query.executeUpdate();
				} else {
					response.setMessage("Newly added item is sent as update");
					return response;
				}
			}
		} else {
			response.setMessage("NO UPDATE EXIST");
			return response;
		}

		response.setMessage("UPDATED SUCCESSFULLY");
		return response;
	}

	public String headerUpdate(SlaListDto dto) {
		Query query = this.getSession()
				.createQuery("update ProcessConfigDo p set p.sla =:sla where p.processName=:processName");
		if (!ServicesUtil.isEmpty(dto.getSlaCount()) && !ServicesUtil.isEmpty(dto.getSlaUnit())) {
			query.setParameter("sla", dto.getSlaCount() + " " + dto.getSlaUnit());
		} else {
			query.setParameter("sla", null);
		}
		query.setParameter("processName", dto.getProcessName());
		query.executeUpdate();
		return "SUCCESS";
	}
	
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(HibernateConfiguration.class);
		SlaManagementDao slaManagementDao = applicationContext.getBean(SlaManagementDao.class);
		List<SlaProcessNameListDto> slaProcessList = slaManagementDao.getSlaProcessList(null);
		System.out.println(slaProcessList);
		applicationContext.close();
		
	}
}
