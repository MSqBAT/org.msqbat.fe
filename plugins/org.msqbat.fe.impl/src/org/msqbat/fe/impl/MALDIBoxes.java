/**********************************************************************
 Copyright (c) 2012-2014 Alexander Kerner. All rights reserved.
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ***********************************************************************/

package org.msqbat.fe.impl;

import java.util.ArrayList;
import java.util.List;

import net.sf.jranges.range.doublerange.RangeDouble;
import net.sf.jranges.range.doublerange.impl.FactoryRangeDoubleZeroPositive;
import net.sf.jranges.range.doublerange.impl.RangeDoubleUtils;
import net.sf.jranges.range.doublerange.impl.ZeroPositiveDoubleRange;

public class MALDIBoxes extends Binning {

    private transient List<RangeDouble> splitRanges;

    public RangeDouble getRange() {
        return new ZeroPositiveDoubleRange(130.5655, 10254.6250, 1.0005);
    }

    @Override
    public List<RangeDouble> getRanges() {
        return split();
    }

    public synchronized List<RangeDouble> split() {
        if (splitRanges == null) {
            splitRanges = new ArrayList<RangeDouble>(RangeDoubleUtils.split(getRange(), 6,
                    new FactoryRangeDoubleZeroPositive()));
        }
        return splitRanges;
    }

}
