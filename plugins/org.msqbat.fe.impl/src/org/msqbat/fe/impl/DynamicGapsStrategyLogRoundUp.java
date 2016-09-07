package org.msqbat.fe.impl;

public class DynamicGapsStrategyLogRoundUp implements DynamicGapsStrategy {

    public final static int DEFAULT_START_GAP_SIZE = 3;

    private int startGapSize;

    public DynamicGapsStrategyLogRoundUp() {
        this(DEFAULT_START_GAP_SIZE);
    }

    public DynamicGapsStrategyLogRoundUp(final int startGapSize) {
        this.startGapSize = startGapSize;
    }

    public int getStartGapSize() {
        return startGapSize;
    }

    public void setStartGapSize(final int startGapSize) {
        this.startGapSize = startGapSize;
    }

    @Override
    public Integer transform(final Integer growingBoxSize) {
        if (growingBoxSize < 0) {
            throw new IllegalArgumentException("for argument " + growingBoxSize);
        }
        final int result = (int) Math.round(Math.log(growingBoxSize));
        return result + startGapSize;
    }
}
