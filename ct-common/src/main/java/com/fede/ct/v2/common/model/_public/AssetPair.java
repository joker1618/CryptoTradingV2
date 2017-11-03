package com.fede.ct.v2.common.model._public;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f.barbano on 13/09/2017.
 */
public class AssetPair {

	private String pairName;
	private String altName;
	private String aClassBase;
	private String base;
	private String aClassQuote;
	private String quote;
	private String lot;
	private Integer pairDecimals;
	private Integer lotDecimals;
	private Integer lotMultiplier;
	private List<Integer> leverageBuy;     	// es. "leverage_buy":[2,3]
	private List<Integer> leverageSell;		// es. "leverage_sell":[2,3]
	private List<FeeSchedule> fees;       	// es. "fees":[[0,0.26],[50000,0.24],[100000,0.22],...]
	private List<FeeSchedule> feesMaker;   	// es. "fees_maker":[[0,0.16],[50000,0.14],[100000,0.12],...]
	private String feeVolumeCurrency;
	private Integer marginCall;
	private Integer marginStop;

	public AssetPair() {
		this.leverageBuy = new ArrayList<>();
		this.leverageSell = new ArrayList<>();
		this.fees = new ArrayList<>();
		this.feesMaker = new ArrayList<>();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AssetPair)) return false;

		AssetPair assetPair = (AssetPair) o;

		if (pairName != null ? !pairName.equals(assetPair.pairName) : assetPair.pairName != null) return false;
		if (altName != null ? !altName.equals(assetPair.altName) : assetPair.altName != null) return false;
		if (aClassBase != null ? !aClassBase.equals(assetPair.aClassBase) : assetPair.aClassBase != null) return false;
		if (base != null ? !base.equals(assetPair.base) : assetPair.base != null) return false;
		if (aClassQuote != null ? !aClassQuote.equals(assetPair.aClassQuote) : assetPair.aClassQuote != null)
			return false;
		if (quote != null ? !quote.equals(assetPair.quote) : assetPair.quote != null) return false;
		if (lot != null ? !lot.equals(assetPair.lot) : assetPair.lot != null) return false;
		if (pairDecimals != null ? !pairDecimals.equals(assetPair.pairDecimals) : assetPair.pairDecimals != null)
			return false;
		if (lotDecimals != null ? !lotDecimals.equals(assetPair.lotDecimals) : assetPair.lotDecimals != null)
			return false;
		if (lotMultiplier != null ? !lotMultiplier.equals(assetPair.lotMultiplier) : assetPair.lotMultiplier != null)
			return false;
		if (leverageBuy != null ? !leverageBuy.equals(assetPair.leverageBuy) : assetPair.leverageBuy != null)
			return false;
		if (leverageSell != null ? !leverageSell.equals(assetPair.leverageSell) : assetPair.leverageSell != null)
			return false;
		if (fees != null ? !fees.equals(assetPair.fees) : assetPair.fees != null) return false;
		if (feesMaker != null ? !feesMaker.equals(assetPair.feesMaker) : assetPair.feesMaker != null) return false;
		if (feeVolumeCurrency != null ? !feeVolumeCurrency.equals(assetPair.feeVolumeCurrency) : assetPair.feeVolumeCurrency != null)
			return false;
		if (marginCall != null ? !marginCall.equals(assetPair.marginCall) : assetPair.marginCall != null) return false;
		return marginStop != null ? marginStop.equals(assetPair.marginStop) : assetPair.marginStop == null;
	}

	@Override
	public int hashCode() {
		int result = pairName != null ? pairName.hashCode() : 0;
		result = 31 * result + (altName != null ? altName.hashCode() : 0);
		result = 31 * result + (aClassBase != null ? aClassBase.hashCode() : 0);
		result = 31 * result + (base != null ? base.hashCode() : 0);
		result = 31 * result + (aClassQuote != null ? aClassQuote.hashCode() : 0);
		result = 31 * result + (quote != null ? quote.hashCode() : 0);
		result = 31 * result + (lot != null ? lot.hashCode() : 0);
		result = 31 * result + (pairDecimals != null ? pairDecimals.hashCode() : 0);
		result = 31 * result + (lotDecimals != null ? lotDecimals.hashCode() : 0);
		result = 31 * result + (lotMultiplier != null ? lotMultiplier.hashCode() : 0);
		result = 31 * result + (leverageBuy != null ? leverageBuy.hashCode() : 0);
		result = 31 * result + (leverageSell != null ? leverageSell.hashCode() : 0);
		result = 31 * result + (fees != null ? fees.hashCode() : 0);
		result = 31 * result + (feesMaker != null ? feesMaker.hashCode() : 0);
		result = 31 * result + (feeVolumeCurrency != null ? feeVolumeCurrency.hashCode() : 0);
		result = 31 * result + (marginCall != null ? marginCall.hashCode() : 0);
		result = 31 * result + (marginStop != null ? marginStop.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return pairName;
	}


	public String getPairName() {
		return pairName;
	} 
	public void setPairName(String pairName) {
		this.pairName = pairName;
	} 
	public String getAltName() {
		return altName;
	} 
	public void setAltName(String altName) {
		this.altName = altName;
	} 
	public String getAClassBase() {
		return aClassBase;
	} 
	public void setAClassBase(String aClassBase) {
		this.aClassBase = aClassBase;
	} 
	public String getBase() {
		return base;
	} 
	public void setBase(String base) {
		this.base = base;
	} 
	public String getAClassQuote() {
		return aClassQuote;
	} 
	public void setAClassQuote(String aClassQuote) {
		this.aClassQuote = aClassQuote;
	} 
	public String getQuote() {
		return quote;
	} 
	public void setQuote(String quote) {
		this.quote = quote;
	} 
	public String getLot() {
		return lot;
	} 
	public void setLot(String lot) {
		this.lot = lot;
	} 
	public Integer getPairDecimals() {
		return pairDecimals;
	} 
	public void setPairDecimals(Integer pairDecimals) {
		this.pairDecimals = pairDecimals;
	} 
	public Integer getLotDecimals() {
		return lotDecimals;
	} 
	public void setLotDecimals(Integer lotDecimals) {
		this.lotDecimals = lotDecimals;
	} 
	public Integer getLotMultiplier() {
		return lotMultiplier;
	} 
	public void setLotMultiplier(Integer lotMultiplier) {
		this.lotMultiplier = lotMultiplier;
	} 
	public List<Integer> getLeverageBuy() {
		return leverageBuy;
	} 
	public void setLeverageBuy(List<Integer> leverageBuy) {
		this.leverageBuy = leverageBuy;
	} 
	public List<Integer> getLeverageSell() {
		return leverageSell;
	} 
	public void setLeverageSell(List<Integer> leverageSell) {
		this.leverageSell = leverageSell;
	} 
	public List<FeeSchedule> getFees() {
		return fees;
	} 
	public void setFees(List<FeeSchedule> fees) {
		this.fees = fees;
	} 
	public List<FeeSchedule> getFeesMaker() {
		return feesMaker;
	} 
	public void setFeesMaker(List<FeeSchedule> feesMaker) {
		this.feesMaker = feesMaker;
	} 
	public String getFeeVolumeCurrency() {
		return feeVolumeCurrency;
	} 
	public void setFeeVolumeCurrency(String feeVolumeCurrency) {
		this.feeVolumeCurrency = feeVolumeCurrency;
	} 
	public Integer getMarginCall() {
		return marginCall;
	} 
	public void setMarginCall(Integer marginCall) {
		this.marginCall = marginCall;
	} 
	public Integer getMarginStop() {
		return marginStop;
	} 
	public void setMarginStop(Integer marginStop) {
		this.marginStop = marginStop;
	}

	public static class FeeSchedule {
		private Integer volume;
		private BigDecimal percentFee;

		public FeeSchedule(Integer volume, BigDecimal percentFee) {
			this.volume = volume;
			this.percentFee = percentFee;
		}

		public Integer getVolume() {
			return volume;
		}                                                              	
		public void setVolume(Integer volume) {
			this.volume = volume;
		} 
		public BigDecimal getPercentFee() {
			return percentFee;
		} 
		public void setPercentFee(BigDecimal percentFee) {
			this.percentFee = percentFee;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof FeeSchedule)) return false;

			FeeSchedule that = (FeeSchedule) o;

			if (volume != null ? !volume.equals(that.volume) : that.volume != null) return false;
			return percentFee != null ? percentFee.equals(that.percentFee) : that.percentFee == null;
		}

		@Override
		public int hashCode() {
			int result = volume != null ? volume.hashCode() : 0;
			result = 31 * result + (percentFee != null ? percentFee.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "FeeSchedule{" +
					   "volume=" + volume +
					   ", percentFee=" + percentFee +
					   '}';
		}
	}
}
