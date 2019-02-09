package oneapp.workbox.services.dao;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import oneapp.workbox.services.entity.InboxActions;

@Repository
@Transactional
public class InboxActionsDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	public void saveOrUpdateAction(InboxActions inboxActions) {
		getSession().saveOrUpdate(inboxActions);
	}
}
