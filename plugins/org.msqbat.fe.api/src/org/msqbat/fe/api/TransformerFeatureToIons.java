package org.msqbat.fe.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.msqbat.datamodel.api.ion.IonMSqBAT;
import org.msqbat.datamodel.api.peak.FeatureMSqBAT;

import net.sf.kerner.utils.collections.list.TransformerList;
import net.sf.kerner.utils.transformer.Transformer;

public class TransformerFeatureToIons
		implements Transformer<IonMSqBAT, List<IonMSqBAT>>, TransformerList<IonMSqBAT, IonMSqBAT> {

	public final static boolean DEFAULT_INCLUDE_FEATURE = false;

	private boolean includeFeature = DEFAULT_INCLUDE_FEATURE;

	public synchronized boolean isIncludeFeature() {
		return includeFeature;
	}

	public synchronized TransformerFeatureToIons setIncludeFeature(final boolean includeFeature) {
		this.includeFeature = includeFeature;
		return this;
	}

	/**
	 * Returned list is not backed by {@link FeatureMSqBAT#getIons()}.
	 */
	@Override
	public List<IonMSqBAT> transform(final IonMSqBAT element) {

		if (element instanceof FeatureMSqBAT) {
			final List<IonMSqBAT> result = new ArrayList<>(((FeatureMSqBAT) element).getIons());
			if (isIncludeFeature()) {
				result.add(element);
			}
			return result;
		} else {
			return Arrays.asList(element);
		}
	}

	@Override
	public List<IonMSqBAT> transformCollection(final Collection<? extends IonMSqBAT> element) {
		final List<IonMSqBAT> result = new ArrayList<>();
		for (final IonMSqBAT ion : element) {
			result.addAll(transform(ion));
		}
		return result;
	}

}
