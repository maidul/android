package com.ipsmarx.dialer;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class ButtonGridLayout extends ViewGroup
{
  private static final int COLUMNS = 3;
  private static final int NUM_CHILDREN = 12;
  private static final int ROWS = 4;
  private int mButtonHeight;
  private int mButtonWidth;
  private View[] mButtons = new View[12];
  private int mHeight;
  private int mHeightInc;
  private int mPaddingBottom = 0;
  private int mPaddingLeft = 6;
  private int mPaddingRight = 6;
  private int mPaddingTop = 0;
  private int mWidth;
  private int mWidthInc;

  public ButtonGridLayout(Context paramContext)
  {
    super(paramContext);
  }

  public ButtonGridLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public ButtonGridLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  protected void onFinishInflate()
  {
    super.onFinishInflate();
    View[] arrayOfView = this.mButtons;
    for (int i = 0; ; i++)
    {
      if (i >= 12)
      {
        if (((0xF & getResources().getConfiguration().screenLayout) == 3) || ((0xF & getResources().getConfiguration().screenLayout) == 4))
        {
          this.mPaddingBottom = 10;
          this.mPaddingLeft = 10;
          this.mPaddingRight = 10;
          this.mPaddingTop = 10;
        }
        View localView = arrayOfView[0];
        this.mButtonWidth = localView.getMeasuredWidth();
        this.mButtonHeight = localView.getMeasuredHeight();
        this.mWidthInc = (this.mButtonWidth + this.mPaddingLeft + this.mPaddingRight);
        this.mHeightInc = (this.mButtonHeight + this.mPaddingTop + this.mPaddingBottom);
        this.mWidth = (3 * this.mWidthInc);
        this.mHeight = (4 * this.mHeightInc);
        return;
      }
      arrayOfView[i] = getChildAt(i);
      arrayOfView[i].measure(0, 0);
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View[] arrayOfView = this.mButtons;
    int i = this.mPaddingLeft;
    int j = this.mButtonWidth;
    int k = this.mButtonHeight;
    int m = this.mWidthInc;
    int n = this.mHeightInc;
    int i1 = 0;
    int i2 = paramInt4 - paramInt2 - this.mHeight + this.mPaddingTop;
    int i3 = 0;
    if (i3 >= 4)
      return;
    int i4 = i;
    for (int i5 = 0; ; i5++)
    {
      if (i5 >= 3)
      {
        i2 += n;
        i3++;
        break;
      }
      arrayOfView[i1].layout(i4, i2, i4 + j, i2 + k);
      i4 += m;
      i1++;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    setMeasuredDimension(resolveSize(this.mWidth, paramInt1), resolveSize(this.mHeight, paramInt2));
  }

  public void setChildrenBackgroundResource(int paramInt)
  {
    View[] arrayOfView = this.mButtons;
    for (int i = 0; ; i++)
    {
      if (i >= 12)
        return;
      arrayOfView[i].setBackgroundResource(paramInt);
    }
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ButtonGridLayout
 * JD-Core Version:    0.6.2
 */