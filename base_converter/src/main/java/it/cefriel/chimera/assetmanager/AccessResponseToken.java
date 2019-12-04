package it.cefriel.chimera.assetmanager;

public class AccessResponseToken {
	private String access;
	private String refresh;
	
	public String getAccess() {
		return access;
	}
	public void setAccess(String token) {
		this.access = token;
	}
	public String getRefresh() {
		return refresh;
	}
	public void setRefresh(String refreshToken) {
		this.refresh = refreshToken;
	}
	
	
}
