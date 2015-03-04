/**
 *  DataLoader.java
 *  Created on Feb 4, 2013 5:54:33 PM for project rainfall-libgdx
 *  Author: psy_wombats
 *  Contact: psy_wombats@wombatrpgs.net
 */
package net.wombatrpgs.sdrl2015.io.loaders;

import net.wombatrpgs.sdrl2015.core.DataEntry;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

/**
 * This is one of those classes built to replace something deprecated in the
 * first iteration of MGNE. This one plugs into the asset manager to replace
 * the file loader and directory loader and that shit. This is meant to load an
 * individual data file from within the bowels of the data directory.
 */
public class DataLoader extends SynchronousAssetLoader<DataEntry, DataLoader.DataParameter> {
	
	public DataLoader (FileHandleResolver resolver) {
		super(resolver);
	}
	
	static public class DataParameter extends AssetLoaderParameters<DataEntry> {
		
	}

	@Override
	public DataEntry load(AssetManager assetManager, String fileName, FileHandle file, DataParameter parameter) {
		return new DataEntry(resolve(fileName));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, DataParameter parameter) {
		return null;
	}

}
