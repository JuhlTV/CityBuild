package com.citybuild.core.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @Inject - Marks a field or constructor parameter for dependency injection
 * The DI container will automatically populate these fields
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    /**
     * Optional qualifier for ambiguous dependencies
     */
    String value() default "";
}
