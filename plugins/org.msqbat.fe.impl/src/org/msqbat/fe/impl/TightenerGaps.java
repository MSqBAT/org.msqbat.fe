package org.msqbat.fe.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.msqbat.datamodel.api.ComparatorScanNumber;
import org.msqbat.datamodel.api.ion.IonMSqBAT;
import org.msqbat.datamodel.api.peak.FeatureMSqBAT;
import org.msqbat.datamodel.api.sample.SampleIons;
import org.msqbat.datamodel.impl.PeakSimple;
import org.msqbat.datamodel.impl.SampleImpl;
import org.msqbat.fe.api.FeatureTightener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kerner.utils.collections.list.UtilList;

public class TightenerGaps implements FeatureTightener {

	public final static boolean DEFAULT_DYNAMIC_GAPS = false;
	public final static int DEFAULT_MAX_GAP_SIZE = 3;
	private final static Logger log = LoggerFactory.getLogger(TightenerGaps.class);
	private boolean dynamicGaps;
	private DynamicGapsStrategy dynamicGapsStrategy = new DynamicGapsStrategyLogRoundUp();
	private int maxGapSize;

	public TightenerGaps() {
		dynamicGaps = DEFAULT_DYNAMIC_GAPS;
		maxGapSize = DEFAULT_MAX_GAP_SIZE;
	}

	public TightenerGaps(final int maxGapSize, final boolean dynamicGaps) {
		this.maxGapSize = maxGapSize;
		this.dynamicGaps = dynamicGaps;
	}

	public synchronized DynamicGapsStrategy getDynamicGapsStrategy() {

		return dynamicGapsStrategy;
	}

	private synchronized int getGapSize(final int growingBoxSize) {

		if (dynamicGaps) {
			final int result = dynamicGapsStrategy.transform(growingBoxSize);
			if (log.isDebugEnabled()) {
				log.debug("dynamic gap for " + growingBoxSize + " elements " + result);
			}
			return result;
		} else {
			return maxGapSize;
		}
	}

	public synchronized int getMaxGapSize() {

		return maxGapSize;
	}

	public synchronized boolean isDynamicGaps() {

		return dynamicGaps;
	}

	public synchronized void setDynamicGaps(final boolean dynamicGaps) {

		this.dynamicGaps = dynamicGaps;
	}

	public synchronized void setDynamicGapsStrategy(final DynamicGapsStrategy dynamicGapsStrategy) {

		this.dynamicGapsStrategy = dynamicGapsStrategy;
	}

	public synchronized void setMaxGapSize(final int maxGapSize) {

		this.maxGapSize = maxGapSize;
	}

	public synchronized List<FeatureMSqBAT> tighten(final FeatureMSqBAT f) {

		final List<FeatureMSqBAT> result = UtilList.newList();
		final List<IonMSqBAT> members = new ArrayList<IonMSqBAT>(f.getIons());
		Collections.sort(members, new ComparatorScanNumber<>());
		List<IonMSqBAT> newOnes = UtilList.newList();
		IonMSqBAT last = null;
		for (final IonMSqBAT p : members) {
			p.setPeak(null);
			if (last != null) {
				final int gapZ = p.getScanNumber() - last.getScanNumber();
				if (gapZ > getGapSize(gapZ)) {
					if (newOnes.isEmpty()) {
						throw new RuntimeException();
					}
					result.add(new PeakSimple(newOnes));
					newOnes = UtilList.newList();
				}
			}
			newOnes.add(p);
			last = p;
		}
		if (!newOnes.isEmpty()) {
			result.add(new PeakSimple(newOnes));
		}
		return result;
	}

	@Override
	public synchronized SampleIons tighten(final SampleIons sample) {
		if (log.isDebugEnabled()) {
			log.debug("tightening using gapSize: " + getMaxGapSize());
		}
		final SampleImpl sampleNew = new SampleImpl();
		sampleNew.setName(sample.getName() + "-tg");
		final List<List<FeatureMSqBAT>> peaks = sample.getIons().stream().map(p -> tighten((FeatureMSqBAT) p))
				.collect(Collectors.toList());
		peaks.stream().forEach(p -> sampleNew.addIons(p));

		return sampleNew;
	}
}
