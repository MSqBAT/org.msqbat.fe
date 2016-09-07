package org.msqbat.fe.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.msqbat.datamodel.api.ion.IonMSqBAT;
import org.msqbat.datamodel.api.ion.TransformerIon2Mz;
import org.msqbat.datamodel.api.ion.UtilIon;
import org.msqbat.datamodel.api.peak.FeatureMSqBAT;
import org.msqbat.datamodel.api.sample.SampleIons;
import org.msqbat.datamodel.impl.PeakSimple;
import org.msqbat.datamodel.impl.SampleImpl;
import org.msqbat.fe.api.FeatureTightener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kerner.utils.collections.list.UtilList;
import net.sf.kerner.utils.math.UtilMath;

public class TightenerMass implements FeatureTightener {

	public final static double DEFAULT_MAX_MASS_SHIFT = 50;

	public final static boolean DEFAULT_PPM = true;

	private final static Logger log = LoggerFactory.getLogger(TightenerMass.class);

	private double massShift;

	private boolean ppm;

	public TightenerMass() {
		massShift = DEFAULT_MAX_MASS_SHIFT;
		ppm = DEFAULT_PPM;
	}

	public TightenerMass(final double massShift, final boolean ppm) {
		this.ppm = ppm;
		this.massShift = massShift;
	}

	public synchronized double getMassShift() {
		return massShift;
	}

	public synchronized boolean isPpm() {
		return ppm;
	}

	public synchronized boolean needsToTighten(final Collection<? extends IonMSqBAT> peaks) {
		IonMSqBAT last = null;
		for (final IonMSqBAT p : peaks) {
			if (last != null) {
				final double delta = UtilIon.getDeltaMass(last.getMz(), p.getMz(), ppm);
				if (delta > massShift) {
					return true;
				}
			}
			last = p;
		}
		return false;
	}

	public synchronized TightenerMass setMassShift(final double massShift) {
		this.massShift = massShift;
		return this;
	}

	public synchronized TightenerMass setPpm(final boolean ppm) {
		this.ppm = ppm;
		return this;
	}

	public synchronized List<FeatureMSqBAT> tighten(final FeatureMSqBAT f) {
		final List<IonMSqBAT> result1 = UtilList.newList();
		final List<IonMSqBAT> result2 = UtilList.newList();
		if (needsToTighten(f.getIons())) {
			final List<Double> masses = new TransformerIon2Mz().transformCollection(f.getIons());
			final double minMass = UtilMath.getMin(masses);
			final double maxMass = UtilMath.getMax(masses);
			final double massRange = maxMass - minMass;
			final double massBorder = minMass + massRange / 2;
			for (final IonMSqBAT p : f.getIons()) {
				if (p.getMz() < massBorder) {
					p.setPeak(null);
					result1.add(p);
				} else {
					p.setPeak(null);
					result2.add(p);
				}
			}
			if (result1.isEmpty() || result2.isEmpty()) {
				throw new RuntimeException();
			}
			final List<FeatureMSqBAT> result = new ArrayList<FeatureMSqBAT>(2);
			result.add(new PeakSimple(result1));
			result.add(new PeakSimple(result2));
			return result;
		} else {
			final List<FeatureMSqBAT> result = new ArrayList<FeatureMSqBAT>(1);
			result.add(f);
			return result;
		}

	}

	@Override
	public synchronized SampleIons tighten(final SampleIons sample) {
		if (log.isDebugEnabled()) {
			log.debug("tightening using massShift: " + getMassShift() + ",ppm: " + isPpm());
		}
		final SampleImpl sampleNew = new SampleImpl();
		sampleNew.setName(sample.getName() + "-tm");

		final List<List<FeatureMSqBAT>> peaks = sample.getIons().stream().map(p -> tighten((FeatureMSqBAT) p))
				.collect(Collectors.toList());
		peaks.stream().forEach(p -> sampleNew.addIons(p));

		return sampleNew;

	}
}
