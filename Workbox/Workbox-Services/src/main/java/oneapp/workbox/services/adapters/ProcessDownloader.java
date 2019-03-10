package oneapp.workbox.services.adapters;

import java.util.List;

import oneapp.workbox.services.util.PMCConstant;

public class ProcessDownloader extends EventDownloader<List<Process>> {

	public ProcessDownloader() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Process> call() throws Exception {
		return getInstances(PMCConstant.REQUEST_URL_INST
				+ "workflow-instances?status=RUNNING&status=ERRONEOUS&status=CANCELED&status=COMPLETED",
				Process.class);
	}

}
