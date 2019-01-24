package com.GetScreen;

import java.awt.image.BufferedImage;

public class FBImage extends BufferedImage
{
  private int mRawWidth;
  private int mRawHeight;

  public FBImage(int width, int height, int imageType, int rawWidth, int rawHeight)
  {
    super(width, height, imageType);
    this.mRawWidth = rawWidth;
    this.mRawHeight = rawHeight;
  }

  public int getRawWidth() {
    return this.mRawWidth;
  }

  public int getRawHeight() {
    return this.mRawHeight;
  }
}