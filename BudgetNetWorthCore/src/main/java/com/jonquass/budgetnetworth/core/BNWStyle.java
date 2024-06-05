package com.jonquass.budgetnetworth.core;

import org.immutables.value.Value;
import org.immutables.value.Value.Immutable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.CLASS) // Make it class retention for incremental compilation
@Value.Style(
        get = {"is*", "get*"}, // Detect 'get' and 'is' prefixes in accessor methods
        init = "set*", // Builder initialization methods will have 'set' prefix
        typeAbstract = {"*IF"}, // 'Abstract' prefix will be detected and trimmed
        typeImmutable = "*", // No prefix or suffix for generated immutable type
        visibility = Value.Style.ImplementationVisibility.PUBLIC, // Generated class will be always public
        defaults = @Immutable(copy = false)) // Disable copy methods by default
public @interface BNWStyle {
}
