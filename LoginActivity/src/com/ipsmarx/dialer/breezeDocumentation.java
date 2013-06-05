package com.ipsmarx.dialer;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.SOURCE)
@interface breezeDocumentation
{
  public abstract String Author();

  public abstract String Param();

  public abstract String description();
}

/* Location:           C:\Users\Maidul\Desktop\New folder\New folder\classes_dex2jar.jar
 * Qualified Name:     com.ipsmarx.dialer.breezeDocumentation
 * JD-Core Version:    0.6.2
 */