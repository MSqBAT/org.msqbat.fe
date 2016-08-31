package org.msqbat.fe.api;

import org.msqbat.datamodel.api.sample.SampleIons;
import org.msqbat.fe.api.exception.ExceptionFeatureExtraction;

public interface FeatureTightener {

	SampleIons tighten(SampleIons sample) throws ExceptionFeatureExtraction;

}
