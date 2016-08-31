package org.msqbat.fe.api;

import org.msqbat.datamodel.api.sample.SampleIons;
import org.msqbat.fe.api.exception.ExceptionFeatureExtraction;

import net.sf.kerner.utils.progress.ProgressMonitor;

public interface FeatureExtractor {

	SampleIons extractFeatures(SampleIons sample) throws ExceptionFeatureExtraction;

	void setMonitor(ProgressMonitor progressMonitor);
}
