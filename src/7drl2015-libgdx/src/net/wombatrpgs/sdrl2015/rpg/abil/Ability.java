/**
 *  Ability.java
 *  Created on Oct 18, 2013 4:16:28 AM for project mrogue-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.rpg.abil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;

import net.wombatrpgs.sdrl2015.core.Constants;
import net.wombatrpgs.sdrl2015.core.FinishListener;
import net.wombatrpgs.sdrl2015.core.MGlobal;
import net.wombatrpgs.sdrl2015.core.Queueable;
import net.wombatrpgs.sdrl2015.graphics.Graphic;
import net.wombatrpgs.sdrl2015.graphics.effects.AbilFX;
import net.wombatrpgs.sdrl2015.graphics.effects.AbilFxFactory;
import net.wombatrpgs.sdrl2015.io.CommandListener;
import net.wombatrpgs.sdrl2015.io.command.CMapDirections;
import net.wombatrpgs.sdrl2015.maps.MapThing;
import net.wombatrpgs.sdrl2015.maps.events.Cursor;
import net.wombatrpgs.sdrl2015.maps.events.MapEvent;
import net.wombatrpgs.sdrl2015.rpg.CharacterEvent;
import net.wombatrpgs.sdrl2015.rpg.GameUnit;
import net.wombatrpgs.sdrl2015.rpg.CharacterEvent.RayCheck;
import net.wombatrpgs.sdrl2015.rpg.act.Action;
import net.wombatrpgs.sdrl2015.rpg.ai.TacticType;
import net.wombatrpgs.sdrl2015.rpg.enemy.EnemyEvent;
import net.wombatrpgs.sdrl2015.rpg.item.Item;
import net.wombatrpgs.sdrl2015.rpg.travel.Step;
import net.wombatrpgs.sdrlschema.io.data.InputCommand;
import net.wombatrpgs.sdrlschema.maps.data.EightDir;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityMDO;
import net.wombatrpgs.sdrlschema.rpg.abil.AbilityTargetType;
import net.wombatrpgs.sdrlschema.rpg.data.LevelingAttribute;
import net.wombatrpgs.sdrlschema.rpg.data.Relation;

/**
 * An ability is a special sort of action. It can be used by a character or a
 * hero, and it's not necessarily part of an AI routine. Actually it's kind of
 * a typical thing then... it's just constructed from a special ability MDO.
 * 
 * 7DRL: warning, kind of a mess... the main issue here is that an issue is
 * constructed with a specific actor in mind, and ability targeting is not
 * well designed
 */
public class Ability extends Action implements Queueable, CommandListener {
	
	protected AbilityMDO mdo;
	protected AbilEffect effect;
	protected EnumSet<LevelingAttribute> levelingAttributes;
	protected Item item;
	protected RayCheck check;
	protected MapEvent firstHit;
	protected List<GameUnit> targets;
	protected List<GameUnit> lastTargets;
	protected AbilFX fx;
	protected List<Queueable> assets;
	protected Graphic icon;
	protected Cursor targetCursor;
	protected int used;
	protected boolean blocking;
	
	/**
	 * Creates a new ability for a particular actor from data.
	 * @param	actor			The actor using the ability
	 * @param	granter			The item granting the ability, or null
	 * @param	mdo				The create data for the ability
	 */
	public Ability(CharacterEvent actor, Item granter, AbilityMDO mdo) {
		super(actor);
		this.mdo = mdo;
		this.item = granter;
		this.effect = AbilEffectFactory.createEffect(mdo.warhead.key, this);
		this.assets = new ArrayList<Queueable>();
		blocking = false;
		
		levelingAttributes = EnumSet.noneOf(LevelingAttribute.class);
		levelingAttributes.addAll(Arrays.asList(mdo.leveling));
		
		targetCursor = new Cursor();
		assets.add(targetCursor);
		
		if (MapThing.mdoHasProperty(mdo.fx) && MGlobal.graphics.isShaderEnabled()) {
			fx = AbilFxFactory.createFX(mdo.fx, this);
			assets.add(fx);
		}
		icon = new Graphic(Constants.ITEMS_DIR, mdo.icon);
		assets.add(icon);
		
		final CharacterEvent actor2 = actor;
		switch (mdo.target) {
		case PROJECTILE:
			check = actor.new RayCheck() {
				@Override public boolean bad(int tileX, int tileY) {
					if (!actor2.getParent().isTilePassable(actor2, tileX, tileY)) {
						return false;
					}
					for (MapEvent event : actor2.getParent().getEventsAt(tileX, tileY)) {
						if (!event.isPassable() && event != actor2) {
							firstHit = event;
							return true;
						}
					}
					return false;
				}
			};
			break;
		case BALL: case BEAM:
			check = actor.new RayCheck() {
				@Override public boolean bad(int tileX, int tileY) {
					if (!actor2.getParent().isChipPassable(actor2, tileX, tileY)) {
						return true;
					}
					return false;
				}
			};
			break;
		default:
			// it's fine, we don't need raycasting
		}
	}
	
