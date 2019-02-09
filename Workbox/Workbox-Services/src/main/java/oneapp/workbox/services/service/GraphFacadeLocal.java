package oneapp.workbox.services.service;

import oneapp.workbox.services.dto.GraphResponseDto;

public interface GraphFacadeLocal {

	GraphResponseDto getGraphDetails(String processType, String graphType, String duration, String userId);

}
