package oneapp.workbox.services.dto;

public class TokenDetailsDto {

	private String token;
	private Long expirationDateTime;
	private String tokenId;
	private String tokenType;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getExpirationDateTime() {
		return expirationDateTime;
	}

	public void setExpirationDateTime(Long expirationDateTime) {
		this.expirationDateTime = expirationDateTime;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@Override
	public String toString() {
		return "TokenDetailsDto [token=" + token + ", expirationDateTime=" + expirationDateTime + ", tokenId=" + tokenId
				+ ", tokenType=" + tokenType + "]";
	}

}