package oneapp.workbox.services.adapters;

import java.util.List;

import oneapp.workbox.services.util.PMCConstant;

public class TaskDownloader extends EventDownloader<List<Task>> {

	public TaskDownloader() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Task> call() throws Exception {
		return getInstances(PMCConstant.REQUEST_URL_INST
				+ "task-instances?status=READY&status=RESERVED&status=CANCELED&status=COMPLETED&$expand=attributes",
				Task.class);
	}

}
