/**
 *  AbilEffect.java
 *  Created on Oct 18, 2013 4:40:25 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.List;

import net.wombatrpgs.sdrl2015.maps.Level;
import net.wombatrpgs.sdrl2015.rpg.CharacterEvent;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent;
import net.wombatrpgs.sdrl2015.rpg.travel.Step;
import net.wombatrpgs.sdrl2015.rpg.travel.StepWait;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityEffectMDO;

/**
 * This is the core thing behind an ability.
 */
public abstract class AbilEffect {
	
	protected AbilityEffectMDO mdo;
	protected Ability abil;
	protected CharacterEvent actor;
	protected Level parent;
	
	/**
	 * Creates a new effect from data for an ability. Also sets some helpful
	 * fields, for convenience. This will probably be the source of an
	 * irritating bug somewhere down the road. FAKEEDIT: caught it early! No
	 * longer does this, it's done somewhere else.
	 * @param	mdo				The mdo to generate from
	 * @param	abil			The parent ability
	 */
	public AbilEffect(AbilityEffectMDO mdo, Ability abil) {
		this.mdo = mdo;
		this.abil = abil;
	}
	
	/**
	 * Returns the physical animation of this effect. By default, returns
	 * nothing. This is where those cool fire effects should go!
	 * @return					The step animation of this effect
	 */
	public Step getStep() {
		return new StepWait(abil.getActor());
	}
	
	/**
	 * Should the AI use this ability, if targets exist? Apply extra conditions
	 * and maybe use the RNG. By default returns true.
	 * @return					True if AI should use when valid.
	 */
	public boolean aiShouldUse(EnemyEvent actor) {
		return true;
	}
	
	/**
	 * Wrapper for an internal abstract method. Performs the universal
	 * abileffect functionality, which should be minimal. The fancy stuff
	 * belongs in the Ability owner.
	 * @param	targets			The dopes to affect
	 */
	final public void act(List<GameUnit> targets) {
		this.actor = abil.getActor();
		this.parent = actor.getParent();
		internalAct(targets);
	}
	
	/**
	 * Determines how the AI uses this effect.
	 * @return					The tactic this effect accomplishes
	 */
	public abstract TacticType getTactic();
	
	/**
	 * Returns the numerical level of the parent ability as known by the owner.
	 * @return					The level of this ability
	 */
	protected int getLevel() {
		return actor.getUnit().getAbilityLevel(abil.getKey());
	}
	
	/**
	 * Actually perform the ability on some targets.
	 * @param	targets			The dopes to affect
	 */
	protected abstract void internalAct(List<GameUnit> targets);

}
