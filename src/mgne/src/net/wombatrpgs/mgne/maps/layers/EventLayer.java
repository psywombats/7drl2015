/**
 *  ObjectLayer.java
 *  Created on Nov 29, 2012 3:46:55 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.mgne.maps.layers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;

import net.wombatrpgs.mgne.core.MGlobal;
import net.wombatrpgs.mgne.maps.Level;
import net.wombatrpgs.mgne.maps.MapThing;
import net.wombatrpgs.mgne.maps.events.MapEvent;

/**
 * A renderable collection of map events, grouped into a layer in a level.
 */
public class EventLayer extends Layer {
	
	protected List<MapEvent> events;
	
	/**
	 * Creates a new object layer with a parent level and no objects.
	 * @param 	parent			The parent level of the layer
	 * @param	layer			The underlying tiled layer
	 * @param	index			The ordinal of this event layer (of event layer)
	 */
	public EventLayer(Level parent) {
		super(parent);
		events = new ArrayList<MapEvent>();
	}

	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#render
	 * (com.badlogic.gdx.graphics.OrthographicCamera)
	 */
	@Override
	public void render(OrthographicCamera camera) {
		parent.getBatch().begin();
		for (MapEvent event : events) {
			event.render(camera);
		}
		parent.getBatch().end();
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.Renderable#queueRequiredAssets
	 * (com.badlogic.gdx.assets.AssetManager)
	 */
	@Override
	public void queueRequiredAssets(AssetManager manager) {
		for (MapEvent event : events) {
			event.queueRequiredAssets(manager);
		}
	}

	/**
	 * @see net.wombatrpgs.mgne.graphics.Renderable#postProcessing
	 * (com.badlogic.gdx.assets.AssetManager, int)
	 */
	@Override
	public void postProcessing(AssetManager manager, int pass) {
		for (MapEvent event : events) {
			event.postProcessing(manager, pass);
		}
	}
	
	/**
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#isLowerChip()
	 */
	@Override
	public boolean isLowerChip() {
		return false;
	}

	/**
	 * Just check if any no-overlap events are in the area.
	 * @see net.wombatrpgs.mgne.maps.layers.Layer#isPassable
	 * (MapEvent, int, int)
	 */
	@Override
	public boolean isPassable(MapEvent actor, final int x, final int y) {
		// TODO: maps: isPassable
		return true;
	}
	
	/** @return All events contained on this layer */
	public List<MapEvent> getEvents() { return events; }

	/**
	 * Adds another map event to this layer. Charas should be added from their
	 * own method.
	 * @param 	event			The map event to add
	 */
	public void add(MapEvent event) {
		if (event == null) {
			MGlobal.reporter.warn("Added a null object to the map?");
		} else {
			events.add(event);
		}
	}
	
	/**
	 * Removes a map object from this layer.
	 * @param 	event		The map object to remove
	 */
	public void remove(MapEvent event) {
		events.remove(event);
	}
	
	/**
	 * Checks if a map object exists on this layer.
	 * @param 	mapObject		The map object to check if exists
	 * @return					True if the object exists on this layer
	 */
	public boolean contains(MapThing mapObject) {
		return events.contains(mapObject);
	}
	
	/**
	 * Runs timestep integration until the hero moves again.
	 */
	public void integrate() {
		while (true) {
			Collections.sort(events, new Comparator<MapEvent>() {
				@Override
				public int compare(MapEvent a, MapEvent b) {
					int val = (a.ticksToAct() - b.ticksToAct());
					if (val == 0) {
						if (a == MGlobal.getHero()) {
							return 1;
						} else if (b == MGlobal.getHero()) {
							return -1;
						} else {
							return b.hashCode() - a.hashCode();
						}
					} else {
						return val;
					}
				}
			});
			MapEvent next = events.get(0);
			int ticks = next.ticksToAct();
			for (MapEvent event : events) {
				event.simulateTime(ticks);
			}
			//next.simulateTime(1);
			next.onTurn();
			if (next == MGlobal.getHero()) {
				break;
			}
		}
	}
	
	/**
	 * Fetches the first event named appropriately. Call from map.
	 * @param 	name			The string the event name has to match
	 * @return					The event with matching name, or null if none
	 */
	public MapEvent getEventByName(String name) {
		for (MapEvent event : events) {
			if (name.equals(event.getName())) {
				return event;
			}
		}
		return null;
	}
	
	/**
	 * Returns all events in specified group. Call from map.
	 * @param	groupName		The name of the group to fetch from
	 * @return					A list of all events in that group
	 */
	public List<MapEvent> getEventsByGroup(String groupName) {
		List<MapEvent> result = new ArrayList<MapEvent>();
		for (MapEvent event : events) {
			if (event.inGroup(groupName)) result.add(event);
		}
		return result;
	}
	
	/**
	 * Finds and returns all events at a given location.
	 * @param	tileX			The x-coord of the event to find, in tiles
	 * @param	tileY			The y-coord of the event to find, in tiles
	 * @return					All events there, or empty if none
	 */
	public List<MapEvent> getEventsAt(int tileX, int tileY) {
		List<MapEvent> results = new ArrayList<MapEvent>();
		for (MapEvent event : events) {
			if (event.getTileX() == tileX && event.getTileY() == tileY) {
				results.add(event);
			}
		}
		return results;
	}

}