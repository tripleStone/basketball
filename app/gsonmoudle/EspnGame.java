package gsonmoudle;

public class EspnGame {
	
	private Long gameId;
	
	private Integer status;
	
	private String statusText;
	
	private EspnGameTeam away;
	
	private EspnGameTeam home;

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public EspnGameTeam getAway() {
		return away;
	}

	public void setAway(EspnGameTeam away) {
		this.away = away;
	}

	public EspnGameTeam getHome() {
		return home;
	}

	public void setHome(EspnGameTeam home) {
		this.home = home;
	}
	
	

}
