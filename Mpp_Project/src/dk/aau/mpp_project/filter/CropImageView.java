package dk.aau.mpp_project.filter;

import android.content.Context;
import android.graphics.Matrix;
import android.widget.ImageView;
 
/**
 * ImageView to display top-crop scale of an image view.
 *
 * @author Chris Arriola
 */
public class CropImageView extends ImageView {
 
    public CropImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }
 
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        final Matrix matrix = getImageMatrix();
            
        float scale;
        final int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        final int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        final int drawableWidth = getDrawable().getIntrinsicWidth();
        final int drawableHeight = getDrawable().getIntrinsicHeight();
        
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }
            
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
            
        return super.setFrame(l, t, r, b);
    }        
}