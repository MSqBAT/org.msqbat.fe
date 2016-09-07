package org.msqbat.fe.impl;

import net.sf.kerner.utils.pair.PairImpl;

public abstract class SplittingAbstract<O, V> extends PairImpl<O, V> implements Splitting<O, V> {

    public SplittingAbstract() {
        super();

    }

    public SplittingAbstract(final O first) {
        super(first);
    }

    public SplittingAbstract(final O first, final V second) {
        super(first, second);
    }

    public SplittingAbstract(final SplittingAbstract<O, V> template) {
        super(template);

    }

    @Override
    public String toString() {
        return getSecond().toString();
    }

}
