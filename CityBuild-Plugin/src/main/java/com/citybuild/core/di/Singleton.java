package com.citybuild.core.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @Singleton - Marks a class or binding as a singleton
 * The DI container will create only one instance
 * If not marked, a new instance is created per injection
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Singleton {
}
