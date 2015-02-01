/**
 *  BaconEventFactory.java
 *  Created on Jan 15, 2015 1:04:10 AM for project bacon01-desktop
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.bacon01.maps.events;

import net.wombatrpgs.baconschema.events.EventLightMDO;
import net.wombatrpgs.mgne.maps.TiledMapObject;
import net.wombatrpgs.mgne.maps.events.EventFactory;
import net.wombatrpgs.mgne.maps.events.MapEvent;
import net.wombatrpgs.mgneschema.maps.EventMDO;

/**
 * Generates bacon-specific events.
 */
public class BaconEventFactory extends EventFactory {
	
	protected static final String TYPE_LIGHT = "light";
	protected static final String TYPE_GEOMETRY = "geometry";
	protected static final String TYPE_WARPER = "warper";
	protected static final String TYPE_CHECKPOINT = "checkpoint";
	protected static final String TYPE_STALKER = "stalker";
	protected static final String TYPE_ITEM = "item";

	/**
	 * @see net.wombatrpgs.mgne.maps.events.EventFactory#createEvent
	 * (net.wombatrpgs.mgne.maps.TiledMapObject)
	 */
	@Override
	protected MapEvent createEvent(TiledMapObject object) {
		String type = object.getString(TiledMapObject.PROPERTY_TYPE);
		if (TYPE_LIGHT.equals(type)) {
			return new EventLight(object.generateMDO(EventLightMDO.class), object);
		} else if (TYPE_GEOMETRY.equals(type)) {
			return new EventGeometry(object.generateMDO(EventMDO.class), object);
		} else if (TYPE_WARPER.equals(type)) {
			return new EventWarper(object.generateMDO(EventMDO.class), object);
		} else if (TYPE_CHECKPOINT.equals(type)) {
			return new EventCheckpoint(object.generateMDO(EventMDO.class), object);
		} else if (TYPE_STALKER.equals(type)) {
			return new EventStalker(object.generateMDO(EventMDO.class));
		} else if (TYPE_ITEM.equals(type)) {
			return new EventItem(object.getString("key"), object);
		}
		return super.createEvent(object);
	}
}
