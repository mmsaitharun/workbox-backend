package oneapp.workbox.services.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.entity.ConfigValues;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Repository("ConfigDao")
@Transactional
public class ConfigDao {

	@Autowired
	SessionFactory sessionFactory;

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	private static final Logger logger = LoggerFactory.getLogger(ConfigDao.class);

	@SuppressWarnings("unchecked")
	public String getConfigurationByRef(String configRef) {
		Criteria criteria = this.getSession().createCriteria(ConfigValues.class);
		if (!ServicesUtil.isEmpty(configRef)) {
			criteria.add(Restrictions.eq("configId", configRef));
			List<ConfigValues> configValues = criteria.list();
			if (!ServicesUtil.isEmpty(configValues) && configValues.size() == 1) {
				ConfigValues configValue = configValues.get(0);
				return configValue.getConfigValue();
			}
		}
		return null;
	}

	public ResponseMessage saveOrUpdateConfigByRef(String configRef, String configNewValue) {
		ConfigValues configValues = null;
		Session session = null;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(PMCConstant.FAILURE);
		responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		responseMessage.setMessage("Mandatory fields missing");
		if (!ServicesUtil.isEmpty(configRef) && !ServicesUtil.isEmpty(configNewValue)) {
			session = this.getSession();
			configValues = new ConfigValues();
			configValues.setConfigId(configRef);
			configValues.setConfigValue(configNewValue);
			try {
				session.saveOrUpdate(configValues);
				responseMessage.setStatus(PMCConstant.SUCCESS);
				responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
				responseMessage.setMessage("Save or update successful");
			} catch (Exception ex) {
				responseMessage.setMessage("Exception while save or update : " + ex.getMessage());
				logger.error("[ConfigDao][updateConfigurationByRef][Exception] : " + ex);
			}
		}
		return responseMessage;
	}

	public ResponseMessage deleteConfigurationByRef(String configRef) {
		ConfigValues configValues = null;
		Session session = null;
		ResponseMessage responseMessage = new ResponseMessage();
		responseMessage.setStatus(PMCConstant.FAILURE);
		responseMessage.setStatusCode(PMCConstant.CODE_FAILURE);
		responseMessage.setMessage("Mandatory fields missing");

		if (!ServicesUtil.isEmpty(configRef)) {
			session = this.getSession();
			configValues = new ConfigValues();
			configValues.setConfigId(configRef);
			try {
				session.delete(configValues);
				responseMessage.setStatus(PMCConstant.SUCCESS);
				responseMessage.setStatusCode(PMCConstant.CODE_SUCCESS);
				responseMessage.setMessage("Delete successful");
			} catch (Exception ex) {
				responseMessage.setMessage("Exception while delete : " + ex.getMessage());
				logger.error("[ConfigDao][deleteConfigurationByRef][Exception] : " + ex);
			}
		}
		return responseMessage;
	}

}