package oneapp.workbox.services.dao;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.UserIDPMappingDto;
import oneapp.workbox.services.entity.UserIDPMappingDo;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("userIDPMappingDao")
@Transactional
public class UserIDPMappingDao {
	
	@Autowired
	SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final Logger logger = LoggerFactory.getLogger(UserIDPMappingDao.class);

	protected UserIDPMappingDo importDto(UserIDPMappingDto fromDto) {
		UserIDPMappingDo mappingDo = null;
		if (!ServicesUtil.isEmpty(fromDto)) {
			mappingDo = new UserIDPMappingDo();
			mappingDo.setSerialId(fromDto.getSerialId());
			mappingDo.setUserEmail(fromDto.getUserEmail());
			mappingDo.setUserFirstName(fromDto.getUserFirstName());
			mappingDo.setUserLastName(fromDto.getUserLastName());
			mappingDo.setUserLoginName(fromDto.getUserLoginName());
			mappingDo.setUserId(fromDto.getUserId());
			mappingDo.setUserRole(fromDto.getUserRole());
		}
		return mappingDo;
	}

	protected UserIDPMappingDto exportDto(UserIDPMappingDo entity) {
		UserIDPMappingDto mappingDto = null;
		if (!ServicesUtil.isEmpty(entity)) {
			mappingDto = new UserIDPMappingDto();
			mappingDto.setSerialId(entity.getSerialId());
			mappingDto.setUserEmail(entity.getUserEmail());
			mappingDto.setUserFirstName(entity.getUserFirstName());
			mappingDto.setUserLastName(entity.getUserLastName());
			mappingDto.setUserLoginName(entity.getUserLoginName());
			mappingDto.setUserId(entity.getUserId());
			mappingDto.setUserRole(entity.getUserRole());
		}
		return mappingDto;
	}

	@SuppressWarnings("unchecked")
	public List<UserIDPMappingDto> getAllUser() {
		List<UserIDPMappingDto> dtos = new ArrayList<UserIDPMappingDto>();

		String queryString = " select do from UserIDPMappingDo do";
		try {
			logger.error("[PMC][UserIDPMappingDao][getAllUser]" + queryString);
			Query query = this.getSession().createQuery(queryString);
			List<UserIDPMappingDo> resultList = query.list();
			if (!ServicesUtil.isEmpty(resultList)) {
				for (UserIDPMappingDo entity : resultList) {
					dtos.add(exportDto(entity));
				}
			}
		} catch (Exception e) {
			System.err.println(
					"[PMC][UserIDPMappingDao][UserIDPMappingDao][getIDPUser][error]" + e.getLocalizedMessage());
		}
		return dtos;
	}
	
	
	public String createIDPUser(UserIDPMappingDto dto) {
		String response = PMCConstant.FAILURE;
		try {

			this.getSession().save(importDto(dto));
			response = PMCConstant.SUCCESS;
		} catch (Exception e) {
			logger.error(
					"[PMC][UserIDPMappingDao][UserIDPMappingDao][createIDPUser][error]" + e.getMessage());
		}
		return response;
	}

	public void delete(UserIDPMappingDto dto) {
		this.getSession().delete(importDto(dto));;
	}
}