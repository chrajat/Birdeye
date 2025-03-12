package common.launchsetup;

public enum Platform {
	Web("Web"), WAP("WAP"), androidApp("androidApp"), marketAndroidApp("marketAndroidApp"), iosApp("iosApp"), WebApi("WebApi"), WAPApi("WAPApi");

	private final String name;

	private Platform(String platform) {
		name = platform;
	}
	public String getPlatformName() {
	       return this.name;
	    }
}
