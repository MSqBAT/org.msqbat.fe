package org.msqbat.fe.impl;

import org.msqbat.datamodel.api.ion.IonMSqBAT;

public class SplittingPeakFracIndex extends SplittingAbstract<IonMSqBAT, Integer> {

	public SplittingPeakFracIndex(final IonMSqBAT peak) {
		super(peak, peak.getScanNumber());
	}

	@Override
	public int compareTo(final Splitting<IonMSqBAT, Integer> o) {
		return Integer.valueOf(getSecond()).compareTo(Integer.valueOf(o.getSecond()));
	}

}
