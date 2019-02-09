package oneapp.workbox.services.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oneapp.workbox.services.dao.GraphDao;
import oneapp.workbox.services.dto.GraphDto;
import oneapp.workbox.services.dto.GraphResponseDto;
import oneapp.workbox.services.dto.ResponseMessage;
import oneapp.workbox.services.util.PMCConstant;
import oneapp.workbox.services.util.ServicesUtil;

@Service("GraphFacade")
public class GraphFacade implements GraphFacadeLocal {

	private static final Logger logger = LoggerFactory.getLogger(GraphFacade.class);

	@Autowired
	GraphDao graphDao;

	ResponseMessage responseDto;

	@Override
	public GraphResponseDto getGraphDetails(String processType ,String graphType,String duration,String userId) {
		GraphResponseDto response = new GraphResponseDto();
		GraphDto graphDto= null;
		responseDto = new ResponseMessage();
		responseDto.setStatus("FAILURE");
		responseDto.setStatusCode("1");
		try {
			graphDto = graphDao.getDetails(processType,graphType,duration,userId);

			if (!ServicesUtil.isEmpty(graphDto)) {
				response.setGraphDto(graphDto);
				responseDto.setMessage("Data fetched Successfully");
			} else {
				responseDto.setMessage(PMCConstant.NO_RESULT);
			}
			responseDto.setStatus("SUCCESS");
			responseDto.setStatusCode("0");
		} catch (Exception e) {
			logger.error("[PMC][HeatMap][getUserWorkLoad][error]" + e.getMessage());
			responseDto.setMessage("Fetching data failed due to " + e.getMessage());
		}
		response.setMessage(responseDto);
		return response;
	}

	
}