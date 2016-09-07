package org.msqbat.fe.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.msqbat.datamodel.api.ion.IonMSqBAT;
import org.msqbat.datamodel.api.ion.TransformerIon2Mz;
import org.msqbat.datamodel.api.peak.FeatureMSqBAT;
import org.msqbat.datamodel.impl.PeakSimple;
import org.msqbat.fe.api.FeatureSplitter;
import org.msqbat.fe.api.UtilFeature;
import org.msqbat.fe.api.exception.ExceptionFeatureExtraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kerner.utils.collections.UtilCollection;
import net.sf.kerner.utils.collections.list.UtilList;
import net.sf.kerner.utils.exception.ExceptionRuntimeProperty;

public class FeatureSplitterByFractionIndex implements FeatureSplitter {

	private final static CutValidatorDefault CUT_CALIDATOR = new CutValidatorDefault();

	private final static Logger log = LoggerFactory.getLogger(FeatureSplitterByFractionIndex.class);

	private final static TransformerPeakToSplittingPeakFracIndex TRANSFORMER_PEAK_TO_SPLITTING = new TransformerPeakToSplittingPeakFracIndex();

	private final static TransformerSplittingToPeak TRANSFORMER_SPLITTING_TO_PEAK = new TransformerSplittingToPeak();

	static List<? extends SplittingPeakFracIndex> getLower(final List<? extends SplittingPeakFracIndex> splittings,
			final Integer cutIndex) {
		final List<SplittingPeakFracIndex> lower = UtilList.newList();
		for (@SuppressWarnings("rawtypes")
		final Iterator iterator = splittings.iterator(); iterator.hasNext();) {
			final SplittingPeakFracIndex splittingPeak = (SplittingPeakFracIndex) iterator.next();
			if (splittingPeak.getSecond() < cutIndex) {
				lower.add(splittingPeak);
				iterator.remove();
			} else {
				// ignore
			}
		}
		if (lower.isEmpty()) {
			// TODO: why comparator explicitly?
			final SplittingPeakFracIndex lowest = UtilCollection.getLowest(splittings, (o1, o2) -> o1.compareTo(o2));
			lower.add(lowest);
			splittings.remove(lowest);
		}
		return lower;
	}

	static List<List<? extends SplittingPeakFracIndex>> splitSplittings(
			final List<? extends SplittingPeakFracIndex> splittings, final List<Integer> cutIndices) {

		final List<? extends SplittingPeakFracIndex> splittingsCopy = UtilList.newList(splittings);

		final List<List<? extends SplittingPeakFracIndex>> splittingsNew = UtilList.newList();
		if (cutIndices.isEmpty()) {
			splittingsNew.add(splittings);
		} else {
			for (final Integer index : cutIndices) {
				splittingsNew.add(getLower(splittingsCopy, index));
			}
			splittingsNew.add(splittingsCopy);
		}

		final Collection<SplittingPeakFracIndex> doubleCheck = UtilCollection.newCollection();
		for (final List<? extends SplittingPeakFracIndex> i : splittingsNew) {
			doubleCheck.addAll(i);
		}

		if (!doubleCheck.equals(splittings)) {
			for (final SplittingPeakFracIndex e : UtilCollection.getSymmetricDifference(doubleCheck, splittings)) {
				if (log.isWarnEnabled()) {
					log.warn("element lost: " + e.getFirst());
				}

			}
		}

		return splittingsNew;
	}

	private List<Integer> cutIndices;

	public synchronized List<Integer> getCutIndices() {
		return cutIndices;
	}

	@Override
	public synchronized void setCutIndices(final List<Integer> cutIndices) {
		this.cutIndices = cutIndices;
	}

	@Override
	public synchronized Collection<FeatureMSqBAT> split(final FeatureMSqBAT feature) throws ExceptionFeatureExtraction {
		final Collection<FeatureMSqBAT> result = UtilCollection.newCollection();
		if (UtilCollection.nullOrEmpty(cutIndices)) {
			result.add(feature);
			return result;
		}
		final List<List<IonMSqBAT>> newOnes = split(feature.getIons(), cutIndices);
		for (final List<IonMSqBAT> newMembers : newOnes) {
			if (newMembers.isEmpty()) {
				throw new ExceptionFeatureExtraction();
			}
			result.add(new PeakSimple(newMembers));
		}

		final int sizeBefore = feature.getIons().size();
		final int sizeAfter = UtilFeature.getAllMemberIons(result).size();

		final Properties p = new Properties();
		p.put("sizeBefore", sizeBefore);
		p.put("sizeAfter", sizeAfter);

		if (sizeBefore != sizeAfter) {
			if (log.isWarnEnabled()) {
				log.warn("one fraction lost");
			}
		}

		return result;
	}

	synchronized List<List<IonMSqBAT>> split(List<? extends IonMSqBAT> members, final List<Integer> cutIndices)
			throws ExceptionFeatureExtraction {
		if (!UtilCollection.notNullNotEmpty(members)) {
			throw new IllegalArgumentException("no members");
		}
		members = UtilList.newList(members);
		if (!CUT_CALIDATOR.valid(cutIndices)) {
			throw new IllegalArgumentException(cutIndices.toString());
		}

		final List<List<IonMSqBAT>> result = UtilList.newList();

		if (cutIndices.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug("empty cuts, skipping");
			}
			result.add(new ArrayList<IonMSqBAT>(members));
			return result;
		}

		final List<SplittingPeakFracIndex> splittings = TRANSFORMER_PEAK_TO_SPLITTING.transformCollection(members);

		if (splittings.size() != members.size()) {
			throw new ExceptionFeatureExtraction();
		}

		boolean ok = false;
		for (final SplittingPeakFracIndex s : splittings) {
			if (cutIndices.contains(s.getSecond())) {
				ok = true;
			}
		}

		if (!ok) {
			final Properties p = new Properties();
			p.put("cutindices", cutIndices);
			p.put("splittings", splittings);
			p.put("mz", new TransformerIon2Mz().transformCollection(members));
			throw new ExceptionRuntimeProperty(p);
		}

		for (final List<? extends SplittingPeakFracIndex> s : splitSplittings(splittings, cutIndices)) {
			if (!s.isEmpty()) {
				result.add(TRANSFORMER_SPLITTING_TO_PEAK
						.transformCollection((Collection<? extends Splitting<IonMSqBAT, ?>>) s));
			}
		}

		return result;
	}
}
