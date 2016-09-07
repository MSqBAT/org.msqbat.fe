package org.msqbat.fe.impl;

public class DynamicGapsStrategyLog implements DynamicGapsStrategy {

    
    public DynamicGapsStrategyLog() {
        super();
    }

    @Override
    public Integer transform(Integer growingBoxSize) {
        if (growingBoxSize < 0) {
            throw new IllegalArgumentException("for argument " + growingBoxSize);
        }
        if (growingBoxSize == 1) {
            return 1;
        }
        long result = Math.round(Math.log(growingBoxSize));
        return (int) result;
    }

}