	/** @return The in-game name of this ability */
	public String getName() { return mdo.name; }
	
	/** @return The in-game desc of this ability */
	public String getDescription() { return mdo.abilDesc; }
	
	/** @return The step animation of this ability */
	public Step getStep() { return effect.getStep(); }
	
	/** @return All the units currently targeted by this ability */
	public List<GameUnit> getTargets() { return targets; }
	
	/** @return The range of this ability, in fractional tiles (radius) */
	public Float getRange() {
		Float range = mdo.range;
		if (isLeveled(LevelingAttribute.INCREASE_RANGE)) {
			range += getLevel();
		}
		return range;
	}
	
	/** @return This ability's targeting type */
	public AbilityTargetType getType() { return mdo.target; }
	
	/** @return How this ability should look in the UI */
	public Graphic getIcon() { return icon; }
	
	/** @return The class of effect this ability has */
	public Class<? extends AbilEffect> getEffectClass() { return effect.getClass(); }
	
	/** @return The unique ID of this ability's type */
	public String getKey() { return mdo.key; }
	
	/** @return A description of what happens when this abil is leveled */
	public String getLevelText() { return mdo.levelDesc; }
	
	/** @return True if this ability can be leveled at all */
	public boolean canBeLeveled() { return levelingAttributes.size() > 0; }
	
	/** @return The actor's level of this ability */
	public int getLevel() { return actor.getUnit().getAbilityLevel(getKey()); }
	
	/** @return How the AI should use this ability */
	public TacticType getTactic() { return effect.getTactic(); }
	
	/** @return The number of times this ability has been used in the night */
	public int getUsed() { return used; }
	
	/** @return True if this item works via consumables rather than nightly */
	public boolean isConsumableBased() { return item != null && item.getUses() > 0; }
	
