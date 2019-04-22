package app.cloud.vrajinc.v_cloud;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewer_Zoomed {
    public void display(Context context, ImageView imageView,String image_url)
    {
        String SERVER_URL ="http://vrajinc.ddns.net:8080";
        BitmapDrawable background = ImagePreviewerUtils.getBlurredScreenDrawable(context,imageView.getRootView());
        View zoomed_view = (View) LayoutInflater.from(context).inflate(R.layout.zoomed_image_layout,null);
        ImageView zoomed_image = zoomed_view.findViewById(R.id.zoomed_image_view);
        Picasso.get().load(SERVER_URL+image_url).into(zoomed_image);
        final Dialog zoomed_image_dialog = new Dialog(context,R.style.ImagePreviewerTheme);
        zoomed_image_dialog.getWindow().setBackgroundDrawable(background);
        zoomed_image_dialog.setContentView(zoomed_view);
        zoomed_image_dialog.show();
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(zoomed_image_dialog.isShowing())
                {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    int action = event.getActionMasked();
                    if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
                    {
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        zoomed_image_dialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
