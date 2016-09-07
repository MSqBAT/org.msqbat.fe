package org.msqbat.fe.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CutValidatorDefault implements CutValidator {

    private final static Logger log = LoggerFactory.getLogger(CutValidatorDefault.class);

    static final String INVALID = "invalid cuts: %s";

    static final String VALID = "valid cuts: %s";

    private static void logInvalid(final Collection<Integer> cuts) {
        if (log.isDebugEnabled())
            log.debug(String.format(INVALID, cuts));
    }

    @Override
    public boolean valid(final Collection<Integer> cuts) {
        if (cuts == null) {
            logInvalid(cuts);
            return false;
        }
        if (cuts.isEmpty()) {
            return true;
        }
        if (cuts.contains(Integer.valueOf(0))) {
            logInvalid(cuts);
            return false;
        }
        return true;
    }

}
