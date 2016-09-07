package org.msqbat.fe.api;

import java.util.Collection;
import java.util.List;

import org.msqbat.datamodel.api.peak.FeatureMSqBAT;
import org.msqbat.fe.api.exception.ExceptionFeatureExtraction;

public interface FeatureSplitter {

    void setCutIndices(List<Integer> cuts);

    Collection<FeatureMSqBAT> split(final FeatureMSqBAT feature) throws ExceptionFeatureExtraction;

}
