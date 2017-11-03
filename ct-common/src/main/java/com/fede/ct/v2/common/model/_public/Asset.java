package com.fede.ct.v2.common.model._public;


/**
 * Created by f.barbano on 07/09/2017.
 * This class is the result object of the call to 'https://api.kraken.com/0/public/Assets'
 */
public class Asset {

	private String assetName;
	private String aClass;
	private String altName;
	private Integer decimals;
	private Integer displayDecimals;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Asset)) return false;

		Asset asset = (Asset) o;

		if (assetName != null ? !assetName.equals(asset.assetName) : asset.assetName != null) return false;
		if (aClass != null ? !aClass.equals(asset.aClass) : asset.aClass != null) return false;
		if (altName != null ? !altName.equals(asset.altName) : asset.altName != null) return false;
		if (decimals != null ? !decimals.equals(asset.decimals) : asset.decimals != null) return false;
		return displayDecimals != null ? displayDecimals.equals(asset.displayDecimals) : asset.displayDecimals == null;
	}

	@Override
	public int hashCode() {
		int result = assetName != null ? assetName.hashCode() : 0;
		result = 31 * result + (aClass != null ? aClass.hashCode() : 0);
		result = 31 * result + (altName != null ? altName.hashCode() : 0);
		result = 31 * result + (decimals != null ? decimals.hashCode() : 0);
		result = 31 * result + (displayDecimals != null ? displayDecimals.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return assetName;
	}

	public String getAssetName() {
		return assetName;
	}
	public void setAssetName(String assetName) {
		this.assetName = assetName;
	}
	public String getAClass() {
		return aClass;
	}
	public void setAClass(String aClass) {
		this.aClass = aClass;
	}
	public String getAltName() {
		return altName;
	}
	public void setAltName(String altName) {
		this.altName = altName;
	} 
	public Integer getDecimals() {
		return decimals;
	} 
	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	} 
	public Integer getDisplayDecimals() {
		return displayDecimals;
	} 
	public void setDisplayDecimals(Integer displayDecimals) {
		this.displayDecimals = displayDecimals;
	}

}
