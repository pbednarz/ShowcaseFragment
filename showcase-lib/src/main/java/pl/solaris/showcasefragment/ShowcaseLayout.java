package pl.solaris.showcasefragment;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by pbednarz on 2015-01-20.
 */
final class ShowcaseLayout extends RelativeLayout {
    private float centerX;
    private float centerY;
    private float radius;
    private float radiusScale;
    private Paint paintShadow;
    private ImageView showcaseTarget;
    private TextView showcaseTitle;
    private TextView showcaseDetails;
    private TextView showcaseDone;

    public ShowcaseLayout(Context context) {
        this(context, null);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.scf_style);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public ShowcaseLayout(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle);
        if (isInEditMode()) return;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_showcase, this, true);
        setClickable(true);
        showcaseTarget = (ImageView) findViewById(R.id.image);
        showcaseTitle = (TextView) findViewById(R.id.title);
        showcaseDetails = (TextView) findViewById(R.id.description);
        showcaseDone = (TextView) findViewById(R.id.done);

        //Load defaults from resources
        final Resources res = getResources();
        final int defaultShowCaseColor = res.getColor(R.color.scf_showcase_color);
        final int defaultShowCaseBgColor = res.getColor(R.color.scf_showcase_bg_color);
        final int defaultTintColor = res.getColor(R.color.scf_showcase_tint);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ShowcaseAttrs, defStyle, defStyleRes);
        paintShadow = new Paint();
        paintShadow.setAntiAlias(true);
        paintShadow.setStyle(Paint.Style.FILL);
        paintShadow.setColor(a.getColor(R.styleable.ShowcaseAttrs_circle_color, defaultShowCaseColor));
        Drawable background = a.getDrawable(R.styleable.ShowcaseAttrs_android_background);
        if (background != null) {
            setBackgroundDrawable(background);
        } else {
            setBackgroundColor(defaultShowCaseBgColor);
        }

        TypedValue typedValue = new TypedValue();
        getResources().getValue(R.dimen.scf_radius_scale, typedValue, true);
        radiusScale = typedValue.getFloat();
        showcaseTitle.setTextAppearance(context, a.getResourceId(R.styleable.ShowcaseAttrs_title_text_appearance, R.style.TextAppearance_AppCompat_Large_Inverse));
        showcaseDetails.setTextAppearance(context, a.getResourceId(R.styleable.ShowcaseAttrs_desc_text_appearance, R.style.TextAppearance_AppCompat_Small_Inverse));
        showcaseDone.setTextAppearance(context, a.getResourceId(R.styleable.ShowcaseAttrs_done_text_appearance, R.style.TextAppearance_AppCompat_Medium_Inverse));
        showcaseTarget.setColorFilter(a.getColor(R.styleable.ShowcaseAttrs_circle_tint, defaultTintColor));
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (radius > 0) {
            canvas.drawCircle(centerX, centerY, radius, paintShadow);
        }
        super.onDraw(canvas);
    }

    public void setCircleBounds(int width, int height, int top, int left) {
        centerX = left + width * 0.5f;
        centerY = top + height * 0.5f;
        radius = width * radiusScale * 0.5f;
    }

    public void setOnDoneClickListener(OnClickListener listener) {
        showcaseDone.setOnClickListener(listener);
    }

    public void setData(String title, String description, String done) {
        showcaseTitle.setText(title);
        showcaseDetails.setText(description);
        showcaseDone.setText(done);
    }

    public ImageView getTargetSpot() {
        return showcaseTarget;
    }
}