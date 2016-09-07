package org.msqbat.fe.impl;

import org.msqbat.datamodel.api.ion.IonMSqBAT;

import net.sf.kerner.utils.collections.list.AbstractTransformingListFactory;

public class TransformerPeakToSplittingPeakFracIndex extends
        AbstractTransformingListFactory<IonMSqBAT, SplittingPeakFracIndex> {

    @Override
    public SplittingPeakFracIndex transform(final IonMSqBAT element) {
        return new SplittingPeakFracIndex(element);
    }

}
