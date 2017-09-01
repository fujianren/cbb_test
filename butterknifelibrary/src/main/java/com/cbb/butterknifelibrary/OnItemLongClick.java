package com.cbb.butterknifelibrary;

import android.view.View;

import com.cbb.butterknifelibrary.internal.ListenerClass;
import com.cbb.butterknifelibrary.internal.ListenerMethod;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static android.widget.AdapterView.OnItemLongClickListener;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Bind a method to an {@link OnItemLongClickListener OnItemLongClickListener} on the view for each
 * ID specified.
 * <pre><code>
 * {@literal @}OnItemLongClick(R.id.example_list) boolean onItemLongClick(int position) {
 *   Toast.makeText(this, "Long clicked position " + position + "!", Toast.LENGTH_SHORT).show();
 *   return true;
 * }
 * </code></pre>
 * Any number of parameters from
 * {@link OnItemLongClickListener#onItemLongClick(android.widget.AdapterView, View,
 * int, long) onItemLongClick} may be used on the method.
 *
 * @see OnItemLongClickListener
 */
@Target(METHOD)
@Retention(CLASS)
@ListenerClass(
    targetType = "android.widget.AdapterView<?>",
    setter = "setOnItemLongClickListener",
    type = "android.widget.AdapterView.OnItemLongClickListener",
    method = @ListenerMethod(
        name = "onItemLongClick",
        parameters = {
            "android.widget.AdapterView<?>",
            "android.view.View",
            "int",
            "long"
        },
        returnType = "boolean",
        defaultReturn = "false"
    )
)
public @interface OnItemLongClick {
  /** View IDs to which the method will be bound. */
  int[] value() default { View.NO_ID };
}
