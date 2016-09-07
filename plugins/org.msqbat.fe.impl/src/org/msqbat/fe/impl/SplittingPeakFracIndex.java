package org.msqbat.fe.impl;

import org.msqbat.datamodel.api.provider.ProviderScanNumber;

public class SplittingPeakFracIndex extends SplittingAbstract<ProviderScanNumber, Integer> {

	public SplittingPeakFracIndex(final ProviderScanNumber peak) {
		super(peak, peak.getScanNumber());
	}

	@Override
	public int compareTo(final Splitting<ProviderScanNumber, Integer> o) {
		return Integer.valueOf(getSecond()).compareTo(Integer.valueOf(o.getSecond()));
	}

}
