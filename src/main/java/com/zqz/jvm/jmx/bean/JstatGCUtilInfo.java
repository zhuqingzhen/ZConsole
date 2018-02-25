package com.zqz.jvm.jmx.bean;

/**
 * 
 * @author zqz
 *
 */
public class JstatGCUtilInfo {
	
	private long S0;
	private long S1;
	private long E;
	private long O;
	private long M;
	private long CCS;
	private long YGC;
	private long YGCT;
	private long FGC;
	private long FGCT;
	private long GCT;
	
	public long getS0() {
		return S0;
	}
	public void setS0(long s0) {
		S0 = s0;
	}
	public long getS1() {
		return S1;
	}
	public void setS1(long s1) {
		S1 = s1;
	}
	public long getE() {
		return E;
	}
	public void setE(long e) {
		E = e;
	}
	public long getO() {
		return O;
	}
	public void setO(long o) {
		O = o;
	}
	public long getM() {
		return M;
	}
	public void setM(long m) {
		M = m;
	}
	public long getCCS() {
		return CCS;
	}
	public void setCCS(long cCS) {
		CCS = cCS;
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
