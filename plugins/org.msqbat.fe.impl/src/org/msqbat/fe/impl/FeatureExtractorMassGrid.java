
package org.msqbat.fe.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.msqbat.datamodel.api.ComparatorMzIntensityScanNumber;
import org.msqbat.datamodel.api.ion.ComparatorIon;
import org.msqbat.datamodel.api.ion.IonMSqBAT;
import org.msqbat.datamodel.api.peak.FeatureMSqBAT;
import org.msqbat.datamodel.api.sample.SampleIons;
import org.msqbat.datamodel.impl.PeakSimple;
import org.msqbat.datamodel.impl.SampleImpl;
import org.msqbat.datamodel.impl.ion.IonImpl;
import org.msqbat.fe.api.FeatureExtractor;
import org.msqbat.fe.api.SettingsFeatureExtraction;
import org.msqbat.fe.api.UtilFeature;
import org.msqbat.fe.api.exception.ExceptionFeatureExtraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Range;

import net.sf.kerner.utils.collections.ClonerImpl;
import net.sf.kerner.utils.collections.list.UtilList;
import net.sf.kerner.utils.exception.ExceptionRuntimeProperty;
import net.sf.kerner.utils.progress.ProgressMonitor;

public class FeatureExtractorMassGrid implements FeatureExtractor {

	private final static Logger log = LoggerFactory.getLogger(FeatureExtractorMassGrid.class);

	static Multimap<Range<Double>, IonMSqBAT> buildMapRangeToPeak(final Set<Range<Double>> ranges,
			final NavigableSet<IonMSqBAT> ions) {

		// Do not generalize to ProviderMz, since (Tree)Set<ProvderMz> only will
		// fail

		final Multimap<Range<Double>, IonMSqBAT> result = MultimapBuilder.hashKeys()
				.treeSetValues(new ComparatorMzIntensityScanNumber<>()).build();
		for (final Range<Double> range : ranges) {
			final IonMSqBAT lower = new IonImpl(range.lowerEndpoint(), 1);
			final IonMSqBAT upper = new IonImpl(range.upperEndpoint(), 1);
			result.putAll(range, ions.subSet(lower, true, upper, true));
		}
		return result;
	}

	public static Set<Range<Double>> buildRanges() {
		// ZeroPositiveDoubleRange(130.5655, 10254.6250, 1.0005)
		final Set<Range<Double>> result = new HashSet<>();
		double dLast = 130.5655;
		for (double d = dLast; d < 10254.6250; d += 1.0005) {
			if (dLast == d) {
				// skip first
				continue;
			}
			result.add(Range.closed(dLast, d));
			dLast = d;

		}
		return result;
	}

	private ProgressMonitor monitor;

	private final SettingsFeatureExtraction settings;

	public FeatureExtractorMassGrid(final SettingsFeatureExtraction settings) {
		super();
		this.settings = settings;

	}

	@Override
	public SampleIons extractFeatures(final SampleIons sample) throws ExceptionFeatureExtraction {
		if (log.isInfoEnabled()) {
			log.info("Extract Peaks from " + sample.getName());
		}

		final SampleImpl sampleNew = new SampleImpl();
		sampleNew.setName(sample.getName() + "-pe");
		final TreeSet<IonMSqBAT> ions = new TreeSet<IonMSqBAT>(new ComparatorIon<IonMSqBAT>());
		final int sizeBefore = sample.getIons().size();
		ions.addAll(new ClonerImpl<IonMSqBAT>().cloneList(sample.getIons()));
		final int sizeAfter = ions.size();
		if (sizeBefore != sizeAfter) {
			throw new RuntimeException("Lost ions " + sizeBefore + "<>" + sizeAfter);
		}
		final ArrayList<FeatureMSqBAT> list = new ArrayList<>(extractPeaks(ions));
		// Collections.sort(list, new ComparatorProviderScanNumber());
		sampleNew.addIons(list);
		return sampleNew;
	}

	public Collection<FeatureMSqBAT> extractPeaks(final NavigableSet<IonMSqBAT> ions)
			throws ExceptionFeatureExtraction {
		final Collection<FeatureMSqBAT> result = new LinkedList<>();
		// assign peaks to mass ranges
		final Multimap<Range<Double>, IonMSqBAT> mapRangeToPeaks = buildMapRangeToPeak(buildRanges(), ions);
		for (final Entry<Range<Double>, Collection<IonMSqBAT>> entry : mapRangeToPeaks.asMap().entrySet()) {

			final FeatureMSqBAT p = new PeakSimple(UtilList.cast(entry.getValue()));
			result.add(p);

		}

		if (log.isDebugEnabled()) {
			log.debug("found " + mapRangeToPeaks.values().size() + " peaks");
		}

		final int sizeBefore = ions.size();
		final int sizeAfter = UtilFeature.getAllMemberIons(result).size();

		final Properties p = new Properties();
		p.put("sizeBefore", sizeBefore);
		p.put("sizeAfter", sizeAfter);

		if (sizeBefore != sizeAfter) {
			throw new ExceptionRuntimeProperty(p);
		}

		return result;
	}

	@Override
	public void setMonitor(final net.sf.kerner.utils.progress.ProgressMonitor progressMonitor) {

		this.monitor = progressMonitor;
	}
}
