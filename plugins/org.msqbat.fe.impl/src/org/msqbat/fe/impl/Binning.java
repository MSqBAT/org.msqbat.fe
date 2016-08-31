package org.msqbat.fe.impl;

import java.util.Collection;
import java.util.List;

import org.msqbat.datamodel.api.provider.ProviderMz;

import net.sf.jranges.range.doublerange.RangeDouble;
import net.sf.kerner.utils.collections.map.MapList;

public abstract class Binning {

	public static RangeDouble findRangeMZ(final ProviderMz peak, final Collection<? extends RangeDouble> ranges) {

		for (final RangeDouble r : ranges) {
			if (r.includes(peak.getMz())) {
				return r;
			}
		}
		throw new RuntimeException("could not find valid range for " + peak);
	}

	public static MapList<RangeDouble, ProviderMz> getBinningMZ(final Collection<? extends RangeDouble> ranges,
			final Collection<? extends ProviderMz> peaks) {

		final MapList<RangeDouble, ProviderMz> result = new MapList<RangeDouble, ProviderMz>();
		for (final ProviderMz p : peaks) {
			result.put(findRangeMZ(p, ranges), p);
		}
		return result;
	}

	public abstract List<RangeDouble> getRanges();
}