	/**
	 * Checks if an attribute is raised when this abil levels.
	 * @param	attribute		The attribute to check
	 * @return					True if that attribute is raised
	 */
	public boolean isLeveled(LevelingAttribute attribute) {
		return levelingAttributes.contains(attribute);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + "(" + mdo.key + ")";
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.rpg.act.Action#baseCost()
	 */
	@Override
	public int baseCost() {
		int cost = mdo.energyCost;
		if (isLeveled(LevelingAttribute.DECREASE_ENERGY)) {
			for (int i = 0; i < getLevel(); i += 1) {
				cost *= .2f;
			}
		}
		return cost;
	}
	
	/**
	 * Looks up the radius of this ability. This will be the same as the range
	 * in most cases, but beams and projectiles have radius 1.5. Right now only
	 * really suited for abilfx.
	 * @return					The radius of this ability
	 */
	public float getRadius() {
		switch (mdo.target) {
		case BALL:
			return mdo.range;
		case USER: case MELEE:
			return 1f;
		case BEAM: case PROJECTILE:
			return 1.5f;
		default:
			return 0;
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (Queueable asset : assets) {
			asset.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mrogue.core.Queueable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (Queueable asset : assets) {
			asset.postProcessing(manager, pass);
		}
	}

	/**
	 * This should only really be called by enemy AI or us from other methods.
	 * @see net.wombatrpgs.mrogue.rpg.act.Action#act()
	 */
	@Override
	final public void act() {
		actor.getUnit().onAbilityUsed(this);
		if (item != null && item.getEquipAbilityKey().equals(getKey())
				&& !actor.getUnit().getEquipment().isEquipped(item)) {
			actor.getUnit().getEquipment().equip(item);
		}
		if (actor != MGlobal.hero) {
			acquireTargets();
		}
		if (MapThing.mdoHasProperty(mdo.fx) &&
				MGlobal.hero.inLoS(actor) &&
				(targets.size() > 0 || mdo.target == AbilityTargetType.BALL)) {
			fxSpawn();
			fx = null;
		}
		effect.act(targets);
		actor.addStep(getStep());
		
		// awful hack for stacking consumables
		used += 1;
		if (isConsumableBased() && used >= item.getUses()) {
			actor.getUnit().getInventory().removeItem(item);
			for (Item item : actor.getUnit().getInventory().getItems()) {
				if (item != this.item && item.getCarryAbilityKey().equals(getKey())) {
					this.item = item;
					resetUses();
					break;
				}
			}
		}
		
		targets = null;
	}
	
	/**
	 * @see net.wombatrpgs.mrogue.io.CommandListener#onCommand
	 * (net.wombatrpgs.mrogueschema.io.data.InputCommand)
	 */
	@Override
	public boolean onCommand(InputCommand command) {
		if (!blocking) {
			MGlobal.reporter.warn("Ability got a command when not blocking: " + command);
			return false;
		}
		if (command == InputCommand.INTENT_CANCEL) {
			unblock();
			return true;
		}
		switch (mdo.target) {
		case MELEE:
			switch (command) {
			case MOVE_NORTH:		meleeTarget(EightDir.NORTH);		break;
			case MOVE_NORTHEAST:	meleeTarget(EightDir.NORTHEAST);	break;
			case MOVE_EAST:			meleeTarget(EightDir.EAST);			break;
			case MOVE_SOUTHEAST:	meleeTarget(EightDir.SOUTHEAST);	break;
			case MOVE_SOUTH:		meleeTarget(EightDir.SOUTH);		break;
			case MOVE_SOUTHWEST:	meleeTarget(EightDir.SOUTHWEST);	break;
			case MOVE_WEST:			meleeTarget(EightDir.WEST);			break;
			case MOVE_NORTHWEST:	meleeTarget(EightDir.NORTHWEST);	break;
			default:													break;
			}
			unblock();
			break;
		default:
			System.out.println("not yet implemented");
		}
		// we always consume commands on our command map
		return true;
	}

	/**
	 * The hero should call this to use this ability. Will check if the player
	 * has provided enough info for us to use this move, and if so, uses it.
	 * @return					True if this ability is no longer blocking
	 */
	public boolean useAndBlock() {
		acquireTargets();
		if (targets != null) {
			lastTargets = targets;
			if (targets.size() > 0 || mdo.target == AbilityTargetType.BALL) {
				MGlobal.hero.actAndWait(this);
			}
			targets = null;
			return true;
		}
		return !blocking;
	}
	
	/**
	 * Spawns a graphical representation of this ability.
	 */
	public void fxSpawn() {
		if (MGlobal.graphics.isShaderEnabled()) {
			if (fx == null) {
				fx = AbilFxFactory.createFX(mdo.fx, this);
				fx.postProcessing(MGlobal.assetManager, 0);
			}
			fx.spawn();
		}
	}
	
	/**
	 * Checks if this ability has any valid targets. Useful for AI.
	 * @return					True if any targets are in range
	 */
	public boolean anyInRange() {
		if (getTactic() == TacticType.RANDOM || getTactic() == TacticType.SUPPORT) return true;
		acquireTargets();
		if (targets == null || targets.size() == 0) return false;
		for (GameUnit target : targets) {
			if (actor.getUnit().getRelationTo(target).attackIfBored) return true;
		}
		return false;
	}
	
	/**
	 * Calculates the number of uses this abilities has for the given unit.
	 * @param	unit			The unit to get uses for
	 * @return					How many times this ability can be used
	 */
	public int getUses(GameUnit unit) {
		if (item != null && item.getUses() > 0) {
			int uses = 0;
			for (Item otherItem : unit.getInventory().getItems()) {
				if (otherItem.getCarryAbilityKey().equals(getKey())) {
					uses += otherItem.getUses();
				}
			}
			return uses - used;
		} else {
			int uses = mdo.uses;
			if (isLeveled(LevelingAttribute.INCREASE_USES)) {
				uses += unit.getAbilityLevel(getKey());
			}
			uses -= unit.getUsesSinceNight(getKey());
			return uses;
		}
	}
	
	/**
	 * Hack for having multiple consumables.
	 */
	public void resetUses() {
		used = 0;
	}
	
	/**
	 * RNG-based check to see if this ability should be used by the AI. At this
	 * point, the ability is known to be valid. The idea is that AI might want
	 * to do something other than spam valid abilities.
	 * @param	actor			The actor doing the acting
	 * @return					True if AI should use this ability now
	 */
	public boolean aiShouldUse(EnemyEvent actor) {
		return (effect.aiShouldUse(actor)) && (MGlobal.rand.nextFloat() < mdo.useChance);
	}
	
	/**
	 * Switches on the ability targeting type and gets all victims affected by
	 * this attack. Sets the resulting list to the corresponding field.
	 * @return					The characters affected by this ability
	 */
	protected void acquireTargets() {
		switch (mdo.target) {
		case USER:
			targets = new ArrayList<GameUnit>();
			targets.add(actor.getUnit());
			break;
		case BALL:
			targets = new ArrayList<GameUnit>();
			for (CharacterEvent chara : actor.getParent().getCharacters()) {
				if (actor.euclideanTileDistanceTo(chara) <= mdo.range &&
						actor.rayExistsTo(chara, check) &&
						chara != actor) {
					targets.add(chara.getUnit());
				}
			}
			break;
		case MELEE:
			if (actor == MGlobal.hero) {
				if (targets != null) {
					return;
				}
				if (!blocking) {
					targets = null;
					MGlobal.levelManager.getScreen().registerCommandListener(this);
					MGlobal.levelManager.getScreen().pushCommandContext(new CMapDirections());
					MGlobal.ui.getNarrator().msg("Enter a direction...");
					blocking = true;
				}
			} else {
				targets = new ArrayList<GameUnit>();
				for (CharacterEvent chara : actor.getParent().getCharacters()) {
					if (actor.euclideanTileDistanceTo(chara) < 1.5 && actor != chara) {
						targets.add(chara.getUnit());
						break;
					}
				}
			}
			break;
		case PROJECTILE: case BEAM:
			if (actor == MGlobal.hero) {
				if (targets != null) {
					return;
				}
				if (!targetCursor.isActive()) {
					targets = null;
					CharacterEvent last = null;
					if (lastTargets != null && lastTargets.size() > 0) {
						last = lastTargets.get(0).getParent();
						if (!actor.inLoS(last) ||
								last.getUnit().isDead() ||
								actor.euclideanTileDistanceTo(last) > mdo.range) {
							last = null;
						}
					}
					if (last == null) {
						for (GameUnit unit : actor.getUnit().getVisibleEnemies()) {
							if (actor.euclideanTileDistanceTo(unit.getParent()) < mdo.range) {
								last = unit.getParent();
								break;
							}
						}
					}
					targetCursor.activate(true);
					targetCursor.setRange(mdo.range);
					if (last != null) {
						targetCursor.setTileX(last.getTileX());
						targetCursor.setTileY(last.getTileY());
						targetCursor.setX(last.getTileX() * last.getParent().getTileWidth());
						targetCursor.setY(last.getTileY() * last.getParent().getTileHeight());
						targetCursor.setLastTarget(last);
					}
					targetCursor.registerFinishListener(new FinishListener() {
						@Override public void onFinish() {
							targets = new ArrayList<GameUnit>();
							if (mdo.target == AbilityTargetType.BEAM) {
								int tileX = actor.getTileX();
								int tileY = actor.getTileY();
								int origX = tileX;
								int origY = tileY;
								while (tileX != targetCursor.getTileX() || tileY != targetCursor.getTileY()) {
									EightDir dir = actor.directionTo(targetCursor);
									tileX += dir.getVector().x;
									tileY += dir.getVector().y;
									for (MapEvent other : actor.getParent().getEventsAt(tileX, tileY)) {
										// hax
										if (other instanceof CharacterEvent) {
											targets.add(((CharacterEvent) other).getUnit());
										}
									}
									actor.setTileX(tileX);
									actor.setTileY(tileY);
								}
								actor.setTileX(origX);
								actor.setTileY(origY);
							} else {
								if (targetCursor.getLastTarget() != null) {
									targets.add(targetCursor.getLastTarget().getUnit());
								}
							}
						}
					});
					MGlobal.ui.getNarrator().msg("Select a target...");
					blocking = true;
				}
			} else {
				targets = new ArrayList<GameUnit>();
				if (getTactic() == TacticType.OFFENSE) {
					for (GameUnit enemy : actor.getUnit().getVisibleEnemies()) {
						if (actor.euclideanTileDistanceTo(enemy.getParent()) <= mdo.range &&
								actor != enemy.getParent() &&
								actor.getUnit().getRelationTo(enemy).attackIfBored) {
							targets.add(enemy);
							break;
						}
					}
				} else {
					for (GameUnit unit : actor.getUnit().getVisibleUnits()) {
						if (actor.euclideanTileDistanceTo(unit.getParent()) <= mdo.range &&
								actor != unit.getParent() &&
								actor.getUnit().getRelationTo(unit) == Relation.ALLIED) {
							targets.add(unit);
							break;
						}
					}
				}
			}
			break;
		default:
			MGlobal.reporter.warn("Unknown ability target type " + mdo.target +
					" for ability + " + mdo.key);
		}
	}
	
	/**
	 * Acquires a target in the specified direction.
	 * @param	dir				The direction to target
	 */
	protected void meleeTarget(EightDir dir) {
		int targetX = (int) (actor.getTileX() + dir.getVector().x);
		int targetY = (int) (actor.getTileY() + dir.getVector().y);
		targets = new ArrayList<GameUnit>();
		for (CharacterEvent chara : actor.getParent().getCharacters()) {
			if (chara.getTileX() == targetX && chara.getTileY() == targetY) {
				targets.add(chara.getUnit());
			}
		}
	}
	
	/**
	 * A fancy way to set blocking to false from true.
	 */
	protected void unblock() {
		MGlobal.levelManager.getScreen().popCommandContext();
		MGlobal.levelManager.getScreen().unregisterCommandListener(this);
		blocking = false;
	}
}
