
package org.msqbat.fe.api;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.msqbat.datamodel.api.ion.IonMSqBAT;
import org.msqbat.datamodel.api.peak.FeatureMSqBAT;

import net.sf.kerner.utils.collections.list.UtilList;

public class UtilFeature {

	public static List<IonMSqBAT> getAllMemberIons(final Collection<? extends FeatureMSqBAT> peaks) {
		final List<Collection<IonMSqBAT>> result = peaks.stream().map(t -> {
			return t.getIons();
		}).collect(Collectors.toList());
		return UtilList.append(result);
	}
}
