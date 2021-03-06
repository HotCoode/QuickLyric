/*
 * *
 *  * This file is part of QuickLyric
 *  * Created by geecko
 *  *
 *  * QuickLyric is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * QuickLyric is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  * You should have received a copy of the GNU General Public License
 *  * along with QuickLyric.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.geecko.QuickLyric.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.geecko.QuickLyric.R;

import static android.graphics.PorterDuff.Mode.OVERLAY;

public class FadeInNetworkImageView extends NetworkImageView {
    private static final int FADE_IN_TIME_MS = 500;

    public FadeInNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Context context = getContext();
        if (context != null) {
            Resources resources = context.getResources();
            Bitmap diskBitmap =
                    ((BitmapDrawable) resources.getDrawable(R.drawable.base_cover)).getBitmap();
            int overlayColor = 0xFFF;
            if (bm != null) {
                Palette coverPalette = Palette.generate(bm);
//              int muted = coverPalette.getLightMutedColor(resources.getColor(R.color.selected));
                Palette.Swatch swatch = coverPalette.getVibrantSwatch();
                if (swatch == null || swatch.getPopulation() < 200) {
                    swatch = coverPalette.getLightMutedSwatch();
                    if (swatch == null || swatch.getPopulation() < 200)
                        swatch = coverPalette.getMutedSwatch();
                    if (swatch == null)
                        return;
                }
                // DST = Disk, SRC = Artwork
                overlayColor = swatch.getRgb();
                ColorFilter filter = new PorterDuffColorFilter(overlayColor, OVERLAY);
                diskBitmap = diskBitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas diskCanvas = new Canvas(diskBitmap);
                Paint paint = new Paint();
                paint.setColorFilter(filter);
                diskCanvas.drawBitmap(diskBitmap, 0f, 0f, paint);
            }
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                    new ColorDrawable(overlayColor),
                    new BitmapDrawable(context.getResources(), diskBitmap)
            });

            setImageDrawable(td);
            td.startTransition(FADE_IN_TIME_MS);
        }
    }

}