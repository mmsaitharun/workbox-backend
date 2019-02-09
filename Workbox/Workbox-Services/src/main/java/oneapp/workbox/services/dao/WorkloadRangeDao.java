package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.WorkloadRangeDto;
import oneapp.workbox.services.entity.WorkloadRangeDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("WorkloadRangeDao")
@Transactional
public class WorkloadRangeDao {
	
	@Autowired
	SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	protected WorkloadRangeDo importDto(WorkloadRangeDto fromDto) {
		WorkloadRangeDo workloadRangeDo = new WorkloadRangeDo();
		if (!ServicesUtil.isEmpty(fromDto.getLoadType())) {
			workloadRangeDo.setLoadType(fromDto.getLoadType());
			if (!ServicesUtil.isEmpty(fromDto.getHighLimit()))
				workloadRangeDo.setHighLimit(fromDto.getHighLimit());
			if (!ServicesUtil.isEmpty(fromDto.getLowLimit()))
				workloadRangeDo.setLowLimit(fromDto.getLowLimit());
		}

		return workloadRangeDo;
	}

	protected WorkloadRangeDto exportDto(WorkloadRangeDo entity) {
		WorkloadRangeDto workloadRangeDto = new WorkloadRangeDto();
		workloadRangeDto.setLoadType(entity.getLoadType());
		if (!ServicesUtil.isEmpty(entity.getHighLimit()))
			workloadRangeDto.setHighLimit(entity.getHighLimit());
		if (!ServicesUtil.isEmpty(entity.getLowLimit()))
			workloadRangeDto.setLowLimit(entity.getLowLimit());
		return workloadRangeDto;
	}
	
	protected List<WorkloadRangeDto> exportDtoList(Collection<WorkloadRangeDo> listDo) {
		List<WorkloadRangeDto> returnDtos = null;
		if (!ServicesUtil.isEmpty(listDo)) {
			returnDtos = new ArrayList<WorkloadRangeDto>(listDo.size());
			for (Iterator<WorkloadRangeDo> iterator = listDo.iterator(); iterator.hasNext();) {
				returnDtos.add(exportDto(iterator.next()));
			}
		}
		return returnDtos;
	}

	public String updateWorkloadRange(WorkloadRangeDto dto) {
		String response = PMCConstant.FAILURE;
		try {
			System.err.println("workloadRangeDtos 4" + dto.toString());

			String updateQuery = "UPDATE workload_tb set lower_limit=" + dto.getLowLimit() + ",upper_limit="
					+ dto.getHighLimit() + " where load_type='" + dto.getLoadType() + "'";
			System.err.println("[PMC][workloadRangeDao][updateQuery]" + updateQuery);

			int resultRows = this.getSession().createSQLQuery(updateQuery).executeUpdate();
			if (resultRows > 0) {
				return PMCConstant.SUCCESS;
			}
		} catch (Exception e) {
			System.err.println("[PMC][workloadRangeDao][error]" + e.getLocalizedMessage());
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	public List<WorkloadRangeDto> getAllResults(String string) {
		List<WorkloadRangeDo> list = this.getSession().createCriteria(WorkloadRangeDo.class).list();
		if(!ServicesUtil.isEmpty(list) && list.size() > 0) {
			return exportDtoList(list);
		}
		return null;
	}

	public void create(WorkloadRangeDto workloadRangeDto) {
		this.getSession().save(importDto(workloadRangeDto));
	}
}
