package com.example.studentsmatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Loads a profile picture from a file path.
 * - Shrinks big camera photos so they don't crash the app ("bitmap too large").
 * - Rotates the photo upright according to the camera's EXIF orientation.
 * - Returns null when there is no usable picture (no path, missing or empty file).
 */
public class ProfilePic {

    private static final int TARGET_SIZE_PX = 512;

    public static Bitmap load(String path) {
        if (path == null || path.isEmpty()) return null;
        File file = new File(path);
        if (!file.exists() || file.length() == 0) {
            Log.d("StudentsMatch", "no profile pic file: " + path);
            return null;
        }

        // Read only the size first, then decode a smaller copy.
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bounds);
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null;

        int sample = 1;
        while (bounds.outWidth / (sample * 2) >= TARGET_SIZE_PX
                && bounds.outHeight / (sample * 2) >= TARGET_SIZE_PX) {
            sample *= 2;
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = sample;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
        if (bitmap == null) return null;

        int degrees = 0;
        try {
            int orientation = new ExifInterface(path).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) degrees = 90;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) degrees = 180;
            else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) degrees = 270;
        } catch (IOException e) {
            Log.d("StudentsMatch", "could not read photo orientation: " + e);
        }
        if (degrees != 0) {
            Matrix m = new Matrix();
            m.postRotate(degrees);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        }
        return bitmap;
    }

    /** Deletes a photo file the camera never filled (e.g. the user pressed back). */
    public static void deleteIfEmpty(String path) {
        if (path == null || path.isEmpty()) return;
        File file = new File(path);
        if (file.exists() && file.length() == 0) file.delete();
    }
}
