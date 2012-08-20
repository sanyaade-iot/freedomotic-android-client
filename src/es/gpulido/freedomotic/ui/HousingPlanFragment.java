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
package es.gpulido.freedomotic.ui;

import it.freedomotic.model.environment.Environment;
import it.freedomotic.model.environment.Zone;
import it.freedomotic.model.geometry.FreedomPolygon;
import it.freedomotic.model.object.EnvObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import es.gpulido.freedomotic.R;
import es.gpulido.freedomotic.api.EnvironmentController;
import es.gpulido.freedomotic.api.FreedomoticController;
import es.gpulido.freedomotic.ui.base.SelectableObjectFragment;
import es.gpulido.freedomotic.util.DrawableElement;
import es.gpulido.freedomotic.util.DrawableObject;
import es.gpulido.freedomotic.util.DrawableRoom;
import es.gpulido.freedomotic.util.DrawingUtils;

@TargetApi(12) public class HousingPlanFragment extends SelectableObjectFragment implements
		Observer {

	InternalView myview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		myview = new InternalView(getSherlockActivity());
		return myview;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.housingplan, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_fit_on_screen) {
			myview.fitToScreen();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void update(Observable observable, Object data) {
		if (observable.equals(EnvironmentController.getInstance()))
			myview.postInvalidate();
		else
			myview.initialize();

	}

	@Override
	public void onResume() {
		super.onResume();
		EnvironmentController.getInstance().addObserver(this);
	};

	@Override
	public void onPause() {
		EnvironmentController.getInstance().deleteObserver(this);
		super.onPause();
	};

	// /////////////////////////////////////////////////

	private class InternalView extends View {
		Path envPath = new Path(); // the enviroment path that is going to be
									// draw.

		HashMap<String, Bitmap> cachedObjBitmap = new HashMap<String, Bitmap>();
		ArrayList<DrawableRoom> drawingRooms = new ArrayList<DrawableRoom>();
		ArrayList<DrawableObject> drawingObjects = new ArrayList<DrawableObject>();

		Paint mPaint;
		Paint environmentPaint;
		Paint environmentPaint2;
		Paint environmentShadowPaint;

		private float mPosX;
		private float mPosY;

		private float mLastTouchX;
		private float mLastTouchY;

		private DrawableElement mLastElementTouch;
		// Flag to only transform objects once in the onDraw
		boolean transformed = false;

		private static final int INVALID_POINTER_ID = -1;

		// The ‘active pointer’ is the one currently moving our object.
		private int mActivePointerId = INVALID_POINTER_ID;
		private ScaleGestureDetector mScaleDetector;
		private float mScaleFactor = 1.f;

		private static final int MARGIN = 20;
		Bitmap lookup;
		Canvas ghostCanvas;
		boolean mInitialized = false;

		public InternalView(Context context) {
			super(context);

			// Create our ScaleGestureDetector
			mScaleDetector = new ScaleGestureDetector(context,
					new ScaleListener());
			initialize();
		}

		protected void initialize() {
			// TODO: solve the bug when env is null because the environment is
			// not yet loaded

			if (EnvironmentController.getInstance().getEnvironment() != null) {

				Environment env = EnvironmentController.getInstance()
						.getEnvironment();
				FreedomPolygon poly = env.getShape();
				envPath = DrawingUtils.freedomPolygonToPath(poly);

				// Create all Paints to be used
				mPaint = new Paint();
				mPaint.setStrokeWidth(7.0f); // 7 pixel line width. TODO: Maybe
				// we need to apply the scale
				mPaint.setColor(Color.DKGRAY); // tealish with no transparency
				mPaint.setStyle(Paint.Style.STROKE); // stroked, aka a line with
				// no fill
				mPaint.setAntiAlias(true);

				environmentShadowPaint = new Paint();
				environmentShadowPaint.setStrokeWidth(50.0f);
				environmentShadowPaint.setColor(Color.BLACK);
				environmentShadowPaint.setAlpha(127);
				environmentShadowPaint.setStyle(Paint.Style.STROKE);
				environmentShadowPaint.setAntiAlias(true);

				environmentPaint = new Paint();
				environmentPaint.setStrokeWidth(33.0f);
				environmentPaint.setColor(Color.WHITE);
				environmentPaint.setStyle(Paint.Style.STROKE);
				environmentPaint.setAntiAlias(true);

				environmentPaint2 = new Paint();
				environmentPaint2.setStrokeWidth(30.0f);
				environmentPaint2.setColor(Color.LTGRAY);
				environmentPaint2.setStyle(Paint.Style.STROKE);
				environmentPaint2.setAntiAlias(true);

				Paint textPaint = new Paint();
				textPaint.setStrokeWidth(1); // 7 pixel line width.
				textPaint.setTextSize(20);
				textPaint.setColor(Color.BLACK); // tealish with no transparency
				textPaint.setStyle(Paint.Style.STROKE); // stroked, aka a line
				// with no fill
				textPaint.setAntiAlias(true);

				// create all drawingrooms
				for (Zone r : EnvironmentController.getInstance().getRooms()) {
					drawingRooms.add(new DrawableRoom(r, mPaint, textPaint));
					// TODO: Take care of the objects not in room
					for (EnvObject obj : r.getObjects()) {
						drawingObjects.add(new DrawableObject(obj));

					}
				}
				// create all drawingObjects
				// for (EnvObject obj:
				// EnvironmentController.getInstance().getObjects())
				// {
				// drawingObjects.add(new DrawableObject(obj));
				// }
				mInitialized = true;
			} else {

			}

		}

		// Adapt the "original coordinates" from freedom to the android device
		// size
		public void fitToScreen() {
			int actionBarHeight = 0;// getSherlockActivity().getActionBar().getHeight();
			int xSize = getWidth() - MARGIN * 2;
			int ySize = getHeight() - MARGIN * 2 - actionBarHeight;

			RectF pathBounds = new RectF();
			envPath.computeBounds(pathBounds, true);
			float xPathSize = pathBounds.width();
			float yPathSize = pathBounds.height();

			float xScale = xSize / xPathSize;
			float yScale = ySize / yPathSize;
			if (xScale < yScale)
				mScaleFactor = xScale;
			else
				mScaleFactor = yScale;
			mPosX = MARGIN;
			mPosY = MARGIN + actionBarHeight;
			invalidate();

		}

		public void centerTo(float x, float y) {
			// int xSize =getWidth()-MARGIN*2;
			// int ySize =getHeight()-MARGIN*2;
			// int xSizeCenter = xSize/2;
			// int ySizeCenter = xSize/2;
			mPosX = x;
			mPosY = y;
			invalidate();
		}

		// Map of objects that are drawed
		SparseArray<DrawableElement> objectsIndex;
		// Color that is used to represent no painting in the ghostCanvas
		int eraseColor = Color.BLACK;

		@SuppressLint("NewApi") void createGhostCanvas(Canvas canvas) {
			//TODO: The objects index shouldn't be created always.
			objectsIndex = new SparseArray<DrawableElement>();
			lookup = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(),
					Bitmap.Config.ARGB_8888);
			lookup.setHasAlpha(true);
			// //do this so that regions outside any path have a default
			// //path index of 255
			lookup.eraseColor(eraseColor);
			ghostCanvas = new Canvas(lookup);
			ghostCanvas.translate(mPosX, mPosY);
			ghostCanvas.scale(mScaleFactor, mScaleFactor);

		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if (mInitialized) {
				// TODO: figure out how to know the scale before the draw
				if (!transformed) {
					fitToScreen();
					transformed = true;
				}
				canvas.save();
				canvas.translate(mPosX, mPosY);
				canvas.scale(mScaleFactor, mScaleFactor);
				createGhostCanvas(canvas);
				renderEnvironment(canvas);
				renderRooms(canvas);
				renderObjects(canvas);
//				ListView lview = new ListView(this.getContext());
//				lview.draw(canvas);
				canvas.restore();
			} else {
				// initialize();

			}
		}

		public void renderEnvironment(Canvas canvas) {
			canvas.drawPath(envPath, environmentPaint);
			canvas.drawPath(envPath, environmentPaint2);
			canvas.drawPath(envPath, mPaint);

		}

		public void renderRooms(Canvas canvas) {
			for (DrawableRoom dr : drawingRooms) {
				dr.drawGhost(ghostCanvas);
				objectsIndex.put(dr.getIndexColor(), dr);
				dr.draw(canvas);
			}
		}

		public void renderObjects(Canvas canvas) {
			for (DrawableObject dobj : drawingObjects) {
				dobj.draw(canvas);
				dobj.drawGhost(ghostCanvas);
				objectsIndex.put(dobj.getIndexColor(), dobj);
			}

		}

		@SuppressLint({ "NewApi"}) @Override
		public boolean onTouchEvent(MotionEvent ev) {
			// Let the ScaleGestureDetector inspect all events.
			mScaleDetector.onTouchEvent(ev);

			final int action = ev.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN: {
				final float x = ev.getX();
				final float y = ev.getY();

				mLastTouchX = x;
				mLastTouchY = y;
				mActivePointerId = ev.getPointerId(0);
				int pathIndex = lookup.getPixel(Math.round(x), Math.round(y));
				if (pathIndex != Color.BLACK) {
					mLastElementTouch = objectsIndex.get(pathIndex);

				} else {
					mLastElementTouch = null;
				}
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				final int pointerIndex = ev.findPointerIndex(mActivePointerId);
				final float x = ev.getX(pointerIndex);
				final float y = ev.getY(pointerIndex);

				// Only move if the ScaleGestureDetector isn't processing a
				// gesture.
				if (!mScaleDetector.isInProgress()) {
					final float dx = x - mLastTouchX;
					final float dy = y - mLastTouchY;

					mPosX += dx;
					mPosY += dy;

					invalidate();
				}

				mLastTouchX = x;
				mLastTouchY = y;

				break;
			}

			case MotionEvent.ACTION_UP: {
				mActivePointerId = INVALID_POINTER_ID;
				if (mLastElementTouch != null) {
					// we have touch an object
					if (mLastElementTouch instanceof DrawableObject) {

						mListener.onObjectSelected(
								((DrawableObject) mLastElementTouch)
										.getEnvObject().getName(), null);
						// centerTo( mLastTouchX,mLastTouchY);
					}
				}
				mLastElementTouch = null;
				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				mActivePointerId = INVALID_POINTER_ID;
				mLastElementTouch = null;
				break;
			}

			case MotionEvent.ACTION_POINTER_UP: {
				final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = ev.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId) {
					// This was our active pointer going up. Choose a new
					// active pointer and adjust accordingly.
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mLastTouchX = ev.getX(newPointerIndex);
					mLastTouchY = ev.getY(newPointerIndex);
					mActivePointerId = ev.getPointerId(newPointerIndex);
				}
				break;
			}
		}

			return true;
		}

		private class ScaleListener extends
				ScaleGestureDetector.SimpleOnScaleGestureListener {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				mScaleFactor *= detector.getScaleFactor();

				// Don't let the object get too small or too large.
				mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

				invalidate();
				return true;
			}
		}
	}

}
