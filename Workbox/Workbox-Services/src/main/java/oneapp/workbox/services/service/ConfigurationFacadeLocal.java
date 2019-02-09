package oneapp.workbox.services.service;

import java.util.List;

import oneapp.workbox.services.dto.ProcessConfigDto;
import oneapp.workbox.services.dto.ProcessConfigResponse;
import oneapp.workbox.services.dto.ProcessListDto;
import oneapp.workbox.services.dto.ReportAgingDto;
import oneapp.workbox.services.dto.WorkloadRangeResponse;


public interface ConfigurationFacadeLocal {

	ProcessListDto getAllProcessNames();

	ProcessConfigResponse getAllBusinessLabels();

	ProcessConfigDto getBusinessLabelByProcessName(String processName);

	WorkloadRangeResponse getWorkLoadRange();

	List<ReportAgingDto> getAgeingBuckets(String reportName);

	ProcessConfigResponse getUserBusinessLabels(String userRole);

}
