package com.cbb.butterknifelibrary;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a field to the view for the specified ID. The view will automatically be cast to the field
 * type.
 * <pre><code>
 * {@literal @}Bind(R.id.title) TextView title;
 * </code></pre>
 */
@Retention(CLASS)   // 保留的生命周期，仅class阶段有效
@Target(FIELD)      // 使用范围，用于描述域变量
public @interface Bind {
  /** View ID to which the field will be bound. */
  int[] value();
}
