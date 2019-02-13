package oneapp.workbox.services.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "WORKFLOW_EX_GATEWAY")
public class WorkFlowExclusiveGateway extends WorkFlowArtifact {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WorkFlowExclusiveGateway() {
		super();
	}

	public WorkFlowExclusiveGateway(String id, String artifactName, String artifactClassDefinition, String artifactId, String workFlowDefId,
			String gatewayDefault, String artifactIcon) {
		super(id, artifactName, artifactClassDefinition, artifactId, workFlowDefId, artifactIcon);
		this.gatewayDefault = gatewayDefault;
	}

	@Column(name = "GATEWAY_DEFAULT", length = 70)
	private String gatewayDefault;

	public String getGatewayDefault() {
		return gatewayDefault;
	}

	public void setGatewayDefault(String gatewayDefault) {
		this.gatewayDefault = gatewayDefault;
	}

	@Override
	public String toString() {
		return "WorkFlowExclusiveGateway [gatewayDefault=" + gatewayDefault + ", getId()=" + getId()
				+ ", getArtifactName()=" + getArtifactName() + ", getArtifactClassDefinition()="
				+ getArtifactClassDefinition() + ", getArtifactId()=" + getArtifactId() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + "]";
	}

}
