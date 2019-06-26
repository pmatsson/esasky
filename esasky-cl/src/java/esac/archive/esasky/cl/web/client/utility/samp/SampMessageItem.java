package esac.archive.esasky.cl.web.client.utility.samp;

public class SampMessageItem {
	
	private String urn;
	private String title;
	private String url;
	private String fileName;
	private String info;
	private String distributionPath;
	private String msgid;
	
	public SampMessageItem(){
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the urn
	 */
	public String getUrn() {
		return urn;
	}

	/**
	 * @param urn the urn to set
	 */
	public void setUrn(String urn) {
		this.urn = urn;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @return the distributionPath
	 */
	public String getDistributionPath() {
		return distributionPath;
	}

	/**
	 * @param distributionPath the distributionPath to set
	 */
	public void setDistributionPath(String distributionPath) {
		this.distributionPath = distributionPath;
		if(distributionPath != null){
			int p = distributionPath.lastIndexOf('/');
			if(p < 0){
				p = distributionPath.lastIndexOf('\\');
			}
			if(p < 0){
				setFileName(null);
			}else{
				String fileName = distributionPath.substring(p+1); 
				setFileName(fileName);
			}
		}else{
			setFileName(null);
		}
	}

	/**
	 * @return the msgid
	 */
	public String getMsgid() {
		return msgid;
	}

	/**
	 * @param msgid the msgid to set
	 */
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

}
