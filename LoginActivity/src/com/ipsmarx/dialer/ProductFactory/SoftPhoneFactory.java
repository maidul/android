package com.ipsmarx.dialer.ProductFactory;

import android.content.Context;

public class SoftPhoneFactory
{
  static SoftPhone currentProduct;
  private static SoftPhoneFactory instance = null;

  public static SoftPhoneFactory getInstance()
  {
    if (instance == null)
      instance = new SoftPhoneFactory();
    return instance;
  }

  public SoftPhone getCurrentProduct()
  {
    return currentProduct;
  }

  public SoftPhone getSoftPhone(String paramString, Context paramContext)
  {
    if (paramString.equalsIgnoreCase("Sandals"))
      return new SandalsPhone();
    if (paramString.equalsIgnoreCase("IPsmarx"))
      return new GenericPhone(paramContext);
    if (paramString.equalsIgnoreCase("Breeze"))
      return new BrandedPhone(paramContext);
    return null;
  }

  public void setCurrentProduct(String paramString, Context paramContext)
  {
    boolean bool = paramString.equalsIgnoreCase("Sandals");
    Object localObject = null;
    if (bool)
      localObject = new SandalsPhone();
    if (paramString.equalsIgnoreCase("IPsmarx"))
      localObject = new GenericPhone(paramContext);
    if (paramString.equalsIgnoreCase("Breeze"))
      localObject = new BrandedPhone(paramContext);
    currentProduct = (SoftPhone)localObject;
  }
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.ProductFactory.SoftPhoneFactory
 * JD-Core Version:    0.6.2
 */