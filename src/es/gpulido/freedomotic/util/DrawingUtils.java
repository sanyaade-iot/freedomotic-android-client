/*******************************************************************************
 * Copyright (c) 2011 Gabriel Pulido.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Gabriel Pulido - initial API and implementation
 ******************************************************************************/
package es.gpulido.freedomotic.util;

import java.util.HashMap;

import it.freedomotic.model.geometry.FreedomPoint;
import it.freedomotic.model.geometry.FreedomPolygon;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Path;

public class DrawingUtils {

	public static HashMap<String,Bitmap> roomsBitmapsCache = new HashMap<String,Bitmap>();
		
		//Helper class to transform from a FreedomPolygon to a Path
	public static Path freedomPolygonToPath(FreedomPolygon fp)
	{
		Path mP = new Path();
		for (int j = 0; j < fp.getPoints().size(); j++) {
			FreedomPoint point= fp.getPoints().get(j);
			if (j== 0)
				mP.moveTo(point.getX(), point.getY());
			else
				mP.lineTo(point.getX(), point.getY());					  
		}		  		  
		//closing the path
		mP.close();
	
		return mP;
	}
	public static float nextValidColor=0xFF000001;
	private static int redValue=0;
	private static int greenValue=0;
	private static int blueValue=0;
	
	public static int generateNextValidColor()
	{				
		float step = 0x00000001;
		nextValidColor+=step;		
		return (int)nextValidColor;
		
	}
	
	
	
	
	
}
