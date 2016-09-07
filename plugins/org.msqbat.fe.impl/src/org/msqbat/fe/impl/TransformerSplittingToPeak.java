package org.msqbat.fe.impl;

import org.msqbat.datamodel.api.ion.IonMSqBAT;

import net.sf.kerner.utils.collections.list.AbstractTransformingListFactory;

public class TransformerSplittingToPeak extends AbstractTransformingListFactory<Splitting<IonMSqBAT, ?>, IonMSqBAT> {

    @Override
    public IonMSqBAT transform(final Splitting<IonMSqBAT, ?> element) {
        return element.getFirst();
    }

}
