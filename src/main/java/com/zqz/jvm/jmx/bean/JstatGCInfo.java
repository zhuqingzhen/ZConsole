package com.zqz.jvm.jmx.bean;

public class JstatGCInfo {
	
	private long delay;
	
	private long SC;
	private long SU;
	private long EC;
	private long EU;
	private long OC;
	private long OU;
	private long YGC;
	private long YGCT;
	private long FGC;
	private long FGCT;
	private long GCT;
	//code cache
	private long CCC;
	private long CCU;
	
	private long MC;
	private long MU;
	private long CCSC;
	private long CCSU;
	
	private long PC;
	private long PU;
	
	
	public long getDelay() {
		return delay;
	}
	public void setDelay(long delay) {
		this.delay = delay;
	}
	public long getPC() {
		return PC;
	}
	public void setPC(long pC) {
		PC = pC;
	}
	public long getPU() {
		return PU;
	}
	public void setPU(long pU) {
		PU = pU;
	}
	public long getCCC() {
		return CCC;
	}
	public void setCCC(long cCC) {
		CCC = cCC;
	}
	public long getCCU() {
		return CCU;
	}
	public void setCCU(long cCU) {
		CCU = cCU;
	}
	public long getSC() {
		return SC;
	}
	public void setSC(long sC) {
		SC = sC;
	}
	public long getSU() {
		return SU;
	}
	public void setSU(long sU) {
		SU = sU;
	}
	public long getEC() {
		return EC;
	}
	public void setEC(long eC) {
		EC = eC;
	}
	public long getEU() {
		return EU;
	}
	public void setEU(long eU) {
		EU = eU;
	}
	public long getOC() {
		return OC;
	}
	public void setOC(long oC) {
		OC = oC;
	}
	public long getOU() {
		return OU;
	}
	public void setOU(long oU) {
		OU = oU;
	}
	public long getMC() {
		return MC;
	}
	public void setMC(long mC) {
		MC = mC;
	}
	public long getMU() {
		return MU;
	}
	public void setMU(long mU) {
		MU = mU;
	}
	public long getCCSC() {
		return CCSC;
	}
	public void setCCSC(long cCSC) {
		CCSC = cCSC;
	}
	public long getCCSU() {
		return CCSU;
	}
	public void setCCSU(long cCSU) {
		CCSU = cCSU;
	}
	public long getYGC() {
		return YGC;
	}
	public void setYGC(long yGC) {
		YGC = yGC;
	}
	public long getYGCT() {
		return YGCT;
	}
	public void setYGCT(long yGCT) {
		YGCT = yGCT;
	}
	public long getFGC() {
		return FGC;
	}
	public void setFGC(long fGC) {
		FGC = fGC;
	}
	public long getFGCT() {
		return FGCT;
	}
	public void setFGCT(long fGCT) {
		FGCT = fGCT;
	}
	public long getGCT() {
		return GCT;
	}
	public void setGCT(long gCT) {
		GCT = gCT;
	}
	
}
