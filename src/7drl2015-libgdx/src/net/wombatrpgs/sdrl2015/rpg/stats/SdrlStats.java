/**
 *  SagaStats.java
 *  Created on Apr 2, 2014 10:31:24 PM for project saga-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.stats;

import java.util.Arrays;
import java.util.Collection;

import net.wombatrpgs.sdrlschema.rpg.stats.FlagStatLinkable;
import net.wombatrpgs.sdrlschema.rpg.stats.NumericStatLinkable;
import net.wombatrpgs.sdrlschema.rpg.stats.NumericStatModMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.Stat;
import net.wombatrpgs.sdrlschema.rpg.stats.Flag;
import net.wombatrpgs.sdrlschema.rpg.stats.StatEntryMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.StatModMDO;
import net.wombatrpgs.sdrlschema.rpg.stats.StatSetMDO;

/**
 * The SaGa version of the MGN stats. Theoretically it's json-serializable.
 */
public class SdrlStats extends StatEnumLink {
	
	/**
	 * Creates a stats set with all default values.
	 */
	public SdrlStats() {
		
	}
	
	/**
	 * Creates a new set of stats from an existing stat set.
	 * @param	mdo				The data to read from
	 */
	public SdrlStats(StatSetMDO mdo) {
		setStat(Stat.MHP,	mdo.mhp	);
		setStat(Stat.HP,	mdo.hp	);
		setStat(Stat.MMP,	mdo.mmp	);
		setStat(Stat.MP,	mdo.mp	);
		setStat(Stat.MSP,	mdo.msp	);
		setStat(Stat.SP,	mdo.sp	);
		setStat(Stat.PV,	mdo.pv	);
		setStat(Stat.DV,	mdo.dv	);
		setStat(Stat.SPEED,	mdo.speed);
		setStat(Stat.VISION,mdo.vision);
		updateFlags(Arrays.asList(mdo.flags), true);
	}
	
	/**
	 * Creates a new set of stats from existing numeric-only list. Null works.
	 * @param	mdo					The data to read from, or null
	 */
	public SdrlStats(NumericStatModMDO mdo) {
		if (mdo != null) {
			for (StatEntryMDO entryMDO : mdo.stats) {
				setStat(entryMDO.stat, entryMDO.value);
			}
		}
	}
	
	/**
	 * Creates a new set of stats from an existing stat list. Null is fine too.
	 * @param	mdo				The data to read from, or null
	 */
	public SdrlStats(StatModMDO mdo) {
		if (mdo != null) {
			for (StatEntryMDO entryMDO : mdo.stats) {
				setStat(entryMDO.stat, entryMDO.value);
			}
			updateFlags(Arrays.asList(mdo.flags), true);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.rpg.StatEnumLink#numerics()
	 */
	@Override
	protected Collection<? extends NumericStatLinkable> numerics() {
		return Arrays.asList(Stat.values());
	}

	/**
	 * @see net.wombatrpgs.mgne.rpg.StatEnumLink#flags()
	 */
	@Override
	protected Collection<? extends FlagStatLinkable> flags() {
		return Arrays.asList(Flag.values());
	}

}
