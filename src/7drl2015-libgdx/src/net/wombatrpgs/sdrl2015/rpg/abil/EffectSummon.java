/**
 *  EffectSummon.java
 *  Created on Mar 14, 2015 10:19:10 PM for project 7drl2015-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.ArrayList;
import java.util.List;

import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyDefinition;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent;
import net.wombatrpgs.sdrlschema.rpg.RaceMDO;
import net.wombatrpgs.sdrlschema.rpg.SpeciesMDO;
import net.wombatrpgs.sdrlschema.rpg.UnitMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.EffectSummonMDO;

/**
 * Summon minions.
 */
public class EffectSummon extends AbilEffect {
	
	protected EffectSummonMDO mdo;

	/**
	 * Creates an effect given data, ability.
	 * @param	mdo				The data to generate from
	 * @param	abil			The ability to generate for
	 */
	public EffectSummon(EffectSummonMDO mdo, Ability abil) {
		super(mdo, abil);
		this.mdo = mdo;
	}

	/** @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#getTactic() */
	@Override public TacticType getTactic() { return TacticType.SUPPORT; }

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#aiShouldUse
	 * (net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent)
	 */
	@Override
	public boolean aiShouldUse(EnemyEvent actor) {
		return super.aiShouldUse(actor)
				&& abil.getUsed() < abil.getUses(actor.getUnit())
				&& actor.inLoS(MGlobal.hero);
	}

	/**
	 * @see net.wombatrpgs.sdrl2015.rpg.abil.AbilEffect#internalAct(java.util.List)
	 */
	@Override
	protected void internalAct(List<GameUnit> targets) {
		// hacked there and back
		EnemyEvent enemy = (EnemyEvent) actor;
		SpeciesMDO species = enemy.getSpeciesMDO();
		List<RaceMDO> races = new ArrayList<RaceMDO>();
		List<UnitMDO> units = new ArrayList<UnitMDO>();
		
		for (String key : mdo.race) {
			races.add(MGlobal.data.getEntryFor(key, RaceMDO.class));
		}
		for (String key : mdo.unit) {
			units.add(MGlobal.data.getEntryFor(key, UnitMDO.class));
		}
		if (races.isEmpty()) races.add(enemy.getRaceMDO());
		if (units.isEmpty()) units.add(enemy.getUnitMDO());
		
		List<EnemyDefinition> definitions = new ArrayList<EnemyDefinition>();
		for (RaceMDO race : races) {
			for (UnitMDO unit : units) {
				definitions.add(new EnemyDefinition(species, race, unit));
			}
		}
		
		int toSummon = mdo.summonMin;
		if (mdo.summonMax - mdo.summonMin > 0) {
			toSummon += MGlobal.rand.nextInt(mdo.summonMax - mdo.summonMin);
		}
		for (int i = 0; i < toSummon; i += 1) {
			EnemyDefinition def = definitions.get(MGlobal.rand.nextInt(definitions.size()));
			EnemyEvent summon = def.instantiate();
			MGlobal.assetManager.loadAsset(summon, summon.getName());
			summon.spawnNear(actor);
			if (MGlobal.hero.inLoS(summon)) {
				GameUnit.out().msg(summon.getName() + " appears.");
			}
		}
	}

}
