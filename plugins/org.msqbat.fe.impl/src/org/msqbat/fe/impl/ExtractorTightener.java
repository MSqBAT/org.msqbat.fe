
package org.msqbat.fe.impl;

import java.util.List;

import org.msqbat.datamodel.api.sample.SampleIons;
import org.msqbat.fe.api.FeatureExtractor;
import org.msqbat.fe.api.FeatureTightener;
import org.msqbat.fe.api.exception.ExceptionFeatureExtraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.kerner.utils.Util;
import net.sf.kerner.utils.collections.list.UtilList;
import net.sf.kerner.utils.progress.ProgressMonitor;

public class ExtractorTightener implements FeatureExtractor {

	private final static Logger log = LoggerFactory.getLogger(ExtractorTightener.class);

	protected FeatureExtractor extractor;

	protected ProgressMonitor monitor;

	protected List<FeatureTightener> tighteners = UtilList.newList();

	public synchronized ExtractorTightener addTightener(final FeatureTightener tightener) {
		tighteners.add(tightener);
		return this;
	}

	@Override
	public SampleIons extractFeatures(final SampleIons sample) throws ExceptionFeatureExtraction {

		Util.checkForNull(sample);

		String msg = "extracting " + sample.getName() + " using " + extractor;

		if (log.isDebugEnabled()) {
			log.debug(msg);
		}
		if (monitor != null) {
			monitor.notifySubtask(msg);
		}

		SampleIons result = extractor.extractFeatures(sample);

		if (log.isDebugEnabled()) {
			log.debug("found " + result.getIons().size() + " peaks");
		}
		for (final FeatureTightener t : tighteners) {
			msg = "tightening using " + t;
			if (log.isDebugEnabled()) {
				log.debug(msg);
			}
			if (monitor != null && monitor.isCancelled()) {
				return null;
			}
			if (monitor != null) {
				monitor.notifySubtask(msg);
			}
			result = t.tighten(result);

			if (log.isDebugEnabled()) {
				log.debug("found " + result.getIons().size() + " peaks");
			}

			if (monitor != null) {
				monitor.worked();
			}

		}
		return result;
	}

	public FeatureExtractor getExtractor() {
		return extractor;
	}

	public List<FeatureTightener> getTighteners() {
		return tighteners;
	}

	public ExtractorTightener setExtractor(final FeatureExtractor extractor) {
		this.extractor = extractor;
		return this;
	}

	@Override
	public void setMonitor(final ProgressMonitor progressMonitor) {
		monitor = progressMonitor;
	}

	public ExtractorTightener setTighteners(final List<FeatureTightener> tighteners) {
		this.tighteners = tighteners;
		return this;
	}

}
