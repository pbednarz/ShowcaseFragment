package pl.solaris.showcasefragment;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by pbednarz on 2015-01-20.
 */
public class ShowcaseFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_STYLE_ID = "style_id";
    private static final String ARG_VIEW_ID = "view_id";
    private static final String ARG_SC_TITLE = "sc_title";
    private static final String ARG_SC_DESC = "sc_desc";
    private static final String ARG_SC_OK = "sc_ok";
    private ShowcaseLayout root;

    public static void startShowcase(final FragmentActivity activity,
                                     final View targetView,
                                     String title,
                                     String description,
                                     String ok) {
        startShowcase(activity, targetView, title, description, ok, 0);
    }

    public static void startShowcase(final FragmentActivity activity,
                                     final View targetView,
                                     String title,
                                     String description,
                                     String ok,
                                     @StyleRes int style) {
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction();
        ft.add(android.R.id.content, ShowcaseFragment.newInstance(targetView, title, description, ok, style));
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static ShowcaseFragment newInstance(View targetView,
                                               String title,
                                               String description,
                                               String ok,
                                               int style) {
        ShowcaseFragment fragment = new ShowcaseFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_VIEW_ID, targetView.getId());
        args.putString(ARG_SC_TITLE, title);
        args.putInt(ARG_STYLE_ID, style);
        args.putString(ARG_SC_DESC, description);
        args.putString(ARG_SC_OK, ok);
        fragment.setArguments(args);
        return fragment;
    }

    private static Bitmap loadBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache(true));
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int styleId = getArguments().getInt(ARG_STYLE_ID, 0);
        root = new ShowcaseLayout(getActivity()
                , null
                , ((styleId > 0) ? 0 : R.attr.scf_style),
                styleId);
        root.setOnDoneClickListener(this);
        root.setData(getArguments().getString(ARG_SC_TITLE, ""),
                getArguments().getString(ARG_SC_DESC, ""),
                getArguments().getString(ARG_SC_OK, ""));
        return root;
    }

    @Override
    public void onDestroyView() {
        root = null;
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
        getTargetView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getTargetView();
    }

    private void getTargetView() {
        if (getActivity() != null) {
            final View view = getActivity().findViewById(getArguments().getInt(ARG_VIEW_ID));
            if (view != null) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        show(view);
                        return false;
                    }
                });
            }
        }
    }

    private void show(View targetView) {
        if (targetView != null) {
            Bitmap bmp = loadBitmapFromView(targetView);
            ImageView targetSpot = root.getTargetSpot();
            targetSpot.setImageBitmap(bmp);
            int imageWidth = bmp.getWidth();
            int imageHeight = bmp.getHeight();
            View windowRoot = getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            int[] viewLocation = new int[2];
            int[] viewLocationDestination = new int[2];
            targetView.getLocationOnScreen(viewLocation);
            windowRoot.getLocationOnScreen(viewLocationDestination);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) targetSpot.getLayoutParams();
            params.leftMargin = viewLocation[0] - viewLocationDestination[0];
            params.topMargin = viewLocation[1] - viewLocationDestination[1];
            targetSpot.setLayoutParams(params);
            root.setCircleBounds(imageWidth, imageHeight, params.topMargin, params.leftMargin);
        }
    }

    @Override
    public void onClick(View v) {
        try {
            ((ShowcaseListener) getActivity()).onShowcaseClose();
        } catch (ClassCastException ignored) {
            getActivity().onBackPressed();
        }
    }

    public static interface ShowcaseListener {
        public void onShowcaseClose();
    }
}
